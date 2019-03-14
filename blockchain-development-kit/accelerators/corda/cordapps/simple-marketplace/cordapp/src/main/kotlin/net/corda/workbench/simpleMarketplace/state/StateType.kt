package net.corda.workbench.simpleMarketplace.state

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
enum class StateType {
    available,
    notavailable,
    approved
}