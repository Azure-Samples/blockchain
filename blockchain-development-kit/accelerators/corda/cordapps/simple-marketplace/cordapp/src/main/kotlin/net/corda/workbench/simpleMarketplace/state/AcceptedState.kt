package net.corda.workbench.simpleMarketplace.state

import net.corda.core.contracts.ContractState
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable

@CordaSerializable
class AcceptedState(var owner: Party,
                    var buyer: Party,
                    var _item: AvailableItem,
                    var offeredPrice: Double,
                    override val linearId: UniqueIdentifier = UniqueIdentifier()) : ContractState, LinearState {


    override val participants: List<Party> get() = buildParticipants()

    fun buildParticipants(): List<Party> {
        val result = mutableSetOf(owner, buyer)

        return result.toList()
    }



    fun ChangeStateTypeToApproved() {
        this._item._state = StateType.approved
    }
}
