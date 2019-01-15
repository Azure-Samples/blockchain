package net.corda.workbench.basicProvenance.flow

import co.paralleluniverse.fibers.Suspendable
import net.corda.workbench.basicProvenance.contract.ItemContract
import net.corda.workbench.basicProvenance.state.ItemState
import net.corda.core.contracts.Command
import net.corda.core.contracts.Requirements.using
import net.corda.core.contracts.StateAndContract
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class CreateFlow(private val _state:ItemState) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {

        val notary: Party = serviceHub.networkMapCache.notaryIdentities.first()
        val createCommand = Command(ItemContract.Create(), listOf(ourIdentity.owningKey))
        val builder = TransactionBuilder(notary = notary)
                .addOutputState(_state, ItemContract.ITEM_ID)
                .addCommand(createCommand)

        val stx = serviceHub.signInitialTransaction(builder)
        val ftx = subFlow(FinalityFlow(stx))

        subFlow(ObserverFlow(_state.Observer, ftx))
        return ftx
    }

    @InitiatedBy(CreateFlow::class)
    class CreateFlowResponder(val flowSession: FlowSession) : FlowLogic<Unit>() {
        @Suspendable
        override fun call() {
            val signedTransactionFlow = object : SignTransactionFlow(flowSession) {
                override fun checkTransaction(stx: SignedTransaction) = requireThat {
                    val output = stx.tx.outputs.single().data
                    "This must be a simple creation " using (output is ItemState)
                }
            }
            subFlow(signedTransactionFlow)
        }

    }
}