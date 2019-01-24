package net.corda.workbench.basicProvenance.flow.workbench

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.reflections.workbench.TxnResult
import net.corda.workbench.basicProvenance.flow.TransferCompleteFlow

@InitiatingFlow
@StartableByRPC
class WorkbenchTransferCompleteFlow(private val linearId: UniqueIdentifier,
                            private val newcounterParty: Party) : FlowLogic<TxnResult>() {

    @Suspendable
    override fun call(): TxnResult {
        val txn = subFlow(TransferCompleteFlow(linearId,newcounterParty))
        return buildWorkbenchTxn(txn, ourIdentity)
    }
}

/**
 *
 */
@InitiatedBy(WorkbenchTransferCompleteFlow::class)
class WorkbenchTransferCompleteFlowResponder(val flowSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        println("WorkbenchCompleteFlowResponder: nothing to do - just print a message!")
    }
}