package net.corda.workbench.simpleMarketplace.state

import net.corda.core.serialization.CordaSerializable


@CordaSerializable
class AvailableItem(var description: String?, var price: Double, var _state: StateType = StateType.available) {





}
