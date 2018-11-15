package net.corda.workbench.transactionBuilder

import net.corda.workbench.refrigeratedTransportation.Shipment
import net.corda.workbench.refrigeratedTransportation.flow.CreateFlow

inline fun <reified T> lookupClass(name: String): Class<T> {
    // TODO - need to find of locating classes dynamically from just a name
    //        not that easy in Java

    val lookup = mapOf("shipment" to Shipment::class,
            "createflow" to CreateFlow::class)

    @Suppress("UNCHECKED_CAST")
    return lookup.get(name.toLowerCase())?.java as Class<T>
}