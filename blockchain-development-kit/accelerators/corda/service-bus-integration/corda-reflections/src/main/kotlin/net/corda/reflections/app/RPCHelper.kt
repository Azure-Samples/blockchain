package net.corda.reflections.app

import net.corda.client.rpc.CordaRPCClient
import net.corda.core.identity.Party
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.utilities.NetworkHostAndPort
import org.slf4j.LoggerFactory

class RPCHelper(val address: String) {
    val logger = LoggerFactory.getLogger(RPCHelper::class.qualifiedName)
    lateinit var rpcOps: CordaRPCOps

    fun connect() {
        try {
            val host = address.split(":")[0]
            val port = Integer.parseInt(address.split(":")[1])

            val hostAndPort = NetworkHostAndPort(host, port)

            val client = CordaRPCClient(hostAndPort)

            val proxy = client.start("user1", "test")
            rpcOps = proxy.proxy
        } catch (ex: Exception) {
            logger.error("Problem connecting to RPC on $address", ex)
            throw ex
        }
    }

    fun cordaRPCOps(): CordaRPCOps? {
        return rpcOps
    }

    fun lookupParty(orgname: String): Party {
        for (n in rpcOps.networkMapSnapshot().listIterator()) {
            if (n.legalIdentities.first().name.organisation.equals(orgname, true)) {
                return n.legalIdentities.first()
            }
        }
        throw IllegalArgumentException("Cannot find organisation of $orgname on the network.")
    }


}