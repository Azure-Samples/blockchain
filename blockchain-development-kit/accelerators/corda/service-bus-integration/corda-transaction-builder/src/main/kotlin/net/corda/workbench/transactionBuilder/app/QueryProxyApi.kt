package net.corda.workbench.transactionBuilder.app

import io.javalin.ApiBuilder
import io.javalin.Javalin
import net.corda.core.utilities.loggerFor
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.transactionBuilder.events.Repo
import org.slf4j.Logger


/**
 * Proxy that simply passes the call onto the agent
 */
class QueryProxyApi(private val registry: Registry) {

    private val logger: Logger = loggerFor<QueryProxyApi>()

    fun register() {
        val app = registry.retrieve(Javalin::class.java)
        val repo = registry.retrieve(Repo::class.java)


        ApiBuilder.path(":network") {


            app.routes {
                ApiBuilder.get("ping") { ctx ->
                    val network = ctx.param("network")!!
                    val port = repo.agentPort(network)
                    ctx.result(port.toString())
                }

                ApiBuilder.path(":node/:app/query") {
                    app.routes {
                        ApiBuilder.get(":state") { ctx ->

                            val network = ctx.param("network")!!
                            val node = ctx.param("node")!!
                            val state = ctx.param("state")!!
                            val appName = ctx.param("app")!!
                            val port = repo.agentPort(network)
                            val url = "http://localhost:$port/$network/$node/$appName/query/$state"

                            logger.info("proxing to agent at: $url")
                            val r = khttp.get(url = url, headers = ctx.headerMap())

                            for (h in r.headers) {
                                ctx.header(h.key, h.value)
                            }
                            ctx.status(r.statusCode)
                            ctx.result(r.text)
                        }
                    }

                    app.routes {
                        ApiBuilder.get(":state/:linearId") { ctx ->
                            val network = ctx.param("network")!!
                            val node = ctx.param("node")!!
                            val state = ctx.param("state")!!
                            val linearId = ctx.param("linearId")!!
                            val port = repo.agentPort(network)
                            val url = "http://localhost:$port/$network/$node/query/$state/$linearId"

                            logger.info("proxing to agent at: $url")
                            val r = khttp.get(url = url, headers = ctx.headerMap())

                            for (h in r.headers) {
                                ctx.header(h.key, h.value)
                            }
                            ctx.status(r.statusCode)
                            ctx.result(r.text)
                        }
                    }
                }
            }
        }
    }


}