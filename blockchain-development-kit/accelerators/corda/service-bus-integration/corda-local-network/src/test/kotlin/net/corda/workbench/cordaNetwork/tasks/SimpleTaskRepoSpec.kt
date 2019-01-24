package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.SimpleTaskRepo
import net.corda.workbench.commons.taskManager.TaskLogMessage
import net.corda.workbench.commons.taskManager.TestContext
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

@RunWith(JUnitPlatform::class)
object SimpleTaskRepoSpec : Spek({

    val ctx = TestContext("simpletaskrepospec")

    beforeGroup {
        File(ctx.workingDir).deleteRecursively()
    }


    it("should log and read tasks ") {
        val executionId = UUID.randomUUID()
        val repo = SimpleTaskRepo("${ctx.workingDir}/tasks")
        println(repo.all())

        val msg1 = TaskLogMessage(executionId,"message 1", UUID.randomUUID())
        repo.store(msg1)
        println(repo.all())

        val msg2 = TaskLogMessage(executionId,"message 2", UUID.randomUUID())
        repo.store(msg2)
        println(repo.all())
    }

})