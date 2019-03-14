package net.corda.workbench.chat

import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable


data class Message(val message: String,
                val interlocutorA: Party,
                val interlocutorB: Party,
                override val linearId: UniqueIdentifier = UniqueIdentifier())
    : LinearState {

    override val participants: List<Party> get() = listOf(interlocutorA, interlocutorB)

}

