package net.corda.reflections

import java.security.KeyPair
import java.security.KeyPairGenerator
//
//inline fun <reified T> lookupClass(name: String): Class<T> {
//    // TODO - need to find of locating classes dynamically from just a name
//    //        not that easy in Java
//
//    val lookup = mapOf("shipment" to Shipment::class,
//            "createflow" to CreateFlow::class)
//
//    return lookup.get(name.toLowerCase())?.java as Class<T>
//}


fun keyPair(): KeyPair {

    val keyGen = KeyPairGenerator.getInstance("DSA", "SUN");

    val pair = keyGen.generateKeyPair()
    return pair
}
