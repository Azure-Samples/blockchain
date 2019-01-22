package net.corda.workbench.chat.flow

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.workbench.chat.ChatContract
import net.corda.workbench.chat.Message

@InitiatingFlow
@StartableByRPC
class EndChatFlow(private val otherParty: Party, private val linearId: UniqueIdentifier = UniqueIdentifier()) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {

        // simplest way of finding a notary
        val notary = serviceHub.networkMapCache.notaryIdentities.first()

        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(linearId))
        val items = serviceHub.vaultService.queryBy<Message>(queryCriteria).states
        if (items.isEmpty()) {
            throw IllegalArgumentException("Cannot find a message for $linearId")
        }
        val currentMessage = items.single()


        val endMessage = Message(message = "bye", interlocutorA = ourIdentity, interlocutorB = otherParty, linearId = linearId)


        // build txn
        val participants = listOf(ourIdentity, otherParty)
        val cmd = Command(ChatContract.Commands.Bye(), participants.map { it -> it.owningKey })
        val builder = TransactionBuilder(notary = notary)
                .addInputState(currentMessage)
                .addOutputState(endMessage, ChatContract.ID)
                .addCommand(cmd)

        // verify and sign
        builder.verify(serviceHub)
        val ptx = serviceHub.signInitialTransaction(builder)

        // make sure everyone else signs
        val signers = listOf(otherParty)
        val sessions = signers.map { initiateFlow(it) }
        val stx = subFlow(CollectSignaturesFlow(ptx, sessions))

        // complete and notarise
        return subFlow(FinalityFlow(stx))

    }
}

/**
 *
 */
@InitiatedBy(EndChatFlow::class)
class EndChatFlowResponder(val flowSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val signedTransactionFlow = object : SignTransactionFlow(flowSession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
                "This must be a message " using (output is Message)
            }
        }
        subFlow(signedTransactionFlow)
    }
}