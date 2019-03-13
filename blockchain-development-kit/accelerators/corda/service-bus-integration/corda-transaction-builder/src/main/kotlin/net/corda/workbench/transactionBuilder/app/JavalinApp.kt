package net.corda.workbench.transactionBuilder.app


import io.javalin.Javalin
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.transactionBuilder.agent.api.*

class JavalinApp(private val port: Int) {

    fun init(registry: Registry): Javalin {

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

        registry.store(app)

        PingApi(registry).register()
        QueryProxyApi(registry).register()
        FlowProxyApi(registry).register()
        ApiController(registry).register()
        EventsController(registry).register(app)
        //WebController(registry).register()
        app.start()
        println("Ready :)")
        return app
    }
}



