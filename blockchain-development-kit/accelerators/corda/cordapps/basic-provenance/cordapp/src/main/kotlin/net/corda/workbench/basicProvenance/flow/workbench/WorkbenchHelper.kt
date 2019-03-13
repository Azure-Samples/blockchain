package net.corda.workbench.basicProvenance.flow.workbench

import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.reflections.workbench.ContractProperty
import net.corda.reflections.workbench.TxnResult
import net.corda.workbench.basicProvenance.state.ItemState


fun buildWorkbenchTxn(txn: SignedTransaction, me: Party): TxnResult {

    val txnHash = txn.coreTransaction.id

    val output = txn.coreTransaction.outputsOfType<ItemState>().single()
    val otherParties = output.participants - me
    return TxnResult(txnHash = txnHash.toString(),
            owner = me.name,
            otherParties = otherParties.map { it.name},
            contractProperties = buildContractPropertyList(output))
}

fun buildContractPropertyList(_item: ItemState): List<ContractProperty> {
    val result = ArrayList<ContractProperty>()
    result.add(ContractProperty("value", _item.value))
    result.add(ContractProperty("state", _item.state))
    result.add(ContractProperty("creator", _item.creator))
    result.add(ContractProperty("otherParty", _item.otherParty))
    result.add(ContractProperty("Observer", _item.Observer))
    result.add(ContractProperty("linearId", _item.linearId))

    return result
}