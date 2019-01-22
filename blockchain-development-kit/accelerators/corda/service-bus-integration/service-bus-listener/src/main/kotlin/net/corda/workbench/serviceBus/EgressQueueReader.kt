package net.corda.workbench.serviceBus


import com.typesafe.config.ConfigFactory
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.serviceBus.messaging.AzureConfig
import net.corda.workbench.serviceBus.messaging.Connection
import java.nio.charset.StandardCharsets


fun main(args: Array<String>) {
    val conf = ConfigFactory.load()
    val registry = Registry().store(AzureConfig(conf))
    val receiver = Connection(registry).egressQueueReceiver()

    while(true){
        val msg = receiver.receive()

        if (msg != null) {
            val body = msg.body
            val bodyText = String(body, StandardCharsets.UTF_8)
            println(bodyText)
            receiver.completeAsync(msg.lockToken)
        }
    }

    //System.exit(0)  // need to force this for some reason
}
