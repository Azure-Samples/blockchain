package net.corda.workbench.transactionBuilder.api

import io.javalin.ApiBuilder
import io.javalin.Context
import io.javalin.Javalin
import net.corda.core.contracts.ContractState
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.reflections.app.RPCHelper
import net.corda.workbench.transactionBuilder.lookupClass
import net.corda.reflections.reflections.LiveRpcCaller
import net.corda.reflections.resolvers.UniqueIdentifierResolver
import net.corda.workbench.commons.registry.Registry


class QueryApi (private val registry: Registry){

    // location of the 'corda-local-network' service, which knows about all the nodes.
    val networkManager = "http://corda-local-network:1114"

    fun register() {
        val app = registry.retrieve(Javalin::class.java)


        ApiBuilder.path(":network") {

            app.routes {
                ApiBuilder.path(":node/query") {
                    app.routes {
                        ApiBuilder.get(":state") { ctx ->
                            val config = lookupNodeConfig(ctx)
                            val clazzName = ctx.param("state")!!
                            val clazz = lookupClass<ContractState>(clazzName)

                            // todo - need to inject in the correct IP address - simple case only works on dev
                            val helper = RPCHelper("corda-local-network:${config.port}")
                            helper.connect()
                            val client = helper.cordaRPCOps()!!
                            val caller = LiveRpcCaller(client)


                            val queryCriteria = QueryCriteria.LinearStateQueryCriteria(contractStateTypes = setOf(clazz ))
                            val result = caller.vaultQueryBy(criteria = queryCriteria,contractStateType = clazz)
                            ctx.json(result.states.reversed().map { it.state.data })
                        }
                    }

                    app.routes {
                        ApiBuilder.get(":state/:linearId") { ctx ->
                            val config = lookupNodeConfig(ctx)
                            val clazzName = ctx.param("state")!!
                            val clazz = lookupClass<ContractState>(clazzName)

                            // todo - need to inject in the correct IP address - simple case only works on dev
                            val helper = RPCHelper("corda-local-network:${config.port}")
                            helper.connect()
                            val client = helper.cordaRPCOps()!!
                            val caller = LiveRpcCaller(client)

                            val idParam = ctx.param("linearId")!!

                            val id = UniqueIdentifierResolver().resolveValue(idParam)!!
                            val externalIds = if (id.externalId != null){
                                listOf(id.externalId!!)
                            } else {
                                null
                            }


                            val queryCriteria = QueryCriteria.LinearStateQueryCriteria(
                                    contractStateTypes = setOf(clazz ),
                                    uuid = listOf(id.id),
                                    externalId =externalIds)
                            val result = caller.vaultQueryBy(criteria = queryCriteria,contractStateType = clazz)
                            ctx.json(result.states.reversed().map { it.state.data })
                        }
                    }
                }
            }
        }
    }

    fun lookupNodeConfig(ctx: Context): NodeConfig {
        val network = ctx.param("network")!!
        val node = ctx.param("node")!!
        val result = khttp.get("$networkManager/$network/nodes/$node/config")

        if (result.statusCode == 200) {
            val json = result.jsonObject
            return NodeConfig(legalName = json.getString("legalName"), port = json.getInt("port"))
        } else {
            throw RuntimeException("Cannot read node config for node:'$node' on network:'$network'")
        }
    }

    data class NodeConfig(val legalName: String, val port: Int)

}