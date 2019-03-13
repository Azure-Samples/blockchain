package net.corda.workbench.cordaNetwork

import io.javalin.Javalin
import net.corda.workbench.commons.event.FileEventStore
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.cordaNetwork.api.ApiController
import net.corda.workbench.cordaNetwork.api.EventsController
import net.corda.workbench.cordaNetwork.api.PingApi

class Javalin(private val port: Int) {

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


        PingApi().register(app)
        ApiController(registry).register(app)
        EventsController(registry).register(app)

        app.start()
        println("Ready :)")

        return app

    }
}



