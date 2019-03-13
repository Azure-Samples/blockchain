package net.corda.workbench.basicProvenance.flow.workbench

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.reflections.workbench.TxnResult
import net.corda.workbench.basicProvenance.flow.CreateFlow
import net.corda.workbench.basicProvenance.state.ItemState
import net.corda.workbench.basicProvenance.state.StateType

@InitiatingFlow
@StartableByRPC
class WorkbenchCreateFlow(private val creator: Party,
                          private val otherParty: Party,
                          private val Observer: Party,
                          private val value: Double
                          ) : FlowLogic<TxnResult>() {

    @Suspendable
    override fun call(): TxnResult {

        val state = ItemState(
                value = value,
                creator = creator,
                otherParty = otherParty,
                state=  StateType.created,
                Observer = Observer,
                linearId = UniqueIdentifier()
             )

        val txn = subFlow(CreateFlow(state))
        return buildWorkbenchTxn(txn, ourIdentity)
    }
}

/**
 *
 */
@InitiatedBy(WorkbenchCreateFlow::class)
class WorkbenchCreateFlowResponder(val flowSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        println("WorkbenchCreateFlowResponder: nothing to do - just print a message!")
    }
}