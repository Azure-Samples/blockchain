package net.corda.workbench.serviceBus.messaging

import com.microsoft.azure.servicebus.IMessage
import com.microsoft.azure.servicebus.IQueueClient
import net.corda.workbench.commons.registry.Registry


class MessageProcessorFactory(private val registry: Registry, private val queueClient: IQueueClient) {

    fun createProcessor(msg: IMessage): IngressMessageProcessor {
        return IngressMessageProcessor(registry, msg, queueClient)
    }

}