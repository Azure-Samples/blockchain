package com.simpleMarketplace.client


import net.corda.client.rpc.CordaRPCClient
import net.corda.core.contracts.StateAndRef
import net.corda.core.identity.CordaX500Name
import net.corda.core.utilities.NetworkHostAndPort
import net.corda.core.utilities.loggerFor
import net.corda.workbench.simpleMarketplace.flow.MarketFlow
import net.corda.workbench.simpleMarketplace.state.AcceptedState
import net.corda.workbench.simpleMarketplace.state.AvailableItem
import org.slf4j.Logger


fun main(args: Array<String>) {
    ExampleClientRPC().main(args)
}

private class ExampleClientRPC {
    companion object {
        val logger: Logger = loggerFor<ExampleClientRPC>()
        private fun logState(state: StateAndRef<AcceptedState>) = logger.info("{}", state.state.data)
    }

    fun main(args: Array<String>) {
        require(args.size == 1) { "Usage: ExampleClientRPC <node address>" }
        val nodeAddress = NetworkHostAndPort.parse(args[0])
        val client = CordaRPCClient(nodeAddress)

        // Can be amended in the com.simpleMarketplace.MainKt file.
        val proxy = client.start("user1", "test").proxy
        val otherpartyName = CordaX500Name("PartyB", "New York", "US")
        val second = proxy.wellKnownPartyFromX500Name(otherpartyName)
        val _item = AvailableItem("XYZ", 800.00)
        val signedTx = proxy.startTrackedFlowDynamic(MarketFlow.Initiator::class.java,_item,800.00, second)
                .returnValue
                .get()

        val msg = String.format("Transaction id %s committed to ledger.\n", signedTx.id)
        // Grab all existing and future IOU states in the vault.
        val (snapshot, updates) = proxy.vaultTrack(AcceptedState::class.java)

        // Log the 'placed' IOU states and listen for new ones.
        snapshot.states.forEach { logState(it) }


    }
}
