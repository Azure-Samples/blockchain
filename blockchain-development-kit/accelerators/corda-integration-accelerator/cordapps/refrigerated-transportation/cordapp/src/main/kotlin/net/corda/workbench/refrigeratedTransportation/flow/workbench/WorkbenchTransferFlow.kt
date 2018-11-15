package net.corda.workbench.refrigeratedTransportation.flow.workbench

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.reflections.workbench.TxnResult
import net.corda.workbench.refrigeratedTransportation.Transfer
import net.corda.workbench.refrigeratedTransportation.flow.TransferResponsibilityFlow

/**
 * A wrapper compatible with Azure Workbench, which
 * needs a single list of params
 */
@InitiatingFlow
@StartableByRPC
class WorkbenchTransferFlow(private val linearId: UniqueIdentifier,
                            private val newCounterparty: Party) : FlowLogic<TxnResult>() {

    @Suspendable
    override fun call(): TxnResult {
        val transfer = Transfer(newCounterparty = newCounterparty)
        val txn = subFlow(TransferResponsibilityFlow(linearId, transfer))

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