package net.corda.workbench.transactionBuilder.clients

import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.transactionBuilder.events.Repo
import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.json.JSONArray
import org.json.JSONObject
import java.lang.RuntimeException
import java.util.ArrayList

/**
 * A client for communicating with a transaction builder agent. An agent is started
 * for each network that we communicate with, this is the only clean way of ensuring
 * proper isolation of classes and configurations
 */
interface AgentClient {
    /**
     * Return all data in the vault for the given app and state.
     *
     * @param app - The registered app name, e.g. 'refrigerated-transportation'
     * @param stateClassName - The name of the state class. Can be just a simple name
     *                          or fully qualified with the package name
     */
    fun queryState(app: String, stateClassName: String): List<Map<String, Any>>

    /**
     * Return all data in the vault for the given app and state.
     *
     * @param app - The registered app name, e.g. 'refrigerated-transportation'
     * @param stateClassName - The name of the state class. Can be just a simple name
     *                          or fully qualified with the package name
     */
    fun queryStateHistory(app: String, stateClassName: String, id : String): List<Map<String, Any>>

    /**
     * Returns a list of fully qualified of available State classes (classes
     * implementing LinearState)
     *
     * @param app - The registered app name, e.g. 'refrigerated-transportation'
     */
    fun listStates(app: String): List<String>

    /**
     * Returns a list of fully qualified of available flows (flows
     * invokable by RPC
     *
     * @param app - The registered app name, e.g. 'refrigerated-transportation'
     */
    fun listFlows(app: String): List<String>

    fun flowMetaData(app: String, flow: String): Map<String, Any>

    fun flowAnnotations(app: String, flow: String): Map<String, Any>

    fun runFlow(app: String, flow: String, data: Map<String, Any?>): Any?

}

interface AgentClientFactory {
    fun createClient(network: String, node: String): AgentClient
}

class AgentClientFactoryImpl(es: EventStore) : AgentClientFactory {
    private val repo = Repo(es)
    override fun createClient(network: String, node: String): AgentClient {
        val port = repo.agentPort(network)
        return AgentQueryImpl("http://localhost:$port/$network/$node")
    }
}

class AgentQueryImpl(private val baseUrl: String) : AgentClient {
    override fun queryStateHistory(app: String, state: String, id: String): List<Map<String, Any>> {
        val request = Request(Method.GET, "$baseUrl/$app/query/$state/$id")
        val client = ApacheClient()
        val resp = client(request)

        if (resp.status.successful) {
            val results = ArrayList<Map<String, Any>>()
            val json = JSONArray(resp.body.toString())
            for (item in json.toList()) {
                results.add(item as Map<String, Any>)
            }
            return results
        } else {
            throw RuntimeException("$request failed with $resp")
        }
    }


    override fun queryState(app: String, state: String): List<Map<String, Any>> {
        val request = Request(Method.GET, "$baseUrl/$app/query/$state")
        val client = ApacheClient()
        val resp = client(request)

        if (resp.status.successful) {
            val results = ArrayList<Map<String, Any>>()
            val json = JSONArray(resp.body.toString())
            for (item in json.toList()) {
                results.add(item as Map<String, Any>)
            }
            return results
        } else {
            throw RuntimeException("$request failed with $resp")
        }
    }


    override fun listStates(app: String): List<String> {
        return requestAsJsonArray("$baseUrl/$app/states/list")
    }

    private fun requestAsJsonArray(url: String): List<String> {

        val request = Request(Method.GET, url)
        val client = ApacheClient()
        val resp = client(request)

        if (resp.status.successful) {
            val json = JSONArray(resp.body.toString())
            return json.toList() as List<String>
        } else {
            throw RuntimeException("$request failed with $resp")
        }
    }


    override fun listFlows(app: String): List<String> {
        return requestAsJsonArray("$baseUrl/$app/flows/list")
    }

    override fun flowAnnotations(app: String, flow: String): Map<String, Any> {
        val request = Request(Method.GET, "$baseUrl/$app/flows/$flow/annotations")
        val client = ApacheClient()
        val resp = client(request)

        if (resp.status.successful) {
            val json = JSONObject(resp.body.toString())
            return json.toMap()
        } else {
            throw RuntimeException("$request failed with $resp")
        }
    }

    override fun flowMetaData(app: String, flow: String): Map<String, Any> {

        val request = Request(Method.GET, "$baseUrl/$app/flows/$flow/metadata")
        val client = ApacheClient()
        val resp = client(request)

        if (resp.status.successful) {
            println("meta data for flow is: ${resp.body}")
            val json = JSONObject(resp.body.toString())
            return json.toMap()
        } else {
            throw RuntimeException("$request failed with $resp")
        }
    }

    override fun runFlow(app: String, flow: String, data: Map<String, Any?>): Any? {
        val json = JSONObject(data).toString(2)
        println(json)

        val request = Request(Method.POST, "$baseUrl/$app/flows/$flow/run")
                .body(json)

        val client = ApacheClient()
        val resp = client(request)

        if (resp.status.successful) {
            println("response from agent is ${resp.body.toString()}")
            val json = JSONObject(resp.body.toString())
            return json.toMap()
        } else {
            throw RuntimeException("$request failed with $resp")
        }

        //return null
    }

}


