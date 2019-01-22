package net.corda.workbench.cordaNetwork.api

import io.javalin.ApiBuilder
import io.javalin.Context
import io.javalin.Javalin
import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.event.Filter
import net.corda.workbench.commons.taskManager.BlockingTasksExecutor
import net.corda.workbench.commons.taskManager.SimpleTaskRepo
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.commons.taskManager.TaskLogMessage
import net.corda.workbench.commons.taskManager.TaskRepo
import net.corda.workbench.cordaNetwork.ProcessManager
import org.json.JSONArray
import java.io.File
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.cordaNetwork.tasks.*


class ApiController(private val registry: Registry) {

    // simple file log of tasks message
    private val taskRepos = HashMap<String, TaskRepo>()

    fun register(app: Javalin) {

        ApiBuilder.path(":networkName") {

            app.routes {
                ApiBuilder.post("nodes/create") { ctx ->

                    val (networkName, taskContext, executor) = standardUnpacking(ctx)
                    @Suppress("UNCHECKED_CAST")
                    val nodes = JSONArray(ctx.body()).toList() as List<String>

                    executor.exec(CreateNodesTask(registry.overide(taskContext), nodes))

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

                    if (!isNetworkRunning(networkName)) {
                        val overrideRegistry = registry.overide(taskContext)
                        val tasks = listOf(StopCordaNodesTask(overrideRegistry), StartCordaNodesTask(overrideRegistry))
                        executor.exec(tasks)

                        ctx.json(successMessage("successfully started $networkName - please wait for nodes to start"))
                    } else {
                        throw RuntimeException("network $networkName is already running")
                    }
                }

                ApiBuilder.post("stop") { ctx ->
                    val (networkName, taskContext, executor) = standardUnpacking(ctx)

                    executor.exec(StopCordaNodesTask(registry.overide(taskContext)))

                    ctx.json(successMessage("successfully stopped $networkName"))
                }

                ApiBuilder.post("delete") { ctx ->
                    val (networkName, taskContext, executor) = standardUnpacking(ctx)

                    val tasks = listOf(StopCordaNodesTask(registry.overide(taskContext)), DeleteNetworkTask(taskContext))
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

                ApiBuilder.get("events") { ctx ->
                    val networkName = ctx.param("networkName")!!
                    val events = registry.retrieve(EventStore::class.java).retrieve(Filter(aggregateId = networkName))
                    ctx.json(events.reversed())
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


    private fun isNetworkRunning(network: String): Boolean {
        return registry.retrieve(EventStore::class.java).retrieve(Filter(aggregateId = network))
                .fold(false) { status, event ->
                    when {
                        event.type == "NetworkStarted" -> true
                        event.type == "NetworkStopped" -> false
                        else -> status
                    }
                }
    }

}