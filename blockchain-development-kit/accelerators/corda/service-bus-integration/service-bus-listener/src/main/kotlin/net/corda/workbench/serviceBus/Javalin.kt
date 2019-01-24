package net.corda.workbench.serviceBus

import com.typesafe.config.ConfigFactory
import io.javalin.Context
import io.javalin.Javalin
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.serviceBus.api.PingApi
import net.corda.workbench.serviceBus.cordaTransactionBuilder.TransactionBuilderClientImpl
import net.corda.workbench.serviceBus.messaging.AzureConfig
import net.corda.workbench.serviceBus.messaging.Listener
import net.corda.workbench.serviceBus.repo.InMemoryWorkbenchRepo

class Javalin(private val port: Int) {

    fun init(): Javalin {

        val app = Javalin.create().apply {
            port(port)
            exception(Exception::class.java) { e, ctx ->
                // build the standard error response
                ctx.status(500)
                val payload = mapOf(
                        "message" to e.message,
                        "stackTrace" to e.stackTrace.joinToString("\n")
                )
                ctx.json(payload)
            }

            error(404) { ctx ->
                val payload = mapOf("message" to "page not found")
                ctx.json(payload)
            }

            //enableStaticFiles("/www", Location.CLASSPATH)
        }


        PingApi().register(app)
        app.start()
        println("Ready :)")

        val conf = ConfigFactory.load()
        val registry = Registry()
        registry.store(AzureConfig(conf))
        registry.store(TransactionBuilderClientImpl())
        registry.store(InMemoryWorkbenchRepo())

        // processing messages
        Listener(registry).run()

        return app

    }
}

fun Context.booleanQueryParam(name: String, default: Boolean = false): Boolean {
    val param = this.queryParam(name)
    if (param != null) {
        if (param.equals("Y", true)) return true
        if (param.equals("true", true)) return true
        return false
    }
    return default

}


