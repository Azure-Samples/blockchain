package net.corda.workbench.transactionBuilder.tasks

import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.processManager.ProcessManager
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.commons.taskManager.BaseTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.transactionBuilder.events.EventFactory
import java.io.File
import java.lang.StringBuilder
import java.io.IOException
import java.net.ServerSocket
import java.util.*


class StartAgentTask(registry: Registry) : BaseTask() {

    private val ctx = registry.retrieve(TaskContext::class.java)
    private val es = registry.retrieve(EventStore::class.java)
    private val processManager = registry.retrieve(ProcessManager::class.java)


    override fun exec(executionContext: ExecutionContext) {

        executionContext.messageSink("Starting agent for: ${ctx.networkName}")

        val startClass = "net.corda.workbench.transactionBuilder.agent.AgentKt"
        val classPath = StringBuilder("build/libs/corda-transaction-builder.jar")
        for (jar in cordaAppsIter()) {
            if (classPath.isNotEmpty()) {
                classPath.append(":")
            }
            classPath.append(jar.absoluteFile)
        }

        //println("Starting agent with cp $classPath")

        // pick a range that won't clash with the 'corda-local-network'
        // just in case both are running on the same server
        val port = freePort(10200..10300)

        val pb = ProcessBuilder(listOf("java", "-cp", classPath.toString(), startClass, port.toString()))
                .inheritIO()
                .start()


        val pid = getPidOfProcess(pb)
        File("${ctx.workingDir}/process_id").writeText(pid.toString())

        executionContext.messageSink("Agent process started on $port, with pid: $pid")

        // record this in the event store
        val ev = EventFactory.AGENT_STARTED(ctx.networkName, port, pid)
        es.storeEvents(listOf(ev))

        // and keep an active list of live processes
        val processId = UUID.randomUUID()
        processManager.register(pb, processId, ctx.networkName + " - Agent" )
    }

    private fun cordaAppsIter(): Sequence<File> {
        return File("${ctx.workingDir}/cordapps").walk()
                .maxDepth(1)
                .filter { it.isFile && it.name.endsWith(".jar") }
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

    @Throws(IOException::class)
    fun freePort(ports: IntRange): Int {
        for (port in ports) {
            try {
                val socket = ServerSocket(port)
                socket.close()
                return port
            } catch (ex: IOException) {
                continue // try next port
            }

        }

        // if the program gets here, no port in the range was found
        throw IOException("no free port found")
    }

}