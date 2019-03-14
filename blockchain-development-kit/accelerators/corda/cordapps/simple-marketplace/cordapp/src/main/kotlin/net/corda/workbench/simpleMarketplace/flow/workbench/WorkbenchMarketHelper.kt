package net.corda.workbench.simpleMarketplace.flow.workbench

import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.reflections.workbench.ContractProperty
import net.corda.reflections.workbench.TxnResult


import net.corda.workbench.simpleMarketplace.state.AcceptedState


fun buildWorkbenchMarketTxn(txn: SignedTransaction, me: Party): TxnResult {

    val txnHash = txn.coreTransaction.id

    val output = txn.coreTransaction.outputsOfType<AcceptedState>().single()
    val otherParties = output.participants - me
    return TxnResult(txnHash = txnHash.toString(),
            owner = me.name,
            otherParties = otherParties.map { it.name},
            contractProperties = buildContractPropertyList(output))
}

fun buildContractPropertyList(_acceptedSt: AcceptedState): List<ContractProperty> {
    val result = ArrayList<ContractProperty>()
    result.add(ContractProperty("AvailableItem", _acceptedSt._item))
    result.add(ContractProperty("owner", _acceptedSt.owner))
    result.add(ContractProperty("buyer", _acceptedSt.buyer))
    result.add(ContractProperty("offeredPrice", _acceptedSt.offeredPrice))

    return result
}