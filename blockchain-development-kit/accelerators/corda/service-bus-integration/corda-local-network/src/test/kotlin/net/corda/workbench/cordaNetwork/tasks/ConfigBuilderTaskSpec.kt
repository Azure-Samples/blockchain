package net.corda.workbench.cordaNetwork.tasks

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.event.FileEventStore
import net.corda.workbench.commons.taskManager.TestContext
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.io.File
import net.corda.workbench.commons.registry.Registry


@RunWith(JUnitPlatform::class)
object ConfigBuilderTaskSpec : Spek({

    describe("Building a network config") {

        it("should build a simple Alice & Bob network") {
            val ctx = TestContext()
            val registry = Registry().store(ctx).store(FileEventStore())

            val task = ConfigBuilderTask(registry, listOf("O=Notary,L=London,C=GB",
                    "O=Alice,L=New York,C=US",
                    "O=Bob,L=Paris,C=FR"))
            task.exec()

            val expectedFile = mutableSetOf("alice_node.conf", "bob_node.conf", "notary_node.conf")

            for (f in File(ctx.workingDir).walk()) {
                if (f.isFile) {
                    expectedFile.remove(f.name)
                    //println(FileInputStream(f).bufferedReader().use { it.readText() })
                }
            }

            assert.that(expectedFile, equalTo(emptySet<String>()))
        }
    }
})