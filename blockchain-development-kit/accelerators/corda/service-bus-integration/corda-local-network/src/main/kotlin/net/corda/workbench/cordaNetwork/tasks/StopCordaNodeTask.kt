package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.event.Filter
import net.corda.workbench.commons.processManager.ProcessManager
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.commons.taskManager.BaseTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.transactionBuilder.events.EventFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit


/**
 * Stops the Corda node
 *
 * Tries to shutdown gracefully, if not resort to more brutal means. Domain
 * event are generated to keep track of what has happened to each PID.
 *
 * TODO - consider a cleaner implementation
 */

class StopCordaNodeTask(registry: Registry, private val nodeName: String) : BaseTask() {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(StopCordaNodeTask::class.java)
    }

    private val ctx = registry.retrieve(TaskContext::class.java)
    private val es = registry.retrieve(EventStore::class.java)
    private val processManager = registry.retrieve(ProcessManager::class.java)


    override fun exec(executionContext: ExecutionContext) {

        executionContext.messageSink("$nodeName - Attempting to stop node.")

        // First try killing the Java process
        val p = processManager.findByLabel(ctx.networkName + ":" + nodeName)
        if (p != null) {
            val pid = getPidOfProcess(p.process)
            processManager.kill(p.process, true)

            es.storeEvent(EventFactory.NODE_STOPPED(ctx.networkName, nodeName, pid, "Shutdown Java process"))
            executionContext.messageSink("$nodeName - Shutdown of node.")
        }
        else {
            executionContext.messageSink("$nodeName - Nothing to do - process already stopped.")
        }

        // check for any pids without a shutdown event.
        for (orphanedPID in orphanedPIDs(ctx.networkName, nodeName)) {
            logger.debug("Found possible orphaned $p for $nodeName - trying to kill")
            try {
                pkill(orphanedPID)
                executionContext.messageSink("$nodeName - Shutdown of orphanedPID - $orphanedPID")

                es.storeEvent(EventFactory.NODE_STOPPED(ctx.networkName, nodeName, orphanedPID, "Killing possible orphaned PID"))

            } catch (ignored: Exception) {
                logger.debug("Failed to kill orphaned PID $p - ${ignored.message}")
            }
        }

    }



    private fun pkill(pid: Long) {
        logger.debug("using pkill on PID $pid")
        val pb = ProcessBuilder(listOf("pkill", "-P", pid.toString()))
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()

        pb.waitFor(5, TimeUnit.SECONDS)
        logger.debug("stdout: " + pb.inputStream.bufferedReader().use { it.readText() })
        logger.debug("err:" + pb.errorStream.bufferedReader().use { it.readText() })

    }


    /**
     * reduce events to find a PID that might not have been shutdown
     */
    fun orphanedPIDs(networkName: String, nodeName: String): Set<Long> {
        val pids = HashSet<Long>()
        es.retrieve(Filter(aggregateId = networkName))
                .forEach { event ->
                    when {
                        event.type == "NodeStarted" && nodeName == event.payload["node"] -> {
                            val pid = event.payload["pid"] as Int
                            pids.add(pid.toLong())
                        }
                        event.type == "NodeStopped" && nodeName == event.payload["node"] -> {
                            val pid = event.payload["pid"] as Int
                            pids.remove(pid.toLong())
                        }
                    }
                }
        return pids
    }

    @Synchronized
    fun getPidOfProcess(p: Process): Long {
        var pid: Long = -1

        try {
            if (p.javaClass.name == "java.lang.UNIXProcess") {
                val f = p.javaClass.getDeclaredField("pid")
                f.isAccessible = true
                pid = f.getLong(p)
                f.isAccessible = false
            }
        } catch (e: Exception) {
            pid = -1
        }

        return pid
    }

}