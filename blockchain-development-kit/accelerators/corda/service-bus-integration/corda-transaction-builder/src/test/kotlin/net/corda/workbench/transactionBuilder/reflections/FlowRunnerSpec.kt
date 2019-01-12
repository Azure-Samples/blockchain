package net.corda.workbench.transactionBuilder.reflections


import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import net.corda.reflections.reflections.FlowRunner
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith


@RunWith(JUnitPlatform::class)
object FlowRunnerSpec : Spek({

    describe("It should invoke flows via reflections") {

        context("Kotlin classes using KClass") {
            lateinit var runner: FlowRunner

            beforeEachTest {
                runner = FlowRunner("net.corda.workbench.transactionBuilder.reflections")

                //_globalSerializationEnv.set(SerializationEnvironmentImpl())
            }

            it("should call simple flow") {
                val params = mapOf("data" to "foo")
                val result = runner.run<String>( "SimpleFlow", params)

                assert.that(result, equalTo("FOO"))
            }

            it("should not call flows without necessary annotations") {
                assert.that ({runner.run<String>( "NotRpcFlow")}, throws<RuntimeException>())
                assert.that ({runner.run<String>( "NotInitiatingFlow")}, throws<RuntimeException>())
            }


        }
    }
})