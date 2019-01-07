package net.corda.reflections.reflections

import kotlin.reflect.KClass

/**
 * The metadata actionAvailable for a single param
 */
data class ParamMetaData(val kclazz : KClass<*>,
                         val optional : Boolean = false,
                         val nullable : Boolean= false) {

}
