package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.BaseTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels

/**
 * Runs the network bootstrapper, streaming results
 */
abstract class AbstractDownloadTask() : BaseTask() {
    val downloadCache = System.getProperty("user.home") + "/.corda-local-network/downloads"


    override fun exec(executionContext: ExecutionContext) {
        downloadJar(executionContext)
    }

    abstract val fileName: String
    abstract val externalUrl: String

    private fun downloadJar(executionContext: ExecutionContext) {
        try {
            if (!File(fileName).exists()) {
                executionContext.messageSink("Starting download of $fileName from $externalUrl")

                safeDelete("$fileName.download")
                File(downloadCache).mkdirs()
                val url = URL(externalUrl)
                val readableByteChannel = Channels.newChannel(url.openStream());
                val fileOutputStream = FileOutputStream("$fileName.download");
                fileOutputStream.getChannel()
                        .transferFrom(readableByteChannel, 0, Long.MAX_VALUE)
                safeRename("$fileName.download",fileName)

                executionContext.messageSink("Completed download of $fileName from $externalUrl")
            }
        } catch (ex: Exception) {
            executionContext.messageSink("Problem downloading $fileName from $externalUrl - ${ex.message}")

            // todo - should be reporting to the error stream
            ex.printStackTrace()
            throw ex
        }

    }

    private fun safeDelete(filename: String) {
        try {
            File(filename).delete()
        } catch (ignored: Exception) {
        }
    }

    private fun safeRename(oldName: String, newName: String) {
        try {
            File(oldName).renameTo(File(newName))
        } catch (ignored: Exception) {
        }
    }
}
