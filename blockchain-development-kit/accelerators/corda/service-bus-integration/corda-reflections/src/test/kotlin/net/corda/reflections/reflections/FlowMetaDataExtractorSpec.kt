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


@RunWith(JUnitPlatform::class)
object FlowMetaDataExtractorSpec : Spek({

    describe("It should extract metadata from flows") {

            lateinit var extractor: FlowMetaDataExtractor

            beforeEachTest {
                extractor = FlowMetaDataExtractor("net.corda.reflections")

            }

            it("should extract metadata for SimpleFlow") {
                val result = extractor.primaryConstructorMetaData( "SimpleFlow")

                assert.that(result, equalTo(mapOf("data" to ParamMetaData(String::class)) as Map<String,Any>))
            }

        it("should extract all metadata for MultipleConstructorFlow") {
            val result = extractor.allConstructorMetaData( "MultipleConstructorFlow")

            assert.that(result, equalTo(listOf(
                    mapOf("params" to mapOf("name" to ParamMetaData(String::class), "age" to ParamMetaData(Int::class)) as Map<String,Any>),
                    mapOf("p1" to ParamMetaData(String::class), "p2" to ParamMetaData(Int::class)) as Map<String,Any>
            )))

        }





    }
})