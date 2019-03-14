package net.corda.workbench.transactionBuilder.events

import net.corda.workbench.commons.event.Event
import java.util.*

/**
 * Build events for storage. TODO it would
 * be nice to have typed classes here, but Event
 * is currently defined as a data class & so cant be extended
 */
object EventFactory {

    fun CORDA_APP_DEPLOYED(network: String, appname: String, appId: UUID, scannablePackages: List<String>): Event {
        return Event(type = "CordaAppDeployed",
                aggregateId = network,
                payload = mapOf("appname" to appname,
                        "appId" to appId.toString(),
                        "network" to network,
                        "scannablePackages" to scannablePackages))

    }

    fun AGENT_STARTED(network: String, port: Int, pid: Long): Event {
        return Event(type = "AgentStarted",
                aggregateId = network,
                payload = mapOf("network" to network,
                        "port" to port,
                        "pid" to pid))
    }

    fun AGENT_STOPPED(network: String, pid: Long, message: String): Event {
        return Event(type = "AgentStopped",
                aggregateId = network,
                payload = mapOf("network" to network,
                        "pid" to pid,
                        "message" to message))
    }

    fun NETWORK_JOINED(network: String, nodes: List<String>): Event {
        return Event(type = "NetworkJoined",
                aggregateId = network,
                payload = mapOf("network" to network,
                        "nodes" to nodes))
    }

    fun CORDAPP_DOWNLOAD(network: String, name: String, md5Hash: String): Event {
        return Event(type = "CordappDownload",
                aggregateId = network,
                payload = mapOf("network" to network,
                        "name" to name,
                        "md5Hash" to md5Hash))
    }
}
