package net.corda.workbench.serviceBus.cordaTransactionBuilder

import org.json.JSONObject
import java.util.*

/**
 * Talk to a transaction-builder-service
 */
interface TransactionBuilderClient {
    fun run(location: CordaLocation, flowName: String, flowParams: Map<String, Any?>): JSONObject
}
/**
 * A client for calling the corda-transaction-builder service,
 * that in turn will make the RPC call via use of Kotlin messaging
 */
class TransactionBuilderClientImpl() : TransactionBuilderClient {


    override fun run(location: CordaLocation, flowName: String, flowParams: Map<String, Any?>): JSONObject {
        val url = baseUrl(location, flowName) + "/run"
        val payload = JSONObject(flowParams)
        val result = khttp.post(url = url, json = payload)

        if (result.statusCode == 200) {
            val json = result.jsonObject
            println(json)
            return json
            // what else to do ?
        } else {
            val error = result.text
            throw RuntimeException("Cannot run flow $flowName, at location $location:\n\n$error")
        }
    }

    private fun baseUrl(location: CordaLocation, flow: String): String {
        return "http://corda-transaction-builder:1112/${location.network}/${location.node}/${location.cordapp}/flows/$flow"
    }
}

class FakeTransactionBuilderClient : TransactionBuilderClient {
    val fakeResults = ArrayDeque<Any>()

    fun addResult(json: String) {
        fakeResults.addLast(JSONObject(json))
    }

    fun addResult(json: JSONObject) {
        fakeResults.addLast(json)
    }

    fun addResult(data: Map<String, Any>) {
        fakeResults.addLast(JSONObject(data))
    }

    fun addException(ex: Exception) {
        fakeResults.addLast(ex)
    }

    override fun run(location: CordaLocation, flowName: String, flowParams: Map<String, Any?>): JSONObject {
        val fake = fakeResults.removeFirst()
        if (fake != null) {
             if (fake is JSONObject){
                 return fake
             }
             if (fake is Exception){
                 throw fake
             }
        }
        return JSONObject();
    }

}

