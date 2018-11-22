package net.corda.workbench.transactionBuilder.api

import io.javalin.ApiBuilder
import io.javalin.Context
import io.javalin.Javalin

import net.corda.reflections.app.RPCHelper
import net.corda.reflections.reflections.FlowMetaDataExtractor
import net.corda.reflections.reflections.FlowRunner
import net.corda.reflections.reflections.LiveRpcCaller
import net.corda.reflections.resolvers.RpcPartyResolver
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.transactionBuilder.CordaAppConfig
import net.corda.workbench.transactionBuilder.CordaAppLoader
import net.corda.workbench.transactionBuilder.booleanQueryParam

import org.json.JSONObject
import rx.functions.Action1
import javax.servlet.http.HttpServletResponse


class FlowApi(private val registry: Registry) {
    val networkManager: String = "http://corda-local-network:1114"
    val loader: CordaAppLoader = CordaAppLoader().scan()


    fun register() {
        val app = registry.retrieve(Javalin::class.java)
        ApiBuilder.path(":network/:node/:app") {

            app.routes {
                ApiBuilder.path("flows/:name") {
                    app.routes {
                        ApiBuilder.post("run") { ctx ->
                            val nodeConfig = lookupNodeConfig(ctx)

                            //val appConfig = lookupAppConfig(ctx) ?: throw RuntimeException("Couldn't locate a config. Does your CordApp have a 'META-INF/services/net/corda/workbench/Registry.json file?")

                            val helper = RPCHelper("corda-local-network:${nodeConfig.port}")
                            helper.connect()
                            val client = helper.cordaRPCOps()!!
                            val resolver = RpcPartyResolver(helper)


                            val runner = FlowRunner("net.corda",
                                    resolver,
                                    LiveRpcCaller(client),
                                    Reporter(ctx.response()))

                            val data = JSONObject(ctx.body()).toMap()

                            val flowName = ctx.param("name")!!
                            println("running $flowName with ${ctx.body()}")

                            val result = runner.run<Any>(ctx.param("name")!!, data)
                            if (result != null){
                                println(result)
                                ctx.json(result)

                            }
                            else {
                                ctx.json(mapOf("message" to "failed or timed out making call to flow class"))
                            }
                        }

                        ApiBuilder.get("metadata") { ctx ->

                            val appConfig = lookupAppConfig(ctx)!!
                            val extractor = FlowMetaDataExtractor(appConfig.scannablePackages[0])


                            if (ctx.booleanQueryParam("all")) {
                                val result = extractor.allConstructorMetaData(ctx.param("name")!!)
                                ctx.json(result)

                            } else {
                                val result = extractor.primaryConstructorMetaData(ctx.param("name")!!)
                                ctx.json(result)
                            }

                        }
                    }
                }
            }
        }
    }

    private fun lookupNodeConfig(ctx: Context): NodeConfig {
        val network = ctx.param("network")!!
        val node = ctx.param("node")!!
        val result = khttp.get("$networkManager/$network/nodes/$node/config")

        if (result.statusCode == 200) {
            val json = result.jsonObject
            return NodeConfig(legalName = json.getString("legalName"), port = json.getInt("port"))
        } else {
            throw RuntimeException("Cannot read node config for node:$node on network:$network")
        }
    }

    fun lookupAppConfig(ctx: Context): CordaAppConfig? {
        val app = ctx.param("app")!!
        return loader.findApp(app)

    }

    data class NodeConfig(val legalName: String, val port: Int)


}


class Reporter(val resp: HttpServletResponse) : Action1<String> {
    override fun call(t: String?) {
        resp.writer.print(t!!)
        resp.writer.print("\n")
        resp.writer.flush()
    }
}
