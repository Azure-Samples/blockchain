package net.corda.reflections.reflections


import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith


@RunWith(JUnitPlatform::class)
object MetaDataSpec : Spek({

    describe("It should extract param metadata via reflections") {

        context("Kotlin classes using KClass") {
            val reflectionsKt = ReflectionsKt()

            it("should extract data for 'OneParam' function") {
                val metadata =
                    reflectionsKt.extractFunctionMetaData(Functions::class, "singleParam")

                assert.that(
                    metadata,
                    equalTo(mapOf("name" to ParamMetaData(String::class)) as Map<String, Any>)
                )
            }

            it("should extract data for 'TwoParam' function") {
                val metadata =
                    reflectionsKt.extractFunctionMetaData(Functions::class, "twoParams")

                assert.that(
                    metadata,
                    equalTo(
                        mapOf(
                            "name" to ParamMetaData(String::class, false, true),
                            "age" to ParamMetaData(Int::class, true, false)
                        ) as Map<String, Any>
                    )
                )
            }

            it("should extract data for 'ClassParam' function") {
                val metadata =
                    reflectionsKt.extractFunctionMetaData(Functions::class, "classParam")

                assert.that(
                    metadata,
                    equalTo(mapOf("one" to mapOf("name" to ParamMetaData(String::class))) as Map<String, Any>)
                )
            }

            it("should extract data for 'resolvedClassParam' function") {
                val metadata =
                    reflectionsKt.extractFunctionMetaData(Functions::class, "resolvedClassParam")

                assert.that(
                    metadata,
                    equalTo(mapOf("party" to ParamMetaData(Party::class)) as Map<String, Any>)
                )
            }

            it("should extract data for 'resolvedClassWithParam' function") {
                val metadata =
                    reflectionsKt.extractFunctionMetaData(Functions::class, "resolvedClassWithParam")

                assert.that(
                    metadata,
                    equalTo(mapOf("idParam" to mapOf("id" to ParamMetaData(UniqueIdentifier::class))) as Map<String, Any>)
                )
            }
        }
    }
})