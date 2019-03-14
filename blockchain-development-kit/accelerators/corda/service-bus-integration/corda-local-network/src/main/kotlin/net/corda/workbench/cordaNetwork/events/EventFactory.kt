package net.corda.workbench.transactionBuilder.events

import net.corda.workbench.commons.event.Event
import java.util.*

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

    fun NODE_STARTED(network: String, node: String, pid: Long, processId: UUID): Event {
        // why pid.toInt here? because a real PID is actually small enough to be expressed as an Int
        // and the Json deserialize will bring it back as an Int. So even if we save as a Long, on
        // reading it will be an Int. An annoying feature of Json serialisation / deserialisation
        // that needs a nice solution.
        return Event(type = "NodeStarted",
                aggregateId = network,
                payload = mapOf("node" to node, "pid" to pid.toInt(), "processId" to processId))

    }

    fun NODE_STOPPED(network: String, node: String, pid: Long, message: String): Event {
        return Event(type = "NodeStopped",
                aggregateId = network,
                payload = mapOf("node" to node, "pid" to pid.toInt(), "message" to message))

    }

    fun NETWORK_STARTED(network: String): Event {
        return Event(type = "NetworkStarted",
                aggregateId = network)

    }

    fun NETWORK_STOPPED(network: String): Event {
        return Event(type = "NetworkStopped",
                aggregateId = network)

    }

    fun NETWORK_CREATED(network: String): Event {
        return Event(type = "NetworkCreated",
                aggregateId = network)

    }

    fun CORDAPP_DEPLOYED(network: String, name: String, size: Int, md5Hash: String): Event {
        return Event(type = "CordappDeployed",
                aggregateId = network,
                payload = mapOf("name" to name, "size" to size,
                        "md5Hash" to md5Hash))

    }


}
