package net.corda.workbench.refrigeratedTransportation.flow

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.Command
import net.corda.core.contracts.requireThat
import net.corda.core.flows.CollectSignaturesFlow
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.SignTransactionFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.workbench.refrigeratedTransportation.RefrigerationContract
import net.corda.workbench.refrigeratedTransportation.Shipment

@InitiatingFlow
@StartableByRPC
class CreateFlow(private val state: Shipment) : FlowLogic<SignedTransaction>() {

    override val progressTracker: ProgressTracker = CreateFlow.tracker()

    @Suspendable
    override fun call(): SignedTransaction {
        progressTracker.currentStep = FINDING_NOTARY

        // simplest way of finding a notary
        val notary = serviceHub.networkMapCache.notaryIdentities.first()

        // build txn
        progressTracker.currentStep = CREATING_TXN
        val cmd = Command(RefrigerationContract.Commands.Create(), state.participants.map { it -> it.owningKey })
        val builder = TransactionBuilder(notary = notary)
                .addOutputState(state, RefrigerationContract.ID)
                .addCommand(cmd)

        // verify and sign
        builder.verify(serviceHub)
        val ptx = serviceHub.signInitialTransaction(builder)

        // make sure everyone else signs
        progressTracker.currentStep = COLLECTING_SIGNATURES
        val signers = (state.participants - ourIdentity)
        val sessions = signers.map { initiateFlow(it) }
        val stx = subFlow(CollectSignaturesFlow(ptx, sessions))

        // complete and notarise
        progressTracker.currentStep = NOTARISING
        val finalTx = subFlow(FinalityFlow(stx))

        // let the observer know
        progressTracker.currentStep = REPORTING
        subFlow(ReportToObserverFlow(state.supplyChainObserver, finalTx))

        progressTracker.currentStep = COMPLETED
        return finalTx
    }

    companion object {
        object FINDING_NOTARY : ProgressTracker.Step("Locating the Notary")
        object CREATING_TXN : ProgressTracker.Step("Building and verify the txn")
        object COLLECTING_SIGNATURES : ProgressTracker.Step("Collect signatures from all parties")
        object NOTARISING : ProgressTracker.Step("With Notary")
        object REPORTING : ProgressTracker.Step("Reporting finalized txn to observer(s)")
        object COMPLETED : ProgressTracker.Step("All done")

        fun tracker() = ProgressTracker(FINDING_NOTARY, CREATING_TXN, COLLECTING_SIGNATURES, NOTARISING, REPORTING, COMPLETED)
    }
}

/**
 *
 */
@InitiatedBy(CreateFlow::class)
class CreateFlowResponder(val flowSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val signedTransactionFlow = object : SignTransactionFlow(flowSession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
                "This must be a simple marketplace transaction" using (output is Shipment)
            }
        }
        subFlow(signedTransactionFlow)
    }
}