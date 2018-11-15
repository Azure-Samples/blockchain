package net.corda.workbench.refrigeratedTransportation

import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable

/**
 * The state of a Refrigeration Item as it moves around.
 *
 * See https://github.com/Azure-Samples/blockchain/blob/master/blockchain-workbench/application-and-smart-contract-samples/refrigerated-transportation/ethereum/RefrigeratedTransportation.sol
 * for reference model under Ethereum.
 */
data class Shipment(val state: StateType,
                    val owner: Party,
                    val initiatingCounterparty: Party,
                    val counterparty: Party?,
                    val previousCounterparty: Party?,
                    val device: Party,
                    val supplyChainOwner: Party,
                    val supplyChainObserver: Party,
                    val minHumidity: Int,
                    val maxHumidity: Int,
                    val minTemperature: Int,
                    val maxTemperature: Int,
                    val complianceSensorType: SensorType?,
                    val complianceSensorReading: Int?,
                    val complianceStatus: Boolean = true,
                    val complianceDetail: String?,
                    val lastSensorUpdateTimestamp: Int?,
                    override val linearId: UniqueIdentifier = UniqueIdentifier())
    : LinearState {

    constructor(owner: Party, device: Party,
                supplyChainOwner: Party, supplyChainObserver: Party,
                minHumidity: Int, maxHumidity: Int, minTemperature: Int, maxTemperature: Int,
                linearId: UniqueIdentifier = UniqueIdentifier()) : this(state = StateType.Created,
            initiatingCounterparty = owner, owner = owner, counterparty = owner, previousCounterparty = null,
            device = device, supplyChainOwner = supplyChainOwner, supplyChainObserver = supplyChainObserver,
            minHumidity = minHumidity, maxHumidity = maxHumidity, minTemperature = minTemperature, maxTemperature = maxTemperature,
            complianceSensorType = null, complianceSensorReading = null, complianceStatus = true, complianceDetail = null,
            lastSensorUpdateTimestamp = null, linearId = linearId)

    override val participants: List<Party> get() = buildParticipants()

    fun recordTelemtery(telemetry: Telemetry): Shipment {
        if (telemetry.humidity > maxHumidity || telemetry.humidity < minHumidity) {
            return copy(complianceSensorType = SensorType.Humidity,
                    complianceSensorReading = telemetry.humidity,
                    complianceDetail = "Humidity value out of range",
                    complianceStatus = false,
                    lastSensorUpdateTimestamp = telemetry.timestamp,
                    state = StateType.OutOfCompliance)

        }
        if (telemetry.temperature > maxTemperature || telemetry.temperature < minTemperature) {
            return copy(complianceSensorType = SensorType.Temperature,
                    complianceSensorReading = telemetry.temperature,
                    complianceDetail = "Temperature value out of range",
                    complianceStatus = false,
                    lastSensorUpdateTimestamp = telemetry.timestamp,
                    state = StateType.OutOfCompliance)

        }
        return copy(lastSensorUpdateTimestamp = telemetry.timestamp)
    }

    fun transferResponsibility(newCounterParty: Party): Shipment {
        return copy(counterparty = newCounterParty, previousCounterparty = counterparty, state = StateType.InTransit)
    }

    fun completeShipment(): Shipment {
        return copy(previousCounterparty = counterparty, counterparty = null, state = StateType.Completed)
    }

    /**
     * Is this reading within bounds
     */
    fun isCompliantReading(telemetry: Telemetry): Boolean {
        return telemetry.humidity in minHumidity..maxHumidity && telemetry.temperature in minTemperature..maxTemperature
    }

    /**
     * Basic internal status checks based on state. Will normally
     * be more specific checks in the contract & flows
     */
    fun isValidForState(): Boolean {
        when (state) {
            StateType.Created -> {
                return (counterparty == null && previousCounterparty == null
                        && complianceSensorType == null && complianceSensorReading == null)
                        && complianceStatus && complianceDetail == null && lastSensorUpdateTimestamp == null
            }

            else              -> {
                // TODO any rules for checking based on internal state
                return true
            }

        }

    }

    fun buildParticipants(): List<Party> {
        val result = mutableSetOf(owner, device, supplyChainOwner, initiatingCounterparty)
        if (counterparty != null) result.add(counterparty)
        if (previousCounterparty != null) result.add(previousCounterparty)
        return result.toList()
    }

}

@CordaSerializable
enum class SensorType {
    None, Humidity, Temperature
}

@CordaSerializable
enum class StateType {
    Created, InTransit, Completed, OutOfCompliance
}


