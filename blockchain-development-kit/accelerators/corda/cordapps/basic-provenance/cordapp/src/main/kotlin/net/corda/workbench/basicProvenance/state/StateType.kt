package net.corda.workbench.basicProvenance.state

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
enum class StateType {

    created,
    onTrasfer,
    completed
}