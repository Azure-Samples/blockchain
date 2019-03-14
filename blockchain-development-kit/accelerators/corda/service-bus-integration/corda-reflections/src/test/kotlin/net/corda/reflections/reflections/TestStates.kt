package net.corda.reflections.reflections

import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party


/**
 * Various patterns needed to test reflections
 */


data class EmptyState(override val linearId: UniqueIdentifier = UniqueIdentifier())
    : LinearState {

    override val participants: List<Party> get() = emptyList()
}


data class SimpleState(val partyA : Party, val partyB : Party, val data :String, override val linearId: UniqueIdentifier = UniqueIdentifier())
    : LinearState {

    override val participants: List<Party> get() = listOf(partyA,partyB)
}

data class NotALinearState(val partyA : Party, val partyB : Party, val data :String)