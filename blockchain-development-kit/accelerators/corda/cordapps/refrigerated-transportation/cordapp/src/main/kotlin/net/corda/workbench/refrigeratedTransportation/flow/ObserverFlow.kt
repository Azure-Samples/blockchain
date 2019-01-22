package net.corda.workbench.refrigeratedTransportation.flow

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.ReceiveTransactionFlow
import net.corda.core.flows.SendTransactionFlow
import net.corda.core.identity.Party
import net.corda.core.node.StatesToRecord
import net.corda.core.transactions.SignedTransaction

@InitiatingFlow
class ReportToObserverFlow(private val observer: Party, private val finalTx: SignedTransaction) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val session = initiateFlow(observer)
        subFlow(SendTransactionFlow(session, finalTx))
    }
}

@InitiatedBy(ReportToObserverFlow::class)
class ReportToObserverFlowResponder(private val otherSideSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // Start the matching side of SendTransactionFlow above, but tell it to record all visible states even
        // though they (as far as the node can tell) are nothing to do with us.
        subFlow(ReceiveTransactionFlow(otherSideSession, true, StatesToRecord.ALL_VISIBLE))
    }
}