package net.corda.workbench.simpleMarketplace.contract


import net.corda.workbench.simpleMarketplace.state.AcceptedState
import net.corda.workbench.simpleMarketplace.state.StateType
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction


class MarketPlaceContract : Contract {
    companion object {
        @JvmStatic
        val ID = "net.corda.workbench.simpleMarketplace.contract.MarketPlaceContract"
    }


    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands.CreateTransfert>()
        requireThat {
            "No inputs should be consumed when issuing an IOU." using (tx.inputs.isEmpty())
            "Only one output state should be created." using (tx.outputs.size == 1)
            val out = tx.outputsOfType<AcceptedState>().single()
            "The lender and the borrower cannot be the same entity." using (out.owner != out.buyer)
            "All of the participants must be signers." using (command.signers.containsAll(out.participants.map { it.owningKey }))
            "The offerPrice must be equals or lower than the AvailableItem price" using (out.offeredPrice!=null||out.offeredPrice!=0.0)

        }
    }


    interface Commands : CommandData {
        class CreateTransfert : Commands
    }
}
