package net.corda.workbench.basicProvenance.client

import net.corda.workbench.basicProvenance.flow.CreateFlow
import net.corda.workbench.basicProvenance.flow.TransferFlow
import net.corda.workbench.basicProvenance.state.ItemState
import net.corda.workbench.basicProvenance.state.StateType
import net.corda.client.rpc.CordaRPCClient
import net.corda.core.contracts.StateAndRef
import net.corda.core.identity.CordaX500Name
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.utilities.NetworkHostAndPort
import net.corda.core.utilities.loggerFor
import org.slf4j.Logger


fun main(args: Array<String>) {
    ExampleClientRPC().main(args)
}

private class ExampleClientRPC {
    companion object {
        val logger: Logger = loggerFor<ExampleClientRPC>()
        private fun logState(state: StateAndRef<ItemState>) = logger.info("{}", state.state.data)
    }

    fun main(args: Array<String>) {
        require(args.size == 1) { "Usage: ExampleClientRPC <node address>" }
        val nodeAddress = NetworkHostAndPort.parse(args[0])
        val client = CordaRPCClient(nodeAddress)


        val proxy = client.start("user1", "test").proxy

        val otherpartyName = CordaX500Name("PartyA", "London", "GB")
        val second = proxy.wellKnownPartyFromX500Name(otherpartyName)

        val observerName = CordaX500Name("PartyC", "Paris", "FR")
        val observer = proxy.wellKnownPartyFromX500Name(observerName)

        val newcouterPartyName = CordaX500Name("PartyB", "New York", "US")
        val newcouterParty = proxy.wellKnownPartyFromX500Name(newcouterPartyName)

        val _item = ItemState(100.0,StateType.created,creator = second!!,otherParty = null,Observer = observer!!)

        val signedTx = proxy.startTrackedFlowDynamic(CreateFlow::class.java,_item)
                .returnValue
                .get()


        val sTx = proxy.startTrackedFlowDynamic(TransferFlow::class.java,_item.linearId,newcouterParty)
                .returnValue
                .get()

        val (snapshot, updates) = proxy.vaultTrack(ItemState::class.java)
        val vaultSnapshot = proxy.vaultQueryBy<ItemState>(
                QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED))
    }
}
