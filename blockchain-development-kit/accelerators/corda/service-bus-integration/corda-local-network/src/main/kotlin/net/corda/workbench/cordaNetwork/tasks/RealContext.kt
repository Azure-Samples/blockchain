package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.TaskContext
import java.io.File
import java.nio.file.Paths

class RealContext(override val networkName: String) : TaskContext {
    private val dataDir: String

    init {
        if (System.getProperty("datadir") != null) {
            dataDir = System.getProperty("datadir") as String
        } else {
            dataDir = Paths.get(System.getProperty("user.home"), ".corda-local-network")
                    .toFile()
                    .absolutePath

        }
    }

    override val workingDir = buildWorkingDir()

    private fun buildWorkingDir(): String {
        val workingDir = "$dataDir/$networkName"
        File(workingDir).mkdirs()
        return workingDir
    }
}