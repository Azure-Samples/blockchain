package net.corda.workbench.transactionBuilder.clients

import org.http4k.client.ApacheClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.json.JSONArray
import java.lang.RuntimeException
import java.util.*

/**
 * A client for communicating with a 'corda-local-network' service
 */
interface LocalNetworkClient {

    fun networks(): List<String>

    fun cordapps(network: String): List<CordaAppInfo>

    fun nodes(network: String): List<String>

    fun downloadCordapp(network: String, cordapp: String): ByteArray

}

class LocalNetworkClientImpl : LocalNetworkClient {

    private val baseUrl = "http://corda-local-network:1115"
    override fun cordapps(network: String): List<CordaAppInfo> {
        val request = Request(Method.GET, "$baseUrl/api/networks/$network/cordapps")
        val client = ApacheClient()
        val resp = client(request)

        if (resp.status.successful) {
            val json = JSONArray(resp.body.toString())
            val result = ArrayList<CordaAppInfo>()
            for (item in json.toList()) {
                item as Map<String, Any>
                result.add(CordaAppInfo(name = item["name"] as String,
                        size = item["size"] as Int,
                        md5Hash = item["md5Hash"] as String))

            }
            return result
        } else {
            throw RuntimeException("$request failed with $resp")
        }

    }

    override fun networks(): List<String> {
        val request = Request(Method.GET, "$baseUrl/api/networks")
        val client = ApacheClient()
        val resp = client(request)

        if (resp.status.successful) {
            val results = ArrayList<String>()
            val json = JSONArray(resp.body.toString())
            for (item in json.toList()) {
                item as Map<String, Any>
                results.add(item["name"] as String)
            }
            return results
        } else {
            throw RuntimeException("$request failed with $resp")
        }
    }

    override fun nodes(network: String): List<String>{
        val request = Request(Method.GET, "$baseUrl/api/networks/$network/nodes")
        val client = ApacheClient()
        val resp = client(request)

        if (resp.status.successful) {
            val results = ArrayList<String>()
            val json = JSONArray(resp.body.toString())
            for (item in json.toList()) {
                println(item)
                item as Map<String, Any>
                results.add(item["organisation"] as String)
            }
            return results
        } else {
            throw RuntimeException("$request failed with $resp")
        }
    }


    override fun downloadCordapp(network: String, cordapp: String): ByteArray {
        val request = Request(Method.GET, "$baseUrl/web/networks/$network/cordapps/$cordapp/download")

        val resp = ApacheClient()(request)
        if (resp.status.successful) {
            val binary = resp.body.payload
            return binary.array()
        } else {
            throw RuntimeException("$request failed with $resp")
        }
    }

}

data class CordaAppInfo(val name: String, val size: Int, val md5Hash: String)
