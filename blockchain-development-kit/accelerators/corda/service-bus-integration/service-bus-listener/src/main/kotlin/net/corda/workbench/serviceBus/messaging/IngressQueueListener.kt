package net.corda.workbench.serviceBus.messaging

import com.microsoft.azure.servicebus.IMessageReceiver
import java.util.concurrent.CompletableFuture
import com.typesafe.config.ConfigFactory
import net.corda.workbench.commons.registry.Registry
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class Listener(private val registry: Registry) {

    fun run() {
        val receiver = Connection(registry).ingressQueueReceiver()
        val sender = Connection(registry).egressQueueClient()
        val factory = MessageProcessorFactory(registry, sender)
        val executor = Executors.newFixedThreadPool(100)

        receiveMessagesAsync(receiver, factory, executor)
    }

    fun receiveMessagesAsync(receiver: IMessageReceiver, factory: MessageProcessorFactory, executor: ExecutorService): CompletableFuture<*> {

        val task: CompletableFuture<Any> = CompletableFuture()

        try {
            CompletableFuture.runAsync {
                while (!task.isCancelled) {
                    try {
                        val message = receiver.receive()
                        if (message != null) {
                            println("RECEIVED $message")

                            val processor = factory.createProcessor(message)

                            // run the processor and wait for a result
                            // TODO - what about timeouts and exceptions?
                            val result = executor.submit(processor)
                            println(result)

                            // respond back to the Azure queue - this message has now been accepted
                            // for processing, and responses will come back on the egress queue
                            receiver.completeAsync(message.lockToken)

                            // No reason to do anything with the result ?
                            //result.get()
                        }
                    } catch (e: Exception) {
                        task.completeExceptionally(e)
                    }
                }
                task.complete(null)
            }
            return task
        } catch (e: Exception) {
            task.completeExceptionally(e)
        }

        return task
    }

}

// local test harness
fun main(args: Array<String>) {

    val conf = ConfigFactory.load()
    val registry = Registry().store(AzureConfig(conf))


    println(conf)
    println("Running listener")
    Listener(registry).run()
    println("done..")
}

