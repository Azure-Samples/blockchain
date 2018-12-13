package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.commons.taskManager.BaseTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.cordaNetwork.ProcessManager
import net.corda.workbench.transactionBuilder.events.EventFactory
import java.io.File

class StartCordaNodeTask(registry: Registry, private val nodeName: String) : BaseTask() {
    private val ctx = registry.retrieve(TaskContext::class.java)!!
    private val es = registry.retrieve(EventStore::class.java)!!

    override fun exec(executionContext: ExecutionContext) {

        val nodeDir = ctx.workingDir + "/" + nodeName

        executionContext.messageStream.invoke("starting node $nodeName in $nodeDir")

        val process = ProcessBuilder(listOf("java", "-jar", "corda.jar"))
                .directory(File(nodeDir))
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()


        ProcessManager.register(ctx.networkName, nodeName, process)

        es.storeEvent(EventFactory.NODE_STARTED(ctx.networkName, nodeName, getPidOfProcess(process).toInt()))
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