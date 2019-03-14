package net.corda.workbench.commons.taskManager

import java.io.File
import java.util.UUID

/**
 * Standard way to pass basic context around between tasks.
 */
interface TaskContext {
    val workingDir: String
    val networkName: String
}

/**
 * Setup for easy use in testcase
 */
class TestContext(override val networkName: String = UUID.randomUUID().toString()) : TaskContext {

    override val workingDir = buildTempDir()

    private fun buildTempDir(): String {
        val workingDir = ".test/$networkName"
        File(workingDir).mkdirs()
        return workingDir
    }
}


