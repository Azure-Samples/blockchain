package net.corda.workbench.transactionBuilder.tasks

import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.commons.taskManager.BaseTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.transactionBuilder.CordaAppLoader
import net.corda.workbench.transactionBuilder.events.EventFactory
import java.io.File
import java.lang.RuntimeException
import java.net.URL
import java.net.URLClassLoader
import java.util.*


class DeployCordaAppTask(registry: Registry, private val cordapp: File, private val registeredName: String) : BaseTask() {

    val ctx = registry.retrieve(TaskContext::class.java)
    val es = registry.retrieve(EventStore::class.java)

    override fun exec(executionContext: ExecutionContext) {
        executionContext.messageStream.invoke("Deploying cordapp ${cordapp.name}")
        val target = "${ctx.workingDir}/cordapps/$registeredName.jar"
        cordapp.copyTo(File(target), true)
        val id = readId(cordapp)
        es.storeEvents(listOf(EventFactory.CORDA_APP_DEPLOYED(registeredName, ctx.networkName, id)))
    }


    private fun readId(jarFile: File): UUID {
        try {
            val cordaURL = URL("file://" + jarFile.absolutePath)
            val classLoader = URLClassLoader(arrayOf(cordaURL))
            val loader = CordaAppLoader()
            loader.scan(classLoader)
            return loader.allApps().single().id
        } catch (ex: Exception) {
            throw RuntimeException("Couldn't find a valid registry file at " +
                    "'src/main/resources/META-INF/services/net.corda.workbench.Registry.json' for ${jarFile.name}")

        }

    }

}

