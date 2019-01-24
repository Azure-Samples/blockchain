package net.corda.workbench.refrigeratedTransportation.flow.workbench

import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.reflections.workbench.ContractProperty
import net.corda.reflections.workbench.TxnResult
import net.corda.workbench.refrigeratedTransportation.Shipment

fun buildWorkbenchTxn(txn: SignedTransaction, me: Party): TxnResult {

    val txnHash = txn.coreTransaction.id
    val output = txn.coreTransaction.outputsOfType<Shipment>().single()
    val otherParties = output.participants - me
    return TxnResult(txnHash = txnHash.toString(),
            owner = me.name,
            otherParties = otherParties.map { it.name },
            contractProperties = buildContractPropertyList(output))
}

fun buildContractPropertyList(shipment: Shipment): List<ContractProperty> {
    val result = ArrayList<ContractProperty>()
    result.add(ContractProperty("state", shipment.state.name))
    result.add(ContractProperty("owner", shipment.owner))
    result.add(ContractProperty("initiatingCounterparty", shipment.initiatingCounterparty))
    result.add(ContractProperty("counterparty", shipment.counterparty))
    result.add(ContractProperty("previousCounterparty", shipment.previousCounterparty))
    result.add(ContractProperty("device", shipment.device))
    result.add(ContractProperty("supplyChainOwner", shipment.supplyChainOwner))
    result.add(ContractProperty("supplyChainObserver", shipment.supplyChainObserver))
    result.add(ContractProperty("minHumidity", shipment.minHumidity))
    result.add(ContractProperty("maxHumidity", shipment.maxHumidity))
    result.add(ContractProperty("minTemperature", shipment.minTemperature))
    result.add(ContractProperty("maxTemperature", shipment.maxTemperature))
    result.add(ContractProperty("complianceSensorType", shipment.complianceSensorType?.name))
    result.add(ContractProperty("complianceSensorReading", shipment.complianceSensorReading))
    result.add(ContractProperty("complianceStatus", shipment.complianceStatus))
    result.add(ContractProperty("complianceDetail", shipment.complianceDetail))
    result.add(ContractProperty("lastSensorUpdateTimestamp", shipment.lastSensorUpdateTimestamp))
    return result
}
