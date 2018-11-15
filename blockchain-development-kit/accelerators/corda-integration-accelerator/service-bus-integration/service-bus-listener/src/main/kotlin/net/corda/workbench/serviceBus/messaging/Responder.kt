package net.corda.workbench.serviceBus.messaging

import com.microsoft.azure.servicebus.IQueueClient
import com.microsoft.azure.servicebus.Message
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Generates "fire and forget" message to the egress queue
 */
class Responder(val returnQueue: IQueueClient) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Responder::class.java)
    }

    fun createContractUpdate(status: String, messageData: IngressMessageProcessor.MessageData, errorMessage: String? = null) {
        try {
            val msgType = if (messageData.isCreateMessage) "CreateContractUpdate" else "CreateContractActionUpdate"
            val msg = HashMap(baseMessage(msgType))
            msg["requestId"] = messageData.requestId
            msg["contractId"] = messageData.contractId
            msg["contractLedgerIdentifier"] = messageData.linearId
            msg["status"] = status
            if (errorMessage != null) {
                msg["additionalInformation"] = mapOf("errorMessage" to errorMessage)
            }
            sendMessage(msg)
        } catch (ex: Exception) {
            // todo - should there be retry logic here is queue is unavailable and so on
            logger.warn("Exception sending createContractUpdate for ${messageData.linearId}", ex)
        }

    }

    private fun sendMessage(msg: HashMap<String, Any>) {
        val messageId = UUID.randomUUID().toString()
        val msgText = JSONObject(msg, true).toString(2)
        val message = Message(msgText)
        message.contentType = "application/json"
        message.label = "Corda"
        message.messageId = messageId
        message.timeToLive = Duration.ofHours(1)

        logger.debug("sending message: $msgText")
        returnQueue.send(message)
    }


    private fun baseMessage(name: String): Map<String, Any> {
        return mapOf("messageSchemaVersion" to "1.0.0",
                "messageName" to name,
                "connectionId" to 1,
                "additionalInformation" to emptyMap<String, Any>())

    }

    fun createContractMessage(flowResult: Map<String, Any?>, messageData: IngressMessageProcessor.MessageData) {
        try {
            val msg = HashMap(baseMessage("ContractMessage"))
            msg["blockId"] = 999
            msg["blockhash"] = flowResult["txnHash"] as String
            msg["modifyingTransactions"] = buildModifyingTransactions(flowResult)
            msg["contractId"] = messageData.contractId
            msg["contractLedgerIdentifier"] = messageData.linearId
            msg["contractProperties"] = buildContractProperties(flowResult)
            msg["isNewContract"] = messageData.isCreateMessage

            sendMessage(msg)

        } catch (ex: Exception) {
            println("problem with createContractMessage()")
            ex.printStackTrace()
        }
    }

    private fun buildContractProperties(flowResult: Map<String, Any?>): List<Map<String, Any?>> {
        val results = ArrayList<Map<String, Any?>>()

        val contractProperties = flowResult["contractProperties"]
        if (contractProperties is List<*>) {
            var counter = 1
            @Suppress("UNCHECKED_CAST")
            for (prop in contractProperties as List<Map<String, Any>>) {
                val working = HashMap<String, Any?>()
                working["name"] = prop["name"]!!
                working["value"] = prop["value"]
                working["workflowPropertyId"] = counter++
                results.add(working)
            }
        }
        if (contractProperties is Map<*, *>) {
            var counter = 1
            @Suppress("UNCHECKED_CAST")
            val props = contractProperties as Map<String, Any>
            for (key in props.keys) {
                val working = HashMap<String, Any?>()
                working["name"] = key
                working["value"] = props[key]
                working["workflowPropertyId"] = counter++
                results.add(working)
            }

        }
        return results
    }

    private fun buildModifyingTransactions(result: Map<String, Any?>): List<Map<String, Any>> {
        val data = HashMap<String, Any>()
        data["transactionId"] = 999
        data["transactionHash"] = result["txnHash"] as String
        data["from"] = jsonStringify(result["owner"])!!
        data["to"] = (result["otherParties"] as List<*>).map { jsonStringify(it) }

        return listOf(data)
    }

    private fun jsonStringify(data: Any?): Any? {
        if (data is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            return JSONObject(data as Map<String, Any>).toString()
        }
        if (data is List<*>) {
            val working = ArrayList<Any?>(data.size)
            for (item in data) {
                working.add(jsonStringify(item))
            }
            return working
        } else {
            return data
        }
    }


}