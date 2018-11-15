package net.corda.workbench.refrigeratedTransportation.flow.workbench

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.reflections.workbench.TxnResult
import net.corda.workbench.refrigeratedTransportation.Telemetry
import net.corda.workbench.refrigeratedTransportation.flow.IngestTelemetryFlow

/**
 * A wrapper compatible with Azure Workbench, which
 * needs a single list of params and a TxnResult / TxnResultTyped
 * return type
 */
@InitiatingFlow
@StartableByRPC
class WorkbenchTelemetryFlow(private val linearId: UniqueIdentifier,
                             private val temperature: Int,
                             private val humidity: Int) : FlowLogic<TxnResult>() {

    @Suspendable
    override fun call(): TxnResult {
        val telemetry = Telemetry(temperature = temperature, humidity = humidity)
        val txn = subFlow(IngestTelemetryFlow(linearId, telemetry))

        return buildWorkbenchTxn(txn, ourIdentity)
    }
}

/**
 *
 */
@InitiatedBy(WorkbenchTelemetryFlow::class)
class WorkbenchTelemetryFlowResponder(val flowSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        println("WorkbenchTelemetryFlowResponder: nothing to do - just print a message!")
    }
}