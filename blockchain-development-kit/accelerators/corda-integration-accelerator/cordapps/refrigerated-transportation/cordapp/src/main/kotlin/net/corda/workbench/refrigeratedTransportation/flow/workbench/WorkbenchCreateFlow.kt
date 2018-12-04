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
import net.corda.workbench.refrigeratedTransportation.Shipment
import net.corda.workbench.refrigeratedTransportation.flow.CreateFlow

/**
 * A wrapper compatible with Azure Workbench, which
 * needs a single list of params and a TxnResult / TxnResultTyped
 * return type
 */
@InitiatingFlow
@StartableByRPC
class WorkbenchCreateFlow(private val linearId: UniqueIdentifier,
                          private val owner: Party,
                          private val device: Party,
                          private val supplyChainOwner: Party,
                          private val supplyChainObserver: Party,
                          private val minHumidity: Int,
                          private val maxHumidity: Int,
                          private val minTemperature: Int,
                          private val maxTemperature: Int) : FlowLogic<TxnResult>() {

    @Suspendable
    override fun call(): TxnResult {

        val state = Shipment(owner = owner,
                device = device,
                supplyChainObserver = supplyChainObserver,
                supplyChainOwner = supplyChainOwner,
                minHumidity = minHumidity,
                maxHumidity = maxHumidity,
                minTemperature = minTemperature,
                maxTemperature = maxTemperature,
                linearId = linearId)

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