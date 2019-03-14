package net.corda.workbench.basicProvenance.flow.workbench

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.reflections.workbench.TxnResult
import net.corda.workbench.basicProvenance.flow.TransferFlow

@InitiatingFlow
@StartableByRPC
class WorkbenchTransferFlow(private val linearId: UniqueIdentifier,
                            private val newCounterparty: Party) : FlowLogic<TxnResult>() {

    @Suspendable
    override fun call(): TxnResult {
        val txn = subFlow(TransferFlow(linearId, newCounterparty))
        return buildWorkbenchTxn(txn, ourIdentity)

    }
}

/**
 *
 */
@InitiatedBy(WorkbenchTransferFlow::class)
class WorkbenchTransferFlowResponder(val flowSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        println("WorkbenchTransferFlowResponder: nothing to do - just print a message!")
    }
}