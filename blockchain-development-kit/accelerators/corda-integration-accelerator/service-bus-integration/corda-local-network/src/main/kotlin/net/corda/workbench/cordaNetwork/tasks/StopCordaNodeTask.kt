package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.BaseTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.cordaNetwork.ProcessManager
import java.io.File
import java.util.concurrent.TimeUnit

class StopCordaNodeTask(val ctx: TaskContext, private val nodeName: String) : BaseTask() {

    override fun exec(executionContext: ExecutionContext) {

        executionContext.messageStream("$nodeName: Attempting to stop node.")

        var killed = false

        // First try killing the node
        val p = ProcessManager.queryForNodeOnNetwork(ctx.networkName, nodeName)
        if (p != null) {
            killed = tryByJavaProcess(executionContext, p)
        }

        if (!killed && ctx is RealContext) {
            killed = tryByCordaPidFile(executionContext, ctx, nodeName)
        }

        if (!killed){
            executionContext.messageStream("$nodeName: failed to shutdown")

        }
    }

    fun tryByCordaPidFile(executionContext: ExecutionContext, ctx: RealContext, nodename: String): Boolean {
        try {
            val pidfile = "${ctx.workingDir}/$nodename/process-id"
            val pid = File(pidfile).readText()
            killPID(pid.toInt())
            executionContext.messageStream("$nodeName: destroyed Corda process")

            return true
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return false
    }

    fun tryByJavaProcess(executionContext: ExecutionContext, p: Process): Boolean {
        if (p.isAlive) {
            try {
                if (p::class.java.getName().equals("java.lang.UNIXProcess")) {
                    val f = p::class.java.getDeclaredField("pid");
                    f.setAccessible(true);
                    val pid = f.getInt(p);
                    killPID(pid)
                }
                executionContext.messageStream("$nodeName: destroyed running Java process")
                // todo - should remove from the running list of processes

                return true
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return false
    }


    fun killPID(pid: Int) {
        val pb = ProcessBuilder(listOf("pkill", "-P", pid.toString()))
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()

        pb.waitFor(5, TimeUnit.SECONDS)
    }

}