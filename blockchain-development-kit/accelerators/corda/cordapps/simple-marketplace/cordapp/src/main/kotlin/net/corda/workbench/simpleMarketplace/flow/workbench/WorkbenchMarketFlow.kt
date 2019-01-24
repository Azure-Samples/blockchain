package net.corda.workbench.simpleMarketplace.flow.workbench

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.reflections.workbench.TxnResult

import net.corda.workbench.simpleMarketplace.flow.MarketFlow
import net.corda.workbench.simpleMarketplace.state.AvailableItem
import net.corda.workbench.simpleMarketplace.state.StateType


@InitiatingFlow
@StartableByRPC
class WorkbenchMarketFlow(private val linearId: UniqueIdentifier = UniqueIdentifier(),
                          private val owner: Party,
                          private val buyer: Party,
                          var description: String,
                          var price: Double,
                          var offeredPrice: Double
) : FlowLogic<TxnResult>() {

    @Suspendable
    override fun call(): TxnResult {

        val _item = AvailableItem(description,price, StateType.available)
        val txn = subFlow(MarketFlow.Initiator(_item,offeredPrice,buyer))
        return buildWorkbenchMarketTxn(txn, ourIdentity)
    }
}

/**
 *
 */
@InitiatedBy(WorkbenchMarketFlow::class)
class WorkbenchMarketFlowResponder(val flowSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        println("WorkbenchCreateFlowResponder: nothing to do - just print a message!")
    }
}