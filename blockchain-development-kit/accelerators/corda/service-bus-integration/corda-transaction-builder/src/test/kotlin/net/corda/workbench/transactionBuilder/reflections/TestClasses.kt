package net.corda.workbench.transactionBuilder.reflections

import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import java.math.BigDecimal
import java.util.*

/**
 * Various patterns needed to test reflections
 */

data class OneParam(val name: String)

data class TwoParams(val name: String, val age: Int)

data class ThreeParams(val name: String, val age: Int, val address: String = "London")

data class NumericParams(val intParam: Int = 1, val longParam: Long = 1L, val floatParam: Float = 1.0f,
                         val doubleParam: Double = 1.0, val bdParam: BigDecimal = BigDecimal.ONE)

data class NullParams(val name: String?)

data class PartyParam(val party: Party)

data class NestedParams(val one: OneParam, val two: TwoParams, val extra: String)

enum class Colours { Red, Green, Blue }

data class EnumParam(val colour: Colours)

data class FactoryScalarClassParam (val id : UUID)

data class ConstructorScalarClassParam (val one : OneParam)

data class UniqueIdentifierParam (val id : UniqueIdentifier)


class Functions(val observer: StringBuilder = StringBuilder()) {

    fun singleParam(name: String) {
        observer.append("singleParam: $name")
    }

    fun noParamsRetString(): String {
        observer.append("noParamsRetString")
        return "foo"
    }

    fun classParam(one: OneParam) {
        observer.append(one.toString())
    }

    fun listParamWithScalar(list: List<Int>) {
        list.joinTo(observer, ",")
    }

    fun listParamWithObject(list: List<OneParam>) {
        list.joinTo(observer, ",")
    }

    fun listParamWithList(list: List<List<Int>>) {
        list.joinTo(observer, ",")
    }

}
