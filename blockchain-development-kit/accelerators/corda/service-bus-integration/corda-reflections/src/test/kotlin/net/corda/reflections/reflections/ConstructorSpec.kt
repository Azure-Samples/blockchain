package net.corda.reflections.reflections


import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.util.*
import kotlin.reflect.KClass


@RunWith(JUnitPlatform::class)
object ConstructorSpec : Spek({

    describe("It should create objects via their constructor") {

        context("Kotlin classes using KClass") {
            val reflectionsKt = ReflectionsKt()

            it("should construct 'OneParam' class") {
                val params = mapOf("name" to "foo")
                val result = reflectionsKt.invokeConstructor(OneParam::class, params)

                assert.that(result, equalTo(OneParam("foo")))
            }

            it("should not construct 'OneParam' class") {
                fun doit(data: Map<String, Any?>) = reflectionsKt.invokeConstructor(OneParam::class, data)

                assert.that({ doit(mapOf("name" to 1)) }, throws<RuntimeException>())
                assert.that({ doit(mapOf("name" to null)) }, throws<RuntimeException>())
                assert.that({ doit(mapOf("foo" to "bar")) }, throws<RuntimeException>())
                assert.that({ doit(emptyMap()) }, throws<RuntimeException>())
                assert.that({ doit(mapOf("name" to "foo", "unexpected" to "data")) }, throws<RuntimeException>())
                assert.that({ doit(mapOf("unexpected" to "data", "name" to "foo")) }, throws<RuntimeException>())
            }

            it("should construct 'TwoParam' class") {
                val params = mapOf("name" to "foo", "age" to 21)
                val result = reflectionsKt.invokeConstructor(TwoParams::class, params)

                assert.that(result, equalTo(TwoParams("foo", 21)))
            }

            it("should not construct 'TwoParam' class") {
                fun doit(data: Map<String, Any>) = reflectionsKt.invokeConstructor(TwoParams::class, data)

                assert.that({ doit(mapOf("name" to "foo")) }, throws<RuntimeException>())
                assert.that({ doit(mapOf("name" to "foo", "age" to "21")) }, throws<RuntimeException>())
                assert.that({ doit(emptyMap()) }, throws<RuntimeException>())
            }

            it("should construct 'ThreeParam' class with default value") {
                val params = mapOf("name" to "foo", "age" to 21)
                val result = reflectionsKt.invokeConstructor(ThreeParams::class, params)

                assert.that(result, equalTo(ThreeParams("foo", 21, "London")))
            }

            it("should coerce numeric values if possible") {
                fun doit(data: Map<String, Any>) = reflectionsKt.invokeConstructor(NumericParams::class, data)

                // int
                assert.that(doit(mapOf("intParam" to 2L)).intParam, equalTo(2))

                // long
                assert.that(doit(mapOf("longParam" to 2)).longParam, equalTo(2L))

                // float
                assert.that(doit(mapOf("floatParam" to 9.9)).floatParam, equalTo(9.9f))
                assert.that(doit(mapOf("floatParam" to 99)).floatParam, equalTo(99f))
                assert.that(doit(mapOf("floatParam" to 99L)).floatParam, equalTo(99f))

                // double
                assert.that(doit(mapOf("doubleParam" to 1.0f)).doubleParam, equalTo(1.0))
                assert.that(doit(mapOf("doubleParam" to 1)).doubleParam, equalTo(1.0))
                assert.that(doit(mapOf("doubleParam" to 1L)).doubleParam, equalTo(1.0))

                // big decimal
                assert.that(doit(mapOf("bdParam" to 1)).bdParam, equalTo(BigDecimal.ONE))
                assert.that(doit(mapOf("bdParam" to 1L)).bdParam, equalTo(BigDecimal.ONE))
                assert.that(doit(mapOf("bdParam" to 1.1)).bdParam, equalTo(BigDecimal.valueOf(11,1)))
                assert.that(doit(mapOf("bdParam" to 1.1f)).bdParam, equalTo(BigDecimal.valueOf(11,1)))

            }

            it("should not coerce numeric values if not matching") {
                fun doit(data: Map<String, Any>) = reflectionsKt.invokeConstructor(NumericParams::class, data)

                assert.that(doit(mapOf("intParam" to 2147483647L)).intParam, equalTo(2147483647))
                assert.that({doit(mapOf("intParam" to 2147483647L+1))}, throws<RuntimeException>())

            }

            it("should construct 'NullParams' class ") {
                fun doit(name: Any?) = reflectionsKt.invokeConstructor(NullParams::class, mapOf("name" to name))

                assert.that(doit("alice"), equalTo(NullParams("alice")))
                assert.that(doit(null), equalTo(NullParams(null)))
            }

            it("should construct 'NestParams' class ") {
                val params = mapOf("one" to mapOf("name" to "alice"),
                        "two" to mapOf("name" to "bob", "age" to 21),
                        "extra" to "hello world")
                val result = reflectionsKt.invokeConstructor(NestedParams::class, params)

                assert.that(result, equalTo(NestedParams(OneParam("alice"), TwoParams("bob", 21), "hello world")))
            }

            it("should construct 'EnumParam' class ") {
                fun doit(colour: String) = reflectionsKt.invokeConstructor(EnumParam::class, mapOf("colour" to colour))

                assert.that(doit("Red"), equalTo(EnumParam(Colours.Red)))
                assert.that({doit("red")}, throws<RuntimeException>())
            }

            it("should construct 'FactoryScalarClassParam' class ") {
                // this tests the static Factory method form
                val uuid = UUID.randomUUID()
                fun doit(id: Any) = reflectionsKt.invokeConstructor(FactoryScalarClassParam::class, mapOf("id" to id))

                assert.that(doit(uuid.toString()), equalTo(FactoryScalarClassParam(uuid)))
                assert.that({doit("not a uuid")}, throws<RuntimeException>())
                assert.that({doit(123)}, throws<RuntimeException>())

            }

            it("should construct 'ConstructorScalarClassParam' class ") {
                // this tests the constructor form
                fun doit(data: Map<String,Any?>) = reflectionsKt.invokeConstructor(ConstructorScalarClassParam::class, data)

                assert.that(doit(mapOf("one" to "Alice")), equalTo(ConstructorScalarClassParam(OneParam("Alice"))))

                // note that in this case the full (nested object) form is also possible
                assert.that(doit(mapOf("one" to mapOf("name" to "Alice"))), equalTo(ConstructorScalarClassParam(OneParam("Alice"))))
            }



            it("should return a list of constructor params in ordinal order") {
                val params = mapOf("name" to "foo",  "address" to "London", "age" to 21)
                @Suppress("UNCHECKED_CAST")
                val result = reflectionsKt.buildConstructorParams(ThreeParams::class as KClass<Any>, params)

                assert.that(result, equalTo(listOf("foo", 21, "London") as List<Any?>))
            }

        }
    }
})