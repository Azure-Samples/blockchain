package net.corda.workbench.serviceBus.messaging

import com.microsoft.azure.servicebus.ClientFactory
import com.microsoft.azure.servicebus.IMessageReceiver
import com.microsoft.azure.servicebus.QueueClient
import com.microsoft.azure.servicebus.ReceiveMode
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder
import com.microsoft.azure.servicebus.primitives.RetryExponential
import net.corda.workbench.commons.registry.Registry
import java.io.PrintStream
import java.time.Duration

/**
 * AzureConfig details for talking to a azure service bus
 */
class Connection(private val registry: Registry) {

    fun ingressQueueClient(): QueueClient {
        val config = registry.retrieve(AzureConfig::class.java)
        val connectionStringBuilder = ConnectionStringBuilder(config.endpoint, config.ingressQueue)
        val retryPolicy = RetryExponential(Duration.ofSeconds(1),
            Duration.ofSeconds(10),
            5, "TRY5TIMES")
        connectionStringBuilder.retryPolicy = retryPolicy
        val sendClient = QueueClient(connectionStringBuilder, ReceiveMode.PEEKLOCK)

        val ps = registry.retrieveOrElse(PrintStream::class.java, System.out)
        ps.println("Sender connected to queue ${config.ingressQueue} using endpoint ${config.endpoint}")
        return sendClient
    }

    fun egressQueueClient(): QueueClient {
        val config = registry.retrieve(AzureConfig::class.java)
        val sendClient = QueueClient(ConnectionStringBuilder(config.endpoint, config.egressQueue), ReceiveMode.PEEKLOCK)

        val ps = registry.retrieveOrElse(PrintStream::class.java, System.out)
        ps.println("Sender connected to queue ${config.egressQueue} using endpoint ${config.endpoint}")
        return sendClient
    }

    fun ingressQueueReceiver(): IMessageReceiver {
        val config = registry.retrieve(AzureConfig::class.java)

        val receiver = ClientFactory.createMessageReceiverFromConnectionStringBuilder(
                ConnectionStringBuilder(config.endpoint, config.ingressQueue), ReceiveMode.PEEKLOCK)

        val ps = registry.retrieveOrElse(PrintStream::class.java, System.out)
        ps.println("Receiver connected to queue ${config.ingressQueue} using endpoint ${config.endpoint}")

        return receiver
    }

    fun egressQueueReceiver(): IMessageReceiver {
        val config = registry.retrieve(AzureConfig::class.java)

        val receiver = ClientFactory.createMessageReceiverFromConnectionStringBuilder(
                ConnectionStringBuilder(config.endpoint, config.egressQueue), ReceiveMode.PEEKLOCK)

        val ps = registry.retrieveOrElse(PrintStream::class.java, System.out)
        ps.println("Receiver connected to queue ${config.egressQueue} using endpoint ${config.endpoint}")

        return receiver
    }

}