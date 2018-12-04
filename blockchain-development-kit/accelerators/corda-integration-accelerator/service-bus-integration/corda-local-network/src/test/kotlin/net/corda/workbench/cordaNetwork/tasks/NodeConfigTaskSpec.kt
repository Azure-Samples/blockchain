package net.corda.workbench.cordaNetwork.tasks

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import net.corda.workbench.commons.taskManager.TestContext
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.io.File
import kotlin.test.assertFailsWith

@RunWith(JUnitPlatform::class)
object NodeConfigTaskSpec : Spek({

    val ctx = TestContext("nodeconfigspec")

    describe("Read a node config") {

        beforeGroup {
            File(ctx.workingDir).deleteRecursively()

            // comment this out to speed up local tests.
            ConfigBuilderTask(ctx, listOf("O=Alice,L=New York,C=US", "O=Bob,L=Paris,C=FR")).exec()
            NetworkBootstrapperTask(ctx).exec()
        }

        it("should read 'alice_node' config") {
            val config = NodeConfigTask(ctx, "alice_node").exec()

            assert.that(config.legalName, equalTo("O=Alice,L=New York,C=US"))
            assert.that(config.port, equalTo(10001))
            assert.that(config.sshPort, equalTo(10004))
        }

        it("should read 'Bob' config") {
            val config = NodeConfigTask(ctx, "Bob").exec()

            assert.that(config.legalName, equalTo("O=Bob,L=Paris,C=FR"))
        }

        it("should allow 'alice_node' or 'Alice' for node name") {
            val config1 = NodeConfigTask(ctx, "alice_node").exec()
            val config2 = NodeConfigTask(ctx, "Alice").exec()

            assert.that(config1, equalTo(config2))
        }

        it("should fail if node not found") {
            assertFailsWith(RuntimeException::class) {
                NodeConfigTask(ctx, "Charlie").exec()
            }
        }
    }
})