package net.corda.workbench.refrigeratedTransportation

import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class Transfer(val newCounterparty: Party)