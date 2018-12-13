package net.corda.reflections.reflections

import java.math.BigDecimal
import kotlin.reflect.*
import kotlin.reflect.full.isSubclassOf


/**
 * Useful Kotlin extension functions to support cleaner reflections handling code
 */

fun KFunction<*>.mandatoryParamCount(): Int {
    return this.valueParams().filter { !it.isOptional }.count()
}

fun KFunction<*>.totalParamCount(): Int {
    return this.valueParams().filter { it.kind == KParameter.Kind.VALUE }.count()
}

fun KFunction<*>.valueParams(): List<KParameter> {
    return this.parameters.filter { it.kind == KParameter.Kind.VALUE }
}

fun KType.isScalar(): Boolean {
    return (this.classifier == Int::class)
            || (this.classifier == Long::class)
            || (this.classifier == Double::class)
            || (this.classifier == Float::class
            || (this.classifier == Boolean::class))

}

fun KClass<*>.isScalar(): Boolean {
    println(this)
    return (this == Int::class)
            || (this == Long::class)
            || (this == Double::class)
            || (this == String::class)
            || (this == Float::class
            || (this == Boolean::class))
}

fun KClass<*>.isEnum(): Boolean {
    try {
        return this.isSubclassOf(Enum::class)
    } catch (ignored: RuntimeException) {
        return false
    }
}

fun KClass<*>.hasAnnotation(annotation: KClass<*>): Boolean {
    for (a in this.annotations) {
        if (a.annotationClass == annotation) return true
    }
    return false
}


fun Any.coerce(expectedType: KClassifier?): Any {
    try {
        if (this is Long) {
            if (expectedType == Int::class && this <= Int.MAX_VALUE && this >= Int.MIN_VALUE) {
                return this.toInt()
            }
            if (expectedType == Double::class) {
                return this.toDouble()
            }
            if (expectedType == Float::class) {
                return this.toFloat()
            }
            if (expectedType == BigDecimal::class) {
                return this.toBigDecimal()
            }
        }
        if (this is Int) {
            if (expectedType == Long::class) {
                return this.toLong()
            }
            if (expectedType == Double::class) {
                return this.toDouble()
            }
            if (expectedType == Float::class) {
                return this.toFloat()
            }
            if (expectedType == BigDecimal::class) {
                return this.toBigDecimal()
            }
        }
        if (this is Double) {
            if (expectedType == Float::class) {
                return this.toFloat()
            }
            if (expectedType == BigDecimal::class) {
                return this.toBigDecimal()
            }
        }
        if (this is Float) {
            if (expectedType == Double::class) {
                return this.toDouble()
            }
            if (expectedType == BigDecimal::class) {
                return this.toBigDecimal()
            }
        }
    } catch (ignored: RuntimeException) {
    }

    return this
}


