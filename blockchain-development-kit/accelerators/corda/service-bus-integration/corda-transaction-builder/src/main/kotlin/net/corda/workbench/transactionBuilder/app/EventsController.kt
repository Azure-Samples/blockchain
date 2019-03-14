package net.corda.workbench.transactionBuilder.app

import io.javalin.ApiBuilder
import io.javalin.Javalin
import net.corda.workbench.commons.event.EventStore

import net.corda.workbench.commons.registry.Registry


class EventsController(private val registry: Registry) {
    val es = registry.retrieve(EventStore::class.java)

    fun register(app: Javalin) {

        ApiBuilder.path("events") {

            app.routes {

                ApiBuilder.get("all") { ctx ->
                    ctx.json(es.retrieve().reversed())
                }

                ApiBuilder.get("tail") { ctx ->
                    val allEvents = es.retrieve()
                    if (allEvents.size > 20) {
                        ctx.json(allEvents.reversed().subList(0, 20))
                    } else {
                        ctx.json(allEvents.reversed())
                    }
                }
            }
        }
    }
}