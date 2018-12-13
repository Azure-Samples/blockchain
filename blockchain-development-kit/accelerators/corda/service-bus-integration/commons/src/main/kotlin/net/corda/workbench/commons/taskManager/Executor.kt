package net.corda.workbench.commons.taskManager

import java.io.OutputStream

/**
 * A simple execution context to control where messages and logging are sent to
 */
class ExecutionContext(messageSink: (String) -> Unit = { consoleMessageSink(it) }) {

    /**
     * Standard output stream. Tasks should redirect any output to this stream
     */
    val outputStream: OutputStream = System.out

    /**
     * Standard error stream. Tasks should redirect any error to this stream
     */
    val errorStream: OutputStream = System.err

    /**
     * Dedicated stream for the task's internal reporting. Should
     * be limited to basic status and progress messages
     */
    val messageStream: (String) -> Unit = messageSink

    /**
     * A default sink (consumer) of messages that just prints to
     * the console
     */
    companion object {
        fun consoleMessageSink(m: String) {
            println(m)
        }
    }
}

/**
 * Executes a single task with logging
 */
class TaskExecutor(private val taskLogMessageSink: (TaskLogMessage) -> Unit) {

    fun exec(t: Task) {

        // wire up an execution context linked to this taskLogMessageSink, so that
        // executionContext.messageStream is sent via TaskLogMessage
        val sink = MessageSink(t, taskLogMessageSink)
        val executionContext = ExecutionContext({ sink.doit(it) })

        taskLogMessageSink.invoke(TaskLogMessage("Starting ${t::class.java.simpleName}", t.taskID))
        try {
            t.exec(executionContext)
            taskLogMessageSink.invoke(TaskLogMessage("Completed ${t::class.java.simpleName}", t.taskID))

        } catch (ex: Exception) {
            taskLogMessageSink.invoke(TaskLogMessage("Failed ${t::class.java.simpleName}", t.taskID))
            taskLogMessageSink.invoke(TaskLogMessage("Exception is: ${ex.message}", t.taskID))
        }
    }

    class MessageSink(val t: Task, private val taskLogMessageSink: (TaskLogMessage) -> Unit) {
        fun doit(message: String) {
            val taskLogMessage = TaskLogMessage(message, t.taskID)
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
