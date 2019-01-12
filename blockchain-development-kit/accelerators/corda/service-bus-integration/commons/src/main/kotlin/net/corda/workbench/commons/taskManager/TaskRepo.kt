package net.corda.workbench.commons.taskManager

import java.io.File
import java.util.ArrayList
import java.util.UUID

/**
 * A repo for recording tasks run.
 */
interface TaskRepo {
    fun store(message: TaskLogMessage)
    fun all(): List<TaskLogMessage>
}

class SimpleTaskRepo(private val directory: String, private val dataFile: String = "tasklog.txt") : TaskRepo {
    init {
        println("creating $directory/$dataFile")
        File(directory).mkdirs()
        File("$directory/$dataFile").createNewFile()
    }

    override fun all(): List<TaskLogMessage> {
        val file = File("$directory/$dataFile")

        val result = ArrayList<TaskLogMessage>();
        file.forEachLine {
            val parts = it.split(":")
            val ts = parts[0].toLong()
            val id = UUID.fromString(parts[1])
            val message = parts[2]
            result.add(TaskLogMessage(message, id, ts))
        }

        return result
    }

    override fun store(message: TaskLogMessage) {
        val file = File("$directory/$dataFile")
        val encoded = "${message.timestamp}:${message.taskId}:${message.message}\n"
        file.appendText(encoded)
    }
}