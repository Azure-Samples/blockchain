package net.corda.workbench.chat.flow.workbench

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.reflections.workbench.TxnResult

import net.corda.workbench.chat.flow.StartChatFlow

/**
 * A wrapper compatible with Azure Workbench, which
 * needs a single list of params and a TxnResult / TxnResultTyped
 * return type
 */
@InitiatingFlow
@StartableByRPC
class WorkbenchStartFlow(private val linearId: UniqueIdentifier,
                         private val otherParty: Party
) : FlowLogic<TxnResult>() {

    @Suspendable
    override fun call(): TxnResult {

        val txn = subFlow(StartChatFlow(otherParty, linearId))
        return buildWorkbenchTxn(txn, ourIdentity)
    }
}

/**
 *
 */
@InitiatedBy(WorkbenchStartFlow::class)
class WorkbenchStartFlowResponder(val flowSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        println("WorkbenchStartFlowResponder: nothing to do - just print a message!")
    }
}