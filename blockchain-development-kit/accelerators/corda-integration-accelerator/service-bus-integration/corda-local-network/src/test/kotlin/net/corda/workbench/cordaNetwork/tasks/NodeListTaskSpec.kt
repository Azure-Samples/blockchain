package net.corda.workbench.cordaNetwork.tasks

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import net.corda.workbench.commons.event.FileEventStore
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.commons.taskManager.TestContext
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.io.File
import kotlin.test.assertFailsWith

@RunWith(JUnitPlatform::class)
object NodeListTaskSpec : Spek({

    val ctx = TestContext("nodelistspec")

    beforeGroup {
        File(ctx.workingDir).deleteRecursively()
        val registry = Registry().store(ctx).store(FileEventStore())

        // comment this out to speed up local tests
        ConfigBuilderTask(registry, listOf("O=Alice,L=New York,C=US", "O=Bob,L=Paris,C=FR")).exec()
        NetworkBootstrapperTask(ctx).exec()
    }

    it("should list all nodes in the network") {
        val nodes = NodeListTask(ctx).exec()
        assert.that(nodes, equalTo(listOf("Alice", "Bob")))
    }

    it("should fail if nodes not found") {
        assertFailsWith(RuntimeException::class) {
            NodeListTask(TestContext()).exec()
        }
    }

})