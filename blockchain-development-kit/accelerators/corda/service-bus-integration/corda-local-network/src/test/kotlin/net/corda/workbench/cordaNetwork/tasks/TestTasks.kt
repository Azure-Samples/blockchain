package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.BaseTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskLogMessage

class TestTask : BaseTask() {
    override fun exec(executionContext: ExecutionContext) {
        executionContext.messageStream.invoke("executing...")
    }
}

class FailingTask : BaseTask() {
    override fun exec(executionContext: ExecutionContext) {
        throw RuntimeException("forced an error")
    }
}

class TestMessageSink {
    private val logs = ArrayList<TaskLogMessage>()

    fun sink(t: TaskLogMessage) {
        logs.add(t)
        System.out.println(t)
    }

    fun messages(): List<String> {
        return logs.map { it.message }
    }
}