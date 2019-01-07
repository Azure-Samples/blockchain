package net.corda.reflections.reflections


import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith


@RunWith(JUnitPlatform::class)
object FunctionSpec : Spek({

    describe("It should call functions via reflections") {

        context("Kotlin classes using KClass") {
            val reflectionsKt = ReflectionsKt()
            lateinit var observer: StringBuilder
            lateinit var obj: Functions

            beforeEachTest {
                observer = StringBuilder()
                obj = Functions(observer)
            }

            it("should call 'OneParam' function") {
                val params = mapOf("name" to "foo")
                reflectionsKt.callFunction<Unit>(obj, "singleParam", params)

                assert.that(observer.toString(), equalTo("singleParam: foo"))
            }

            it("should not call 'OneParam' function") {
                fun doit (data : Map<String,Any?>) = reflectionsKt.callFunction<Unit>(obj,"singleParam",data)

                assert.that({doit(mapOf("name" to 1))}, throws<RuntimeException>())
                assert.that({doit(mapOf("name" to null))}, throws<RuntimeException>())
                assert.that({doit(mapOf("foo" to "bar"))}, throws<RuntimeException>())
                assert.that({doit(emptyMap())}, throws<RuntimeException>())
                assert.that({doit(mapOf("name" to "foo", "unexpected" to "data"))}, throws<RuntimeException>())
                assert.that({doit(mapOf("unexpected" to "data", "name" to "foo"))}, throws<RuntimeException>())
            }

            it("should call 'noParamsRetString' function") {
                val result = reflectionsKt.callFunction<String>(obj, "noParamsRetString")

                assert.that(result, equalTo("foo"))
                assert.that(observer.toString(), equalTo("noParamsRetString"))
            }

            it("should call 'classParam' function") {
                val params = mapOf("one" to mapOf("name" to "foo"))
                reflectionsKt.callFunction<Unit>(obj, "classParam", params)

                assert.that(observer.toString(), equalTo("OneParam(name=foo)"))
            }


            it("should call 'listParamWithScalar' function") {
                val params = mapOf("list" to listOf(1,2,3))
                reflectionsKt.callFunction<Unit>(obj, "listParamWithScalar", params)

                assert.that(observer.toString(), equalTo("1,2,3"))
            }

            it("should call 'listParamWithObject' function") {
                val params = mapOf("list" to listOf(mapOf("name" to "foo"), mapOf("name" to "bar")))
                reflectionsKt.callFunction<Unit>(obj, "listParamWithObject", params)

                assert.that(observer.toString(), equalTo("OneParam(name=foo),OneParam(name=bar)"))
            }

            // needs some refactoring to support this
            xit("should call 'listParamWithList' function") {
                val params = mapOf("list" to listOf(listOf(1,2,3),listOf(4,5,6)))
                reflectionsKt.callFunction<Unit>(obj, "listParamWithList", params)

                assert.that(observer.toString(), equalTo("1,2,3,4,5,6"))
            }


        }
    }
})