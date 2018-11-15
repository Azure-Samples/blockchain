package net.corda.workbench.transactionBuilder.app

import io.javalin.ApiBuilder
import io.javalin.Javalin
import net.corda.core.utilities.loggerFor
import net.corda.workbench.commons.registry.Registry
import org.slf4j.Logger


/**
 * Proxy that simply passes the call onto the agent
 */
class FlowProxyApi(private val registry: Registry) {

    private val logger: Logger = loggerFor<FlowProxyApi>()

    fun register() {
        val app = registry.retrieve(Javalin::class.java)
        val agentRepo = registry.retrieve(AgentRepo::class.java)


        ApiBuilder.path(":network/:node/:app") {

            app.routes {

                ApiBuilder.path("flows/:name") {
                    app.routes {
                        ApiBuilder.post("run") { ctx ->
                            val network = ctx.param("network")!!
                            val node = ctx.param("node")!!
                            val appName = ctx.param("app")!!
                            val name = ctx.param("name")!!
                            val port = agentRepo.agentPort(network)
                            val url = "http://localhost:$port/$network/$node/$appName/flows/$name/run"

                            logger.info("proxing to agent at: $url")
                            val r = khttp.post(url = url, headers = ctx.headerMap(), data = ctx.body())

                            for (h in r.headers) {
                                ctx.header(h.key, h.value)
                            }
                            ctx.status(r.statusCode)
                            ctx.result(r.text)


                        }
                        ApiBuilder.get("metadata") { ctx ->

                            val network = ctx.param("network")!!
                            val node = ctx.param("node")!!
                            val appName = ctx.param("app")!!
                            val name = ctx.param("name")!!
                            val port = agentRepo.agentPort(network)
                            val url = "http://localhost:$port/$network/$node/$appName/flows/$name/metadata"

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