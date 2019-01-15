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
object NodeCordaAppsTaskSpec : Spek({

    val ctx = TestContext("nodeappsspec")
    lateinit var registry : Registry

    beforeGroup {
        File(ctx.workingDir).deleteRecursively()
        registry = Registry().store(ctx).store(FileEventStore())

        // comment this out to speed up local tests
        ConfigBuilderTask(registry, listOf("O=Alice,L=New York,C=US")).exec()
        NetworkBootstrapperTask(ctx).exec()
    }

    it("should return empty list for new node") {
        val apps = NodeCordaAppsTask(ctx, "alice").exec()
        assert.that(apps.isEmpty(), equalTo(true))
    }

    it("should return refrigerated transport app details") {

        val app = File("src/test/resources/cordapps/refrigerated-transportation.jar")
        DeployCordaAppTask(registry, app).exec()

        val apps = NodeCordaAppsTask(ctx, "alice").exec()
        assert.that(apps.size, equalTo(1))
        assert.that(apps[0].name, equalTo("refrigerated-transportation.jar"))
        assert.that(apps[0].md5Hash, equalTo("d2d8ef2228619164bd0b576b85964513"))
        assert.that(apps[0].size, equalTo(115782 ))

    }



})