package net.corda.workbench.transactionBuilder.tasks

import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.processManager.ProcessManager
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.commons.taskManager.BaseTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.transactionBuilder.events.EventFactory
import java.io.File
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

class StopAgentTask(registry: Registry) : BaseTask() {

    private val ctx = registry.retrieve(TaskContext::class.java)
    private val es = registry.retrieve(EventStore::class.java)
    private val processManager = registry.retrieve(ProcessManager::class.java)



    override fun exec(executionContext: ExecutionContext) {

        executionContext.messageSink("${ctx.networkName}: Attempting to stop agent.")

        var killed = false

        // First try killing the agent
        val p = processManager.findByLabel(ctx.networkName + " - Agent")

        if (p != null) {
            killed = tryByJavaProcess(executionContext, p.process)
        }

        if (!killed) {
            killed = tryByPidFile(executionContext)
        }

        cleanupPID()

        if (!killed) {
            executionContext.messageSink("agent for ${ctx.networkName}: failed to shutdown")
        }
    }

    private fun tryByPidFile(executionContext: ExecutionContext): Boolean {
        try {
            val pidfile = "${ctx.workingDir}/process-id"
            val pid = File(pidfile).readText()
            killPID(pid.toInt())

            val ev = EventFactory.AGENT_STOPPED(ctx.networkName, pid.toLong(), "Shutdown using PID file")
            es.storeEvents(listOf(ev))

            executionContext.messageSink("${ctx.networkName}: destroyed agent process with $pid")

            return true
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return false
    }

    private fun tryByJavaProcess(executionContext: ExecutionContext, p: Process): Boolean {
        if (p.isAlive) {
            try {
                if (p::class.java.getName().equals("java.lang.UNIXProcess")) {
                    val f = p::class.java.getDeclaredField("pid");
                    f.setAccessible(true);
                    val pid = f.getInt(p);
                    killPID(pid)

                    val ev = EventFactory.AGENT_STOPPED(ctx.networkName, pid.toLong(), "Shutdown of running Java process")
                    es.storeEvents(listOf(ev))
                    executionContext.messageSink("${ctx.networkName}: destroyed running Java process with PID $pid")
                    return true
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return false
    }


    private fun killPID(pid: Int) {
        val pb = ProcessBuilder(listOf("kill",  pid.toString()))
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()

        if (!pb.waitFor(5, TimeUnit.SECONDS)) {
            throw RuntimeException("Timeout trying to kill PID $pid")
        }
    }

    private fun cleanupPID() {
        try {
            File("${ctx.workingDir}/process_id").delete()
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
    }

}