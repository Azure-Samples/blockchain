package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.event.Filter
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.commons.taskManager.BaseTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.cordaNetwork.ProcessManager
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
 * TODO - consider a cleaner implemetation
 */

class StopCordaNodeTask(registry: Registry, private val nodeName: String) : BaseTask() {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(StopCordaNodeTask::class.java)
    }

    private val ctx = registry.retrieve(TaskContext::class.java)!!
    private val es = registry.retrieve(EventStore::class.java)!!

    override fun exec(executionContext: ExecutionContext) {

        executionContext.messageStream("$nodeName: Attempting to stop node.")

        // First try killing the Java process
        val p = ProcessManager.queryForNodeOnNetwork(ctx.networkName, nodeName)
        if (p != null) {
            val pid = getPidOfProcess(p)
            val killed = tryByJavaProcess(executionContext, p)
            if (killed) {
                es.storeEvent(EventFactory.NODE_STOPPED(ctx.networkName, nodeName, pid.toInt(), "Shutdown Java process"))
                executionContext.messageStream("$nodeName: Clean shutdown of node.")
            }
        }

        // check for any pids without a shutdown event.
        for (orphanedPID in orphanedPIDs(ctx.networkName, nodeName)) {
            logger.debug("Found possible orphaned $p for $nodeName - trying to kill")
            try {
                pkill(orphanedPID)
                es.storeEvent(EventFactory.NODE_STOPPED(ctx.networkName, nodeName, orphanedPID, "Killing possible orphaned PID"))

            } catch (ignored: Exception) {
                logger.debug("Failed to kill orphaned PID $p - ${ignored.message}")
            }
        }

    }


    fun tryByJavaProcess(executionContext: ExecutionContext, p: Process): Boolean {
        if (p.isAlive) {
            try {
                if (p::class.java.getName().equals("java.lang.UNIXProcess")) {
                    val f = p::class.java.getDeclaredField("pid");
                    f.setAccessible(true);
                    val pid = f.getInt(p);
                    pkill(pid)
                }
                executionContext.messageStream("$nodeName: destroyed running Java process")
                ProcessManager.removeProcess(p)
                return true
            } catch (ex: Exception) {
                logger.debug("Problem removing process $p - ${ex.message}")
            }
        }
        return false
    }


    private fun pkill(pid: Int) {
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
    fun orphanedPIDs(networkName: String, nodeName: String): Set<Int> {
        val pids = HashSet<Int>()
        es.retrieve(Filter(aggregateId = networkName))
                .forEach { event ->
                    when {
                        event.type == "NodeStarted" && nodeName == event.payload["node"] -> {
                            val pid = event.payload["pid"] as Int
                            pids.add(pid)
                        }
                        event.type == "NodeStopped" && nodeName == event.payload["node"] -> {
                            val pid = event.payload["pid"] as Int
                            pids.remove(pid)
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