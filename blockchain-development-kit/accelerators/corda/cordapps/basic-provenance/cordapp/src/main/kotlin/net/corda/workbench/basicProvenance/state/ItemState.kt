package net.corda.workbench.basicProvenance.state



import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class ItemState(val value: Double,
                     var state: StateType =  StateType.created,
                     val creator: Party,
                     val otherParty:Party?,
                     val Observer: Party,
                     override val linearId: UniqueIdentifier = UniqueIdentifier()):
        LinearState {

    override val participants: List<Party> get() = buildParticipants()


    fun transfer(newCounterParty: Party,_state:ItemState): ItemState {
        return copy(value = _state.value,state = StateType.onTrasfer, creator = _state.creator,otherParty = newCounterParty,Observer = _state.Observer)
    }

    fun transferComplete(newCounterParty: Party,_state:ItemState): ItemState {
        return copy(value = _state.value,state = StateType.completed, creator = _state.otherParty!!,otherParty = newCounterParty,Observer = _state.Observer)
    }



    fun buildParticipants(): List<Party> {

        val result :MutableSet<Party>
        if(otherParty!=null) {
             result = mutableSetOf(creator,otherParty)
        }
        else {
             result = mutableSetOf(creator)
        }
        return result.toList()
    }
}
