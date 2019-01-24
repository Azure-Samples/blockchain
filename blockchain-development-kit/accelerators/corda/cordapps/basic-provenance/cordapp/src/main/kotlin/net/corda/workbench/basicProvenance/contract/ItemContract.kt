package net.corda.workbench.basicProvenance.contract


import net.corda.workbench.basicProvenance.state.ItemState
import net.corda.workbench.basicProvenance.state.StateType
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey



class ItemContract : Contract {
    companion object {
        @JvmStatic
        val ITEM_ID = "net.corda.workbench.basicProvenance.contract.ItemContract"
    }

    override fun verify(tx: LedgerTransaction) {
        val itemCommand = tx.commands.requireSingleCommand<Commands>()
        val setOfSigners = itemCommand.signers.toSet()

        when (itemCommand.value) {
            is Create -> verifyCreate(tx, setOfSigners)
            is Transfer ->verifyTransfer(tx,setOfSigners)
            is TransferComplete->verifyTransferComplete(tx,setOfSigners)
            else -> throw IllegalArgumentException("Unrecognised command.")
        }
    }

    private fun verifyCreate(tx: LedgerTransaction, signers: Set<PublicKey>) = requireThat {
        "No inputs should be consumed when creating an item." using (tx.inputStates.isEmpty())
        "Only one item state should be created." using (tx.outputStates.size == 1)
        val _item = tx.outputStates.single() as ItemState
        "A new item must have value." using (_item.value!=0.0||_item.value!=null)

    }


    private fun verifyTransfer(tx: LedgerTransaction, signers: Set<PublicKey>) = requireThat {
        "Only one item state should be transfer." using (tx.outputStates.size == 1)
        val _item = tx.outputStates.single() as ItemState
        "Item must be set on transfer" using (_item.state==StateType.onTrasfer)
    }


    private fun verifyTransferComplete(tx: LedgerTransaction, signers: Set<PublicKey>) = requireThat {
        "Only one item state should be transfer." using (tx.outputStates.size == 1)
        val _item = tx.outputStates.single() as ItemState
        "Item must be set on transfer" using (_item.state==StateType.completed)
    }

    interface Commands : CommandData
        class Create : TypeOnlyCommandData(), Commands
        class Transfer : TypeOnlyCommandData(), Commands
        class TransferComplete : TypeOnlyCommandData(), Commands
}
