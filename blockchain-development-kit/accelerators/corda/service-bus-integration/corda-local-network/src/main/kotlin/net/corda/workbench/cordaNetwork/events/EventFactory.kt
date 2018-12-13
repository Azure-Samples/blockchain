package net.corda.workbench.transactionBuilder.events

import net.corda.workbench.commons.event.Event

/**
 * Build events for storage. TODO it would
 * be nice to have typed classes here, but Event
 * is currently defined as a data class & so cant be extended
 */
object EventFactory {

    fun NODES_CREATED(network: String, nodes: List<String>): Event {
        return Event(type = "NodesCreated",
                aggregateId = network,
                payload = mapOf<String, Any>("nodes" to nodes))

    }

    fun NODE_STARTED(network: String, node: String, pid: Int): Event {
        return Event(type = "NodeStarted",
                aggregateId = network,
                payload = mapOf("node" to node, "pid" to pid))

    }

    fun NODE_STOPPED(network: String, node: String, pid: Int, message: String): Event {
        return Event(type = "NodeStopped",
                aggregateId = network,
                payload = mapOf("node" to node, "pid" to pid, "message" to message))

    }


    fun NETWORK_STARTED(network: String): Event {
        return Event(type = "NetworkStarted",
                aggregateId = network)

    }

    fun NETWORK_STOPPED(network: String): Event {
        return Event(type = "NetworkStopped",
                aggregateId = network)

    }

}
