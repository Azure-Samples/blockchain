package net.corda.workbench.transactionBuilder.tasks

import com.natpryce.hamkrest.equalTo
import net.corda.workbench.commons.event.FileEventStore
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.commons.taskManager.TestContext
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.io.File
import com.natpryce.hamkrest.assertion.*
import com.natpryce.hamkrest.throws
import org.jetbrains.spek.api.dsl.xit
import java.util.*


@RunWith(JUnitPlatform::class)
object DeployCordaAppTaskSpec : Spek({

    val ctx = TestContext("deploycordaapptaskspec")
    val es = FileEventStore()
    val registry = Registry().store(ctx).store(es)


    describe("Deploy a corda app") {


        it("should generate registration event from the app details") {

            File("${ctx.workingDir}").deleteRecursively()
            es.truncate()

            val task = DeployCordaAppTask(registry,
                    File("../../cordapps/chat/lib/chat.jar"),
                    "Chat")
            task.exec()

            val ev = es.retrieve().last()
            assert.that(ev.type, equalTo("CordaAppDeployed"))
            assert.that(ev.payload, equalTo(mapOf<String, Any?>("appname" to "Chat",
                    "appId" to "312651fe-90ee-4f10-b6bf-cd9ff62ae5c8",
                    "network" to "deploycordaapptaskspec",
                    "scannablePackages" to  listOf("net.corda.workbench.chat"))))

        }

        it("should fail if no 'registry.json' file ") {

            File("${ctx.workingDir}").deleteRecursively()
            es.truncate()

            val task = DeployCordaAppTask(registry,
                    File("src/test/resources/cordapps/badapp.jar"),
                    "badapp")

            assert.that({ task.exec() }, throws<RuntimeException>())
        }

    }
})