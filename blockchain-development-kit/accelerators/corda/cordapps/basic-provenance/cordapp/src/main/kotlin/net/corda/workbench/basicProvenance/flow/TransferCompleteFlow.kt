package net.corda.workbench.basicProvenance.flow

import co.paralleluniverse.fibers.Suspendable
import net.corda.workbench.basicProvenance.contract.ItemContract
import net.corda.workbench.basicProvenance.state.ItemState
import net.corda.core.contracts.Command
import net.corda.core.contracts.Requirements.using
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class TransferCompleteFlow(private val linearId: UniqueIdentifier, private val newcounterParty: Party) : FlowLogic<SignedTransaction>() {



    @Suspendable
    override fun call(): SignedTransaction {

        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(linearId))
        val items = serviceHub.vaultService.queryBy<ItemState>(queryCriteria).states


        val inputStateAndRef = items.first()
        val _state = inputStateAndRef.state.data
        val outputstate = _state.transferComplete(newcounterParty,_state)
        val createCommand = Command(ItemContract.TransferComplete(), listOf(ourIdentity.owningKey))
        val builder = TransactionBuilder(notary = notary)
                .addInputState(inputStateAndRef)
                .addOutputState(outputstate, ItemContract.ITEM_ID)
                .addCommand(createCommand)

        val stx = serviceHub.signInitialTransaction(builder)
        val otherPartyFlow = initiateFlow(newcounterParty)
        val ftx = subFlow(CollectSignaturesFlow(stx, setOf(otherPartyFlow)))

        subFlow(ObserverFlow(outputstate.Observer, ftx))
        return subFlow(FinalityFlow(ftx))

    }


}


@InitiatedBy(TransferCompleteFlow::class)
class TransferCompleteFlowResponder(val flowSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val signedTransactionFlow = object : SignTransactionFlow(flowSession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
                "This must be a simple transaction" using (output is ItemState)
            }
        }
        subFlow(signedTransactionFlow)
    }
}