package net.corda.workbench.cordaNetwork.api

import io.javalin.ApiBuilder
import io.javalin.Context
import io.javalin.Javalin
import net.corda.workbench.commons.taskManager.BlockingTasksExecutor
import net.corda.workbench.commons.taskManager.SimpleTaskRepo
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.commons.taskManager.TaskLogMessage
import net.corda.workbench.commons.taskManager.TaskRepo
import net.corda.workbench.cordaNetwork.ProcessManager
import net.corda.workbench.cordaNetwork.tasks.ConfigBuilderTask
import net.corda.workbench.cordaNetwork.tasks.DeleteNetworkTask
import net.corda.workbench.cordaNetwork.tasks.DeployCordaAppTask
import net.corda.workbench.cordaNetwork.tasks.NetworkBootstrapperTask
import net.corda.workbench.cordaNetwork.tasks.NodeCertificateTask
import net.corda.workbench.cordaNetwork.tasks.NodeConfigTask
import net.corda.workbench.cordaNetwork.tasks.NodeListTask
import net.corda.workbench.cordaNetwork.tasks.NodeStatusTask
import net.corda.workbench.cordaNetwork.tasks.RealContext
import net.corda.workbench.cordaNetwork.tasks.StartCordaNodesTask
import net.corda.workbench.cordaNetwork.tasks.StopCordaNodesTask
import org.json.JSONArray
import java.io.File

class ApiController {

    // simple file log of tasks message
    private val taskRepos = HashMap<String, TaskRepo>()

    fun register(app: Javalin) {

        ApiBuilder.path(":networkName") {

            app.routes {
                ApiBuilder.post("nodes/create") { ctx ->

                    val (networkName, taskContext, executor) = standardUnpacking(ctx)
                    @Suppress("UNCHECKED_CAST")
                    val nodes = JSONArray(ctx.body()).toList() as List<String>

                    executor.exec(ConfigBuilderTask(taskContext, nodes))
                    executor.exec(NetworkBootstrapperTask(taskContext))

                    ctx.json(successMessage("successfully created network $networkName"))
                }

                ApiBuilder.post("apps/:appname/deploy") { ctx ->
                    val (networkName, taskContext, executor) = standardUnpacking(ctx)

                    val data = ctx.bodyAsBytes()
                    val appName = ctx.param("appname")!!
                    val f = File(tempDir(taskContext) + "/" + appName + ".jar")
                    f.writeBytes(data)

                    executor.exec(DeployCordaAppTask(taskContext, f))

                    ctx.json(successMessage("successfully deployed $appName to $networkName"))
                }

                ApiBuilder.post("start") { ctx ->
                    val (networkName, taskContext, executor) = standardUnpacking(ctx)

                    executor.exec(StartCordaNodesTask(taskContext))

                    ctx.json(successMessage("successfully started $networkName - please wait for nodes to start"))
                }

                ApiBuilder.post("stop") { ctx ->
                    val (networkName, taskContext, executor) = standardUnpacking(ctx)

                    executor.exec(StopCordaNodesTask(taskContext))

                    ctx.json(successMessage("successfully stopped $networkName"))
                }

                ApiBuilder.post("delete") { ctx ->
                    val (networkName, taskContext, executor) = standardUnpacking(ctx)

                    val tasks = listOf(StopCordaNodesTask(taskContext), DeleteNetworkTask(taskContext))
                    executor.exec(tasks)

                    ctx.json(successMessage("successfully deleted $networkName"))
                }


                ApiBuilder.get("nodes") { ctx ->
                    val networkName = ctx.param("networkName")!!
                    val context = RealContext(networkName)
                    val nodes = NodeListTask(context).exec()
                    ctx.json(nodes)
                }

                ApiBuilder.path("nodes/:nodeName") {
                    ApiBuilder.get("config") { ctx ->
                        val networkName = ctx.param("networkName")!!
                        val nodeName = ctx.param("nodeName")!!
                        val context = RealContext(networkName)

                        val config = NodeConfigTask(context, nodeName).exec()
                        ctx.json(config)
                    }

                    ApiBuilder.get("status") { ctx ->
                        val networkName = ctx.param("networkName")!!
                        val nodeName = ctx.param("nodeName")!!
                        val context = RealContext(networkName)

                        val status = NodeStatusTask(context, nodeName).exec()
                        ctx.json(status)
                    }

                    ApiBuilder.get("publickey") { ctx ->
                        val networkName = ctx.param("networkName")!!
                        val nodeName = ctx.param("nodeName")!!
                        val context = RealContext(networkName)

                        val key = NodeCertificateTask(context, nodeName).exec()
                        ctx.contentType("octet-stream")
                        ctx.result(key)
                    }
                }

                ApiBuilder.get("tasks/history") { ctx ->
                    val networkName = ctx.param("networkName")!!
                    val context = RealContext(networkName)
                    val repo = taskRepos.getOrPut(context.networkName) { SimpleTaskRepo("${context.workingDir}/tasks") }
                    ctx.json(repo.all())
                }

                ApiBuilder.get("processes") { ctx ->
                    val networkName = ctx.param("networkName")!!
                    val processes = ProcessManager.queryForNetwork(networkName)
                    ctx.json(processes)
                }
            }
        }
    }

    fun buildMessageSink(context: TaskContext): ((TaskLogMessage) -> Unit) {
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

    fun buildExecutor(context: TaskContext): BlockingTasksExecutor {
        val messageSink = buildMessageSink(context)
        return BlockingTasksExecutor(messageSink)
    }

    fun tempDir(ctx: TaskContext): String {
        val tmpDir = "${ctx.workingDir}/tmp"
        File(tmpDir).mkdirs()
        return tmpDir
    }

    fun standardUnpacking(ctx: Context): Triple<String, RealContext, BlockingTasksExecutor> {
        val networkName = ctx.param("networkName")!!
        val context = RealContext(networkName)
        val executor = buildExecutor(context)
        return Triple(networkName, context, executor)
    }

    fun successMessage(msg: String): MutableMap<String, Any> {
        return mutableMapOf("message" to msg)
    }

}