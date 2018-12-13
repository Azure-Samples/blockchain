package net.corda.workbench.serviceBus.messaging

import com.microsoft.azure.servicebus.IMessage
import com.microsoft.azure.servicebus.IQueueClient
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.serviceBus.cordaTransactionBuilder.CordaLocation
import net.corda.workbench.serviceBus.cordaTransactionBuilder.TransactionBuilderClient
import net.corda.workbench.serviceBus.repo.WorkbenchRepo
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets.UTF_8

/**
 * Process a single message , calling the flow and generating reply messages based on the outcome.
 *
 * This is run via the standard Java Executor classes. The current implementation assumes a flow
 * that will return with a reasonable time frame. Long running flows or flows that become blocked may
 * be incorrectly reported as timed out / failed.
 *
 */
class IngressMessageProcessor(val registry: Registry, val msg: IMessage, val returnQueue: IQueueClient) : Runnable {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(IngressMessageProcessor::class.java)
    }

    override fun run() {
        try {
            logger.debug("Processing ${msg.messageId}")
            process(msg)
        } finally {
            logger.debug("Completed ${msg.messageId}")
        }
    }

    private fun process(msg: IMessage) {
        val responder = Responder(returnQueue)

        if (msg.contentType == "application/json") {
            var messageData: MessageData? = null
            try {
                messageData = extractMessageData(msg)

                val flow = messageData.messageData["workflowName"] as String
                @Suppress("UNCHECKED_CAST")
                val flowParams = buildFlowParms(messageData.messageData["parameters"] as List<Map<String, Any?>>)

                responder.createContractUpdate("Submitted", messageData)

                val cordaLocation = buildCordaLocation(messageData.messageData)
                val client = registry.retrieve(TransactionBuilderClient::class.java)

                val result = client.run(cordaLocation, flow, flowParams)
                responder.createContractUpdate("Committed", messageData)
                responder.createContractMessage(result.toMap(), messageData)
            } catch (e: Exception) {
                if (messageData != null) {
                    // unpacked the message, but there was a problem processing it
                    responder.createContractUpdate("Failure", messageData, e.message)
                } else {
                    // what to do here - couldn't unpack enough from the message,
                    // so all can do is log and / or issue a general failure message
                    logger.warn("Couldn't unpack message ${msg.messageId}", e)
                }
            }
        }
    }

    private fun buildContractId(contractLedgerIdentifier: String): Int {
        val repo = registry.retrieve(WorkbenchRepo::class.java)
        return repo.contractId(contractLedgerIdentifier)
    }

    private fun buildFlowParms(params: List<Map<String, Any?>>): Map<String, Any?> {
        val result = HashMap<String, Any?>()

        for (param in params) {
            val name = param["name"] as String
            val value = param["value"] as Any
            result[name] = value
        }
        return result
    }


    private fun readRequestId(data: Map<String, Any>): String {
        return data["requestId"] as String
    }

    private fun readLinearId(data: Map<String, Any>): String {
        @Suppress("UNCHECKED_CAST")
        val params = data["parameters"] as List<Map<String, Any>>

        for (p in params) {
            if (p["name"] == "linearId") return p["value"] as String
        }
        throw RuntimeException("No linearId found")
    }

    private fun readMessageType(data: Map<String, Any>): Boolean {
        return "CreateContractRequest" == data["messageName"]
    }

    private fun buildCordaLocation(data: Map<String, Any?>): CordaLocation {
        val network = data["connectionId"] as Int
        val cordapp = data["applicationName"] as String
        val node = data["userChainIdentifier"] as String
        return CordaLocation(network.toString(), cordapp, node)
    }

    private fun extractMessageData(msg: IMessage): MessageData {

        val body = msg.body
        val bodyText = String(body, UTF_8)

        val data = JSONObject(bodyText).toMap()

        val requestId = readRequestId(data)
        val linearId = readLinearId(data)
        val contractId = buildContractId(linearId)
        val isCreateMessage = readMessageType(data)

        return MessageData(
                requestId = requestId,
                linearId = linearId,
                contractId = contractId,
                isCreateMessage = isCreateMessage,
                messageData = data
        )

    }

    data class MessageData(
            val requestId: String,
            val linearId: String,
            val contractId: Int,
            val isCreateMessage: Boolean,
            val messageData: Map<String, Any?>
    )

//    // todo - what if no request id?
//    val requestId = readRequestId(data)
//    val linearId = readLinearId(data)
//    val contractId = buildContractId(linearId)
//    val isCreateMessage = readMessageType(data)
}

