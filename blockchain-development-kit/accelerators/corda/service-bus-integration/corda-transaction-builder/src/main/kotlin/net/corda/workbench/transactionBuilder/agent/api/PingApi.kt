package net.corda.workbench.transactionBuilder.agent.api

import io.javalin.ApiBuilder
import io.javalin.Javalin
import net.corda.workbench.commons.registry.Registry


class PingApi (private val registry: Registry) {

    fun register() {
        val app = registry.retrieve(Javalin::class.java)
        app.routes {
            ApiBuilder.get("ping") { ctx ->
                ctx.result("pong")
            }
        }
    }
}