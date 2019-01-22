package net.corda.workbench.transactionBuilder.tasks

import net.corda.workbench.commons.taskManager.TaskContext
import java.io.File


class RealContext(override val networkName: String) : TaskContext {
    val dataDir: String

    init {
        if (System.getProperty("datadir") != null) {
            dataDir = System.getProperty("datadir") as String
        } else {
            dataDir = System.getProperty("user.home") + "/.corda-transaction-builder"
        }
    }

    override val workingDir = buildWorkingDir()

    private fun buildWorkingDir(): String {
        val workingDir = "$dataDir/$networkName"
        File(workingDir).mkdirs()
        return workingDir
    }
}