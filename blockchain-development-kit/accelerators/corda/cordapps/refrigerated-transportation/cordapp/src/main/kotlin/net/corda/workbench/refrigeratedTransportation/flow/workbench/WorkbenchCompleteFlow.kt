package net.corda.workbench.refrigeratedTransportation.flow.workbench

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.reflections.workbench.TxnResult
import net.corda.workbench.refrigeratedTransportation.flow.CompleteFlow

/**
 * A wrapper compatible with Azure Workbench, which
 * needs a single list of params
 */
@InitiatingFlow
@StartableByRPC
class WorkbenchCompleteFlow(private val linearId: UniqueIdentifier) : FlowLogic<TxnResult>() {

    @Suspendable
    override fun call(): TxnResult {
        val txn = subFlow(CompleteFlow(linearId))
        return buildWorkbenchTxn(txn, ourIdentity)
    }
}

/**
 *
 */
@InitiatedBy(WorkbenchCompleteFlow::class)
class WorkbenchCompleteFlowResponder(val flowSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        println("WorkbenchCompleteFlowResponder: nothing to do - just print a message!")
    }
}