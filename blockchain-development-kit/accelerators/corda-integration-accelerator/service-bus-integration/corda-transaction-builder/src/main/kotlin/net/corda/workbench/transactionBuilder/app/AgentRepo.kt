package net.corda.workbench.transactionBuilder.app

import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.event.Filter
import sun.security.provider.certpath.UntrustedChecker

class AgentRepo(val es: EventStore) {

    // reduce events to find the current port
    fun agentPort(network: String): Int {
        return es.retrieve(Filter(aggregateId = network))
                .fold(0) { port, event ->
                    when {
                        @Suppress("UNCHECKED_CAST")
                        event.type == "AgentStarted" ->
                            event.payload["port"] as Int
                        event.type == "AgentStopped" -> 0
                        else -> port
                    }
                }

    }

}