package net.corda.workbench.chat.flow.workbench

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.reflections.annotations.Description
import net.corda.reflections.workbench.TxnResult
import net.corda.workbench.chat.flow.ChatFlow

import net.corda.workbench.chat.flow.StartChatFlow

/**
 * A wrapper compatible with Azure Workbench, which
 * needs a single list of params and a TxnResult / TxnResultTyped
 * return type
 */
@InitiatingFlow
@StartableByRPC
@Description("Chat with the other party. Either party can start this flow," +
        "but the Chat must have been started with a 'WorkbenchStartFlow'. No rude words!")
class WorkbenchChatFlow(private val linearId: UniqueIdentifier,
                        private val message: String
) : FlowLogic<TxnResult>() {

    @Suspendable
    override fun call(): TxnResult {

        val txn = subFlow(ChatFlow(message, linearId))
        return buildWorkbenchTxn(txn, ourIdentity)
    }
}

/**
 *
 */
@InitiatedBy(WorkbenchChatFlow::class)
class WorkbenchChatFlowResponder(val flowSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        println("WorkbenchChatFlowResponder: nothing to do - just print a message!")
    }
}