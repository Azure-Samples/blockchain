package net.corda.reflections.serializers.jackson

import com.natpryce.hamkrest.assertion.*

import com.fasterxml.jackson.databind.ObjectMapper
import com.natpryce.hamkrest.equalTo
import net.corda.core.contracts.Amount
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.CordaX500Name
import net.corda.reflections.reflections.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.util.*

@RunWith(JUnitPlatform::class)
object JacksonSerializerSpec : Spek({

    describe("It should handle Corda specific types in a sensible way") {

        context("Kotlin classes using KClass") {

            val mapper = ObjectMapper()
            mapper.registerModule(Jackson.defaultModule())

            it("should serialize ParamMetaData") {
                fun doit(data: ParamMetaData) = mapper.writeValueAsString(data)

                assert.that(doit(ParamMetaData(String::class)), equalTo("""
                    {"type":"String","optional":false,"nullable":false}
                """.trimIndent()))

                assert.that(doit(ParamMetaData(Int::class, false, true)), equalTo("""
                    {"type":"Int","optional":false,"nullable":true}
                """.trimIndent()))

                assert.that(doit(ParamMetaData(Int::class, true, false)), equalTo("""
                    {"type":"Int","optional":true,"nullable":false}
                """.trimIndent()))
            }

            it("should serialize UniqueIdentifier") {
                val uuid = UUID(1L, 2L)
                fun doit(data: UniqueIdentifier) = mapper.writeValueAsString(data)

                assert.that(doit(UniqueIdentifier(null, uuid)),
                        equalTo("\"00000000-0000-0001-0000-000000000002\""))
                assert.that(doit(UniqueIdentifier("abc", uuid)),
                        equalTo("\"abc_00000000-0000-0001-0000-000000000002\""))
            }

            it("should serialize Amount") {
                val USD = Currency.getInstance("USD")

                fun doit(data: Amount<Any>) = mapper.writeValueAsString(data)

                assert.that(doit(Amount(99, USD)), equalTo("\"USD:99\""))
                assert.that(doit(Amount(10, TestTokenisedAsset())), equalTo("\"TOKEN<TestTokenisedAsset>:10\""))
                assert.that(doit(Amount(123, Object())), equalTo("\"???<Object>:123\""))

            }

            it("should serialize CordaX500Name") {


                fun doit(name: String) = mapper.writeValueAsString(CordaX500Name.parse(name))

                assert.that(doit("O=ContosoLtd,C=US,L=Seattle"), equalTo("\"O=ContosoLtd, L=Seattle, C=US\""))
                assert.that(doit("O=R3LLC,L=London Wall,C=GB"), equalTo("\"O=R3LLC, L=London Wall, C=GB\""))
            }


        }
    }
})