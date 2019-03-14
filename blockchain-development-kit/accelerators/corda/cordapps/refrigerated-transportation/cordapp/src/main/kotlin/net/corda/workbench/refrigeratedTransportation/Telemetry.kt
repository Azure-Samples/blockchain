package net.corda.workbench.refrigeratedTransportation

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class Telemetry(val humidity: Int,
                     val temperature: Int,
                     val timestamp: Int = (System.currentTimeMillis() / 1000).toInt()
)
