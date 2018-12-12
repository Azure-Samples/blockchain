package net.corda.workbench.transactionBuilder.app

import io.javalin.ApiBuilder
import io.javalin.Context
import io.javalin.Javalin
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.commons.taskManager.*
import net.corda.workbench.transactionBuilder.tasks.DeployCordaAppTask
import net.corda.workbench.transactionBuilder.tasks.RealContext
import net.corda.workbench.transactionBuilder.tasks.StartAgentTask
import net.corda.workbench.transactionBuilder.tasks.StopAgentTask
import java.io.File

class ApiController(private val registry: Registry) {

    // simple file log of tasks message
    val taskRepos = HashMap<String, TaskRepo>()

    fun register() {
        val app = registry.retrieve(Javalin::class.java)

        ApiBuilder.path(":networkName") {

            app.routes {

                ApiBuilder.post("apps/:appname/deploy") { ctx ->
                    val (networkName, taskContext, executor) = standardUnpacking(ctx)

                    val data = ctx.bodyAsBytes()
                    val appName = ctx.param("appname")!!
                    val jarFile = File(tempDir(taskContext) + "/" + appName + ".jar")
                    jarFile.writeBytes(data)

                    val deployTask = DeployCordaAppTask(registry.overide(taskContext), jarFile, appName)
                    executor.exec(deployTask)

                    ctx.json(successMessage("successfully deployed $appName to $networkName"))
                }

                ApiBuilder.post("start") { ctx ->
                    val (networkName, taskContext, executor) = standardUnpacking(ctx)

                    val startTask = StartAgentTask(registry.overide(taskContext))
                    executor.exec(startTask)

                    ctx.json(successMessage("successfully started agent for $networkName"))
                }

                ApiBuilder.post("stop") { ctx ->
                    val (networkName, taskContext, executor) = standardUnpacking(ctx)

                    val stopAgent = StopAgentTask(registry.overide(taskContext))
                    executor.exec(stopAgent)

                    ctx.json(successMessage("successfully stopped agent for $networkName"))
                }

                ApiBuilder.post("restart") { ctx ->
                    val (networkName, taskContext, executor) = standardUnpacking(ctx)

                    val stopAgent = StopAgentTask(registry.overide(taskContext))
                    executor.exec(stopAgent)
                    Thread.sleep(10000L)
                    val startTask = StartAgentTask(registry.overide(taskContext))
                    executor.exec(startTask)

                    ctx.json(successMessage("successfully restarted agent for $networkName"))
                }


                ApiBuilder.get("tasks/history") { ctx ->
                    val networkName = ctx.param("networkName")!!
                    val context = RealContext(networkName)
                    val repo = taskRepos.getOrPut(context.networkName) { SimpleTaskRepo("${context.workingDir}/tasks") }
                    ctx.json(repo.all())
                }
            }

        }

    }

    private fun buildMessageSink(context: TaskContext): ((TaskLogMessage) -> Unit) {
        val repo = taskRepos.getOrPut(context.networkName) {
            SimpleTaskRepo("${context.workingDir}/tasks")
        }
        return {
            try {
                repo.store(it)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    }

    private fun buildExecutor(context: TaskContext): BlockingTasksExecutor {
        val messageSink = buildMessageSink(context)
        return BlockingTasksExecutor(messageSink)
    }

    private fun tempDir(ctx: TaskContext): String {
        val tmpDir = "${ctx.workingDir}/tmp"
        File(tmpDir).mkdirs()
        return tmpDir
    }

    private fun standardUnpacking(ctx: Context): Triple<String, RealContext, BlockingTasksExecutor> {
        val networkName = ctx.param("networkName")!!
        val context = RealContext(networkName)
        val executor = buildExecutor(context)
        return Triple(networkName, context, executor)
    }

    private fun successMessage(msg: String): MutableMap<String, Any> {
        return mutableMapOf("message" to msg)
    }

}