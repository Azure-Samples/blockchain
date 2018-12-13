package net.corda.workbench.refrigeratedTransportation.api

import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.hash
import net.corda.core.identity.Party
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.node.NodeInfo
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.utilities.loggerFor
import net.corda.workbench.refrigeratedTransportation.Shipment
import net.corda.workbench.refrigeratedTransportation.Telemetry
import net.corda.workbench.refrigeratedTransportation.Transfer
import net.corda.workbench.refrigeratedTransportation.flow.CompleteFlow
import net.corda.workbench.refrigeratedTransportation.flow.CreateFlow
import net.corda.workbench.refrigeratedTransportation.flow.IngestTelemetryFlow
import net.corda.workbench.refrigeratedTransportation.flow.TransferResponsibilityFlow
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x500.style.BCStyle
import org.json.JSONObject
import org.slf4j.Logger
import java.util.Base64
import java.util.LinkedHashMap
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * This API is accessible from /api/marketplace. The endpoint paths specified below are relative to it.
 * We've defined a bunch of endpoints to deal with IOUs, cash and the various operations you can perform with them.
 */
@Path("transportation")
class TransportationApi(private val rpcOps: CordaRPCOps) {
    private val me = rpcOps.nodeInfo().legalIdentities.first().name

    companion object {
        private val logger: Logger = loggerFor<TransportationApi>()
    }

    /**
     * Returns the node's name.
     */
    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    fun whoAmI() = mapOf("me" to me.toString())

    /**
     * Returns all parties registered with the [NetworkMapService]. These names can be used to look up identities
     * using the [IdentityService].
     */
    @GET
    @Path("peers")
    @Produces(MediaType.APPLICATION_JSON)
    fun getPeers(): Map<String, List<String>> {

        return mapOf("peers" to rpcOps.networkMapSnapshot()
                .filter { isNotary(it).not() && isMe(it).not() && isNetworkMap(it).not() }
                .map { it.legalIdentities.first().name.x500Principal.toString() })
    }

    @GET
    @Path("shipments")
    @Produces(MediaType.APPLICATION_JSON)
    fun getShipments(): List<Map<String, Any>> {
        return rpcOps.vaultQueryBy<Shipment>().states.map {
            linkedMapOf("id" to it.state.data.linearId,
                    "state" to it.state.data.state,
                    "owner" to it.state.data.owner.name.organisation)
        }
    }

    @GET
    @Path("shipments/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getShipment(@PathParam(value = "id") id: String): List<StateAndRef<ContractState>> {
        val linearId = findLinearId(id)
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(linearId!!))
        return rpcOps.vaultQueryBy<Shipment>(queryCriteria).states
    }

    @GET
    @Path("shipments/{id}/states")
    @Produces(MediaType.APPLICATION_JSON)
    fun getShipmentHistory(@PathParam(value = "id") id: String): List<StateAndRef<ContractState>> {
        val linearId = findLinearId(id)
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(linearId!!), status = Vault.StateStatus.ALL)
        return rpcOps.vaultQueryBy<Shipment>(queryCriteria).states
    }

    @GET
    @Path("shipments/{id}/state")
    @Produces(MediaType.TEXT_PLAIN)
    fun getShipmentStatus(@PathParam(value = "id") id: String): String {
        val linearId = findLinearId(id)
        val me = rpcOps.nodeInfo().legalIdentities.first()

        var status = "???"
        if (linearId != null) {
            val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(linearId))
            val result = rpcOps.vaultQueryBy<Shipment>(queryCriteria)
            status = result.states.first().state.data.state.name
        }

        return "${me.name.organisation} has $status state for id $linearId\n"
    }

    @GET
    @Path("shipments/{id}/states/summary")
    @Produces(MediaType.APPLICATION_JSON)
    fun getShipmentHistorySummary(@PathParam(value = "id") id: String): List<Map<String, Any?>> {
        val linearId = findLinearId(id)
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(linearId!!), status = Vault.StateStatus.ALL)
        return rpcOps.vaultQueryBy<Shipment>(queryCriteria).states.map {
            val d = it.state.data

            linkedMapOf(
                    "state" to d.state,
                    "owner" to d.owner.name.organisation,
                    "initiatingCounterparty" to d.initiatingCounterparty.name.organisation,
                    "counterparty" to d.counterparty?.name?.organisation,
                    "previousCounterparty" to d.previousCounterparty?.name?.organisation,
                    "device" to d.device.name.organisation,
                    "supplyChainOwner" to d.supplyChainOwner.name.organisation,
                    "supplyChainObserver" to d.supplyChainObserver.name.organisation,
                    "complianceSensorType" to d.complianceSensorType,
                    "complianceSensorReading" to d.complianceSensorReading,
                    "complianceStatus" to d.complianceStatus,
                    "complianceDetail" to d.complianceDetail,
                    "lastSensorUpdateTimestamp" to d.lastSensorUpdateTimestamp
            )
        }
    }

    @GET
    @Path("shipments/{id}/states/count")
    @Produces(MediaType.TEXT_PLAIN)
    fun getShipmentHistoryCount(@PathParam(value = "id") id: String): String {
        val linearId = findLinearId(id)
        val me = rpcOps.nodeInfo().legalIdentities.first()
        var count = 0
        if (linearId != null) {
            val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(linearId), status = Vault.StateStatus.ALL)
            count = rpcOps.vaultQueryBy<Shipment>(queryCriteria).states.size
        }
        return "${me.name.organisation} has $count states for id $id \n"
    }

    @PUT
    @Path("shipments")
    @Produces(MediaType.APPLICATION_JSON)
    fun createShipment(shipmentDefinitionJson: String): Response {
        return doCreateShipment(shipmentDefinitionJson, UniqueIdentifier())
    }

    @PUT
    @Path("shipments/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun createShipmentWithId(@PathParam(value = "id") id: String, shipmentDefinitionJson: String): Response {
        return doCreateShipment(shipmentDefinitionJson, UniqueIdentifier(id))
    }

    @PUT
    @Path("shipments/{id}/telemetry")
    @Produces(MediaType.APPLICATION_JSON)
    fun recordTelemetry(@PathParam(value = "id") id: String, telemetryJson: String): Response {
        try {

            val linearId = findLinearId(id)

            val json = JSONObject(telemetryJson)
            val humidity = getInt(json, "humidity", 50)
            val temperature = getInt(json, "temperature", 10)
            val telemetry = Telemetry(humidity = humidity, temperature = temperature)

            val result = rpcOps.startTrackedFlowDynamic(IngestTelemetryFlow::class.java, linearId, telemetry).returnValue.get()

            return standardResponse(mapOf("id" to result.id.toString()))

        } catch (e: Exception) {
            return standardErrorResponse(e)
        }
    }

    @PUT
    @Path("shipments/{id}/transfer")
    @Produces(MediaType.APPLICATION_JSON)
    fun transferResponsibility(@PathParam(value = "id") id: String, transfer: String): Response {
        return try {
            val json = JSONObject(transfer)
            val newCounterParty = lookupParty(json.getString("newCounterParty"))
            val transferingTo = Transfer(newCounterParty)

            val linearId = findLinearId(id)
            val result = rpcOps.startTrackedFlowDynamic(TransferResponsibilityFlow::class.java, linearId, transferingTo).returnValue.get()

            val reply = mapOf(
                    "message" to "success",
                    "signedTransaction" to result
            )
            standardResponse(reply)
        } catch (e: Exception) {
            standardErrorResponse(e)
        }
    }

    @PUT
    @Path("shipments/{id}/complete")
    @Produces(MediaType.APPLICATION_JSON)
    fun completeShipment(@PathParam(value = "id") id: String): Response {
        return try {
            println(id)

            val linearId = findLinearId(id)
            val result = rpcOps.startTrackedFlowDynamic(CompleteFlow::class.java, linearId).returnValue.get()

            standardResponse(mapOf("id" to result.id.toString()))

        } catch (e: Exception) {
            standardErrorResponse(e)
        }
    }

    @GET
    @Path("network-map")
    @Produces(MediaType.APPLICATION_JSON)
    fun getNetworkMap(): Response {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(rpcOps.networkMapFeed().snapshot.get(0).legalIdentitiesAndCerts)
                .build()
    }

    fun doCreateShipment(shipmentDefinitionJson: String, id: UniqueIdentifier): Response {
        val me = rpcOps.nodeInfo().legalIdentities.first()
        try {

            val json = JSONObject(shipmentDefinitionJson)
            val minHumidity = getInt(json, "minHumidity", 10)
            val maxHumidity = getInt(json, "maxHumidity", 90)
            val minTemperature = getInt(json, "minTemperature", 5)
            val maxTemperature = getInt(json, "maxTemperature", 20)
            val device = lookupParty(json.getString("device"))
            val supplyChainOwner = lookupParty(json.getString("supplyChainOwner"))
            val supplyChainObserver = lookupParty(json.getString("supplyChainObserver"))

            val item = Shipment(owner = me, device = device,
                    supplyChainOwner = supplyChainOwner, supplyChainObserver = supplyChainObserver,
                    minHumidity = minHumidity, maxHumidity = maxHumidity,
                    minTemperature = minTemperature, maxTemperature = maxTemperature,
                    linearId = id)

            val result = rpcOps.startTrackedFlowDynamic(CreateFlow::class.java, item).returnValue.get()

            result.coreTransaction.id

            val hash = String(Base64.getEncoder().encode(result.coreTransaction.outputStates[0].hash().bytes))
            //val timestamp = result.coreTransaction.inputs[0].
            //val hash = result.coreTransaction.outputStates[0].hash().bytes

            val reply = mapOf("id" to result.id.toString(),
                    "linearId" to item.linearId.toString(),
                    "transactionId" to result.coreTransaction.id.toString(),
                    "outputState" to hash
                    //"transactionId" to result..id.toString()
            )

            return standardResponse(reply)
        } catch (e: Exception) {
            return standardErrorResponse(e)
        }
    }

    private fun X500Name.toDisplayString(): String = BCStyle.INSTANCE.toString(this)

    /** Helpers for filtering the network map cache. */
    private fun isNotary(nodeInfo: NodeInfo) = rpcOps.notaryIdentities().any { nodeInfo.isLegalIdentity(it) }

    private fun isMe(nodeInfo: NodeInfo) = nodeInfo.legalIdentities.first().name == me
    private fun isNetworkMap(nodeInfo: NodeInfo) = nodeInfo.legalIdentities.single().name.organisation == "Network Map Service"

    private fun standardErrorResponse(e: Exception): Response {
        val reply = linkedMapOf("success" to false, "message" to e.message)
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(JSONObject(reply).toString(2))
                .build()
    }

    private fun standardResponse(data: Map<String, Any>): Response {
        val reply = LinkedHashMap(data)
        reply.put("success", true)
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(JSONObject(reply).toString(2))
                .build()
    }

    private fun lookupParty(orgname: String): Party {
        for (n in rpcOps.networkMapSnapshot().listIterator()) {
            if (n.legalIdentities.first().name.organisation == orgname) {
                return n.legalIdentities.first()
            }
        }
        throw IllegalArgumentException("Cannot find organisation of $orgname on the network.")
    }

    private fun getInt(json: JSONObject, field: String, default: Int): Int =
            if (json.has(field)) json.getInt(field) else default

    private fun findLinearId(id: String): UniqueIdentifier? {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria()
        val page = rpcOps.vaultQueryBy<Shipment>(queryCriteria)

        return page.states.find { state ->
            val realId = state.state.data.linearId
            id == realId.externalId || id == realId.id.toString()
        }?.state?.data?.linearId
    }
}