package net.corda.workbench.commons.taskManager


import java.util.*

/**
 * A simple execution context to control where messages and logging are sent to
 */
class ExecutionContext(messageSink: (String) -> Unit = { consoleMessageSink(it) },
                       processRegister: (Process, UUID, String) -> Unit = { _, _, _ -> },
                       id: UUID = UUID.randomUUID()

) {

    /**
     * The unique id associated with this task
     */
    val id: UUID = id

    /**
     * Dedicated stream for the task's internal reporting. Should
     * be limited to basic status and progress messages
     */
    val messageSink: (String) -> Unit = messageSink

    /**
     * Call this to register a long running process with the ProcessManager
     */
    val processRegister: (Process, UUID, String) -> Unit = processRegister


    @Deprecated(message = "use messageSink instead")
    val messageStream: (String) -> Unit = messageSink

    /**
     * A default sink (consumer) of messages that just prints to
     * the console
     */
    companion object {
        fun consoleMessageSink(m: String) {
            println(m)
        }

        fun nullProcessRegister(process: Process, id: UUID, label: String) {}
    }
}

/**
 * Executes a single task with logging, passing on any exceptions thrown
 */
class TaskExecutor(private val taskLogMessageSink: (TaskLogMessage) -> Unit) {

    private val executorId: UUID = UUID.randomUUID()

    fun exec(t: Task) {

        // wire up an execution context linked to this taskLogMessageSink, so that
        // executionContext.messageStream is sent via TaskLogMessage
        val sink = MessageSink(executorId, t, taskLogMessageSink)
        val executionContext = ExecutionContext({ message -> sink.invoke(message) })

        sink.invoke("Starting ${t::class.java.simpleName}")
        try {
            t.exec(executionContext)
            sink.invoke("Completed ${t::class.java.simpleName}")

        } catch (ex: Exception) {
            sink.invoke("Failed ${t::class.java.simpleName}")
            sink.invoke("Exception is: ${ex.message}")
            throw ex
        }
    }

    class MessageSink(val executorId: UUID, val t: Task, private val taskLogMessageSink: (TaskLogMessage) -> Unit) {
        fun invoke(message: String) {
            val taskLogMessage = TaskLogMessage(executorId, message, t.taskID)
            taskLogMessageSink.invoke(taskLogMessage)
        }
    }
}



/**
 * Execute a list of tasks, waiting until the last one completes
 */
class BlockingTasksExecutor(private val taskLogMessageSink: (TaskLogMessage) -> Unit) {

    fun exec(tasks: List<Task>) {
        tasks.forEach { TaskExecutor(taskLogMessageSink).exec(it) }
    }

    fun exec(task: Task) {
        exec(listOf(task))
    }
}
