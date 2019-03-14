package net.corda.workbench.cordaNetwork.events

import net.corda.core.identity.CordaX500Name
import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.event.Filter
import java.util.*

class Repo(val es: EventStore) {

    /**
     * The full list of networks
     */
    fun networks(): List<NetworkInfo> {
        val networks = HashMap<String, MutableMap<String, String>>()
        es.retrieve()
                .forEach { ev ->
                    when {
                        ev.type == "NetworkCreated" -> {
                            networks[ev.aggregateId!!] = mutableMapOf("name" to ev.aggregateId!!)
                        }
                        ev.type == "NetworkStarted" -> {
                            val data = networks[ev.aggregateId]
                            if (data != null) {
                                data["status"] = "Running"
                            }
                        }
                        ev.type == "NetworkStopped" -> {
                            val data = networks[ev.aggregateId]
                            if (data != null) {
                                data["status"] = "Stopped"
                            }
                        }
                        else -> { /* dont care */
                        }
                    }
                }
        return networks
                .values
                .map { NetworkInfo.fromMap(it) }
                .sortedBy { it.name }
    }

    /**
     * Full list of nodes for a network
     */
    fun nodes(network: String): List<NodeInfo> {
        val result = HashSet<NodeInfo>()
        es.retrieve(Filter(aggregateId = network, type = "NodesCreated"))
                .forEach { event ->
                    val nodes = event.payload["nodes"] as List<String>
                    result.addAll(nodes.map { it -> NodeInfo(it) })

                }
        return ArrayList(result).sortedBy { it.organisation }
    }

    /**
     * All the nodes that are currently recorded as running (according to the stored events)
     * Note, this doesn't necessarily reflected the actual state of the running process.
     */
    fun runningNodes(): List<RunningNode> {
        val nodes = HashMap<Pair<String, String>, Long>()
        es.retrieve()
                .forEach { ev ->
                    when {
                        ev.type == "NodeStarted" -> {
                            val key = Pair(ev.aggregateId!!, ev.payload["node"] as String)
                            nodes[key] = (ev.payload["pid"] as Int).toLong()
                        }
                        ev.type == "NodeStopped" -> {
                            val key = Pair(ev.aggregateId!!, ev.payload["node"] as String)
                            nodes.remove(key)

                        }
                        else -> { /* dont care */
                        }
                    }
                }
        return nodes
                .entries
                .map { RunningNode(it.key.first, it.key.second, it.value) }
                .sortedBy { "${it.network}:${it.node}" }

    }

    /**
     * Is the network running, based on the record of the events. Note that
     * this may not reflect the actual status of the processes
     */
    fun isNetworkRunning(network: String): Boolean {
        return es.retrieve(Filter(aggregateId = network))
                .fold(false) { status, event ->
                    when {
                        event.type == "NetworkStarted" -> true
                        event.type == "NetworkStopped" -> false
                        else -> status
                    }
                }
    }

    /**
     * The list of deployed apps as recorded in the event store.
     */
    fun deployedCordapps(network: String) : List<CordaAppInfo> {
        val apps = HashMap<String, CordaAppInfo>()

        es.retrieve(Filter(aggregateId = network, type = "CordappDeployed"))
                .forEach { ev ->
                    val name = ev.payload["name"] as String
                    val info = CordaAppInfo(name = name,
                    size = ev.payload["size"] as Int,
                            md5Hash = ev.payload["md5Hash"] as String,
                            deployedAt = Date(ev.timestamp)

                    )
                    apps[name] = info
                }

        return apps
                .values
                .sortedBy { it.name }

    }
}

data class NetworkInfo(val name: String, val status: String) {
    companion object {
        fun fromMap(data: Map<String, String>): NetworkInfo {
            return NetworkInfo(name = data["name"]!!,
                    status = data.getOrDefault("status", "Never Started"))
        }
    }
}


data class NodeInfo(val x500Name: String) {
    private val parts = CordaX500Name.parse(x500Name)
    val organisation = parts.organisation
    val country = parts.country
    val locality = parts.locality
}

/**
 * Information about a running node.
 */
data class RunningNode(val network: String, val node: String, val pid: Long)

/**
 * Information about a deployed corda app
 */
data class CordaAppInfo(val name: String, val size: Int, val md5Hash: String, val deployedAt: Date = Date())
