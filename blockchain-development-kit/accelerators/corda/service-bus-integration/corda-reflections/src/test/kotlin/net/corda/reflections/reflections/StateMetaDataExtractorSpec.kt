package net.corda.reflections.reflections

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.lang.RuntimeException


@RunWith(JUnitPlatform::class)
object StateMetaDataExtractorSpec : Spek({

    describe("It should extract metadata from States") {

        lateinit var extractor: StateMetaDataExtractor

        beforeEachTest {
            extractor = StateMetaDataExtractor("net.corda.reflections")
        }


        it("should extract all available state") {
            val result = extractor.availableStates()

            assert.that(result, equalTo(listOf("EmptyState", "SimpleState")))
        }

    }
})