package net.corda.workbench.transactionBuilder.tasks


import net.corda.workbench.commons.taskManager.TaskLogMessage


class TestMessageSink {
    val logs = ArrayList<TaskLogMessage>()

    fun sink(t: TaskLogMessage) {
        logs.add(t)
        System.out.println(t)
    }

    fun messages(): List<String> {
        return logs.map { it.message }
    }
}