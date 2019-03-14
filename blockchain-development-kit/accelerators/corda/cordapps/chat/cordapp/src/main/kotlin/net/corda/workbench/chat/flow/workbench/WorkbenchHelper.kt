package net.corda.workbench.chat.flow.workbench

import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.reflections.workbench.ContractProperty
import net.corda.reflections.workbench.TxnResult
import net.corda.workbench.chat.Message

fun buildWorkbenchTxn(txn: SignedTransaction, me: Party): TxnResult {

    val txnHash = txn.coreTransaction.id
    val output = txn.coreTransaction.outputsOfType<Message>().single()
    val otherParties = output.participants - me
    return TxnResult(txnHash = txnHash.toString(),
            owner = me.name,
            otherParties = otherParties.map { it.name },
            contractProperties = buildContractPropertyList(output))
}

fun buildContractPropertyList(message: Message): List<ContractProperty> {
    val result = ArrayList<ContractProperty>()
    result.add(ContractProperty("message", message.message))
    result.add(ContractProperty("interlocutorA", message.interlocutorA))
    result.add(ContractProperty("interlocutorB", message.interlocutorB))
    return result
}
