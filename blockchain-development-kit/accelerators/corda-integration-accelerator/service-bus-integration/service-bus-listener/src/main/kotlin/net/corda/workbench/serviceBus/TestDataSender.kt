package net.corda.workbench.serviceBus

import com.microsoft.azure.servicebus.Message
import com.microsoft.azure.servicebus.QueueClient
import com.typesafe.config.ConfigFactory
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.serviceBus.messaging.AzureConfig
import net.corda.workbench.serviceBus.messaging.Connection
import java.io.File
import java.time.Duration
import java.util.*


/**
 * Build test data and sends to a queue
 */
class TestDataSender(val queueClient: QueueClient) {

    fun sendTransaction(cordapp: String, dataset: String) {

        val linearId = UUID.randomUUID();
        println ("Sending messages for $cordapp/$dataset dataset with linearId of $linearId")

        sendMessages(queueClient, cordapp, dataset, linearId)
        println("   Completed dataset/n")

    }


    fun sendMessages(sendClient: QueueClient, cordapp: String, dataset: String, linearId : UUID) {

        var firstMessage = true
        val directory = "src/test/resources/datasets/$cordapp/$dataset/ingress"
        File(directory).walk().forEach {
            if (it.name.endsWith(".json")) {
                print("   Sending ${it.name}")
                val params = mapOf(
                        "linearId" to linearId.toString(),
                        "requestId" to UUID.randomUUID().toString()
                )

                print(".")
                val content = readFileAsText(it.path, params)
                print(".")

                val messageId = UUID.randomUUID().toString()
                val message = Message(content)
                message.contentType = "application/json"
                message.label = "Corda"
                message.messageId = messageId
                message.timeToLive = Duration.ofHours(1)
                print(".")

                sendClient.send(message)
                println(".done")

                if (firstMessage) {
                    Thread.sleep(10000L)
                    firstMessage = false
                }
                else {
                    Thread.sleep(5000L)
                }
            }
        }
    }

}


fun main(args: Array<String>) {
    val conf = ConfigFactory.load()
    val registry = Registry().store(AzureConfig(conf))
    val sendClient = Connection(registry).ingressQueueClient()

    TestDataSender(sendClient).sendTransaction("refrigeratedTransportation", "happyPath")
    TestDataSender(sendClient).sendTransaction("refrigeratedTransportation", "outOfCompliance")

    println("All done, closing connection to queue")
    sendClient.close()
    System.exit(0)  // need to force this for some reason
}
