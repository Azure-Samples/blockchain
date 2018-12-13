package net.corda.workbench.transactionBuilder.tasks

import com.natpryce.hamkrest.equalTo

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import com.natpryce.hamkrest.assertion.*
import net.corda.workbench.commons.event.FileEventStore
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.commons.taskManager.TestContext
import java.io.File

@RunWith(JUnitPlatform::class)
object StartAgentSpec : Spek({

    val ctx = TestContext("startagentspec")
    val es = FileEventStore()
    val registry = Registry().store(ctx).store(es)


    describe("Read a node config") {

        beforeGroup {
            File("${ctx.workingDir}").deleteRecursively()
            es.truncate()

            val task = DeployCordaAppTask(registry,
                    File("src/test/resources/cordapps/refrigerated-transportation.jar"),
                    "refrigerated-transportation")
            task.exec()
            assert.that(es.retrieve().last().type, equalTo("CordaAppDeployed"))
        }

        it("should start and stop the agent") {

            val startTask = StartAgentTask(registry)
            startTask.exec()

            assert.that(1, equalTo(1))
            assert.that(es.retrieve().last().type, equalTo("AgentStarted"))

            // todo - check the agent is running

            val stopTask = StopAgentTask(registry)
            stopTask.exec()
            assert.that(es.retrieve().last().type, equalTo("AgentStopped"))

            // todo - check the agent is not running


        }

    }
})