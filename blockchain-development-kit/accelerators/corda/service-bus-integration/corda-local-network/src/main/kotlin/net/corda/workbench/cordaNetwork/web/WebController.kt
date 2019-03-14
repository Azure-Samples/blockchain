package net.corda.workbench.cordaNetwork.web


import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.util.options.MutableDataSet
import com.vladsch.flexmark.parser.Parser
import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.processManager.ProcessManager

import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.commons.taskManager.*
import net.corda.workbench.cordaNetwork.AppConfig
import net.corda.workbench.cordaNetwork.events.Repo
import net.corda.workbench.cordaNetwork.tasks.*
import net.corda.workbench.transactionBuilder.copyInputStreamToFile
import net.corda.workbench.transactionBuilder.md5Hash
import net.corda.workbench.transactionBuilder.readFileAsText

import org.http4k.core.*
import org.http4k.core.body.formAsMap
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Paths
import java.util.*

/**
 * Controller for rendering embedded website
 */
class WebController(private val registry: Registry) : HttpHandler {
    private val repo = Repo(registry.retrieve(EventStore::class.java))
    private val processManager = registry.retrieve(ProcessManager::class.java)
    private val config = registry.retrieve(AppConfig::class.java)


    // todo - should be injected in
    private val taskRepos = HashMap<String, TaskRepo>()


    private val routes: RoutingHttpHandler = routes(
            "/" bind Method.GET to {
                Response(Status.PERMANENT_REDIRECT).header("Location", "/web/home")
            },
            "/web" bind routes(
                    "/home" bind Method.GET to {
                        val networks = repo.networks()
                        val page = renderTemplate("home.md",
                                mapOf("networks" to networks))
                        html(page)

                    },

                    "/networks/create" bind Method.GET to { req ->
                        val page = renderTemplate("createNetworkForm.html", emptyMap())
                        html(page)

                    },

                    "/networks/create" bind Method.POST to { req ->
                        val createRequest = unpackCreateNetworkForm(req)

                        println(createRequest)
                        val context = RealContext(createRequest.name)
                        val executor = buildExecutor(context)
                        val scopedRegistry = registry.overide(context)

                        executor.exec(CreateNetworkTask(scopedRegistry))
                        val createOptions = CreateNodesTask.Options(cordaVersion = createRequest.cordaVersion)
                        executor.exec(CreateNodesTask(scopedRegistry, createRequest.organisations, createOptions))

                        val page = renderTemplate("networkCreated.md",
                                mapOf("networkName" to createRequest.name))
                        html(page)

                    },

                    "/networks/{network}" bind Method.GET to { req ->
                        val network = req.path("network")!!
                        val nodes = repo.nodes(network)
                        val cordapps = repo.deployedCordapps(network)
                        val page = renderTemplate("network.md",
                                mapOf("nodes" to nodes, "cordapps" to cordapps, "networkName" to network))
                        html(page)

                    },

                    "/networks/{network}/deploy" bind Method.GET to { req ->
                        val network = req.path("network")!!
                        val page = renderTemplate("deployAppForm.md",
                                mapOf("networkName" to network))
                        html(page)
                    },

                    "/networks/{network}/deploy" bind Method.POST to { req ->
                        val network = req.path("network")!!
                        val context = RealContext(network)


                        // unpack file
                        val receivedForm = MultipartFormBody.from(req)
                        val file = receivedForm.files("cordapp")
                        val filename = file.get(0).filename

                        val working = File(tempDir(context) + "/" + filename)
                        working.copyInputStreamToFile(file[0].content)

                        val md5Hash = working.md5Hash()
                        val byteCount = working.length()

                        // run task
                        val executor = buildExecutor(context)
                        val task = DeployCordaAppTask(registry.overide(context), working)
                        executor.exec(task)

                        val page = renderTemplate("deployAppResult.md",
                                mapOf("networkName" to network,
                                        "fileName" to filename,
                                        "length" to byteCount,
                                        "md5Hash" to md5Hash))
                        html(page)
                    },

                    "/networks/{network}/start" bind Method.POST to { req ->
                        doStartNetwork(req)
                    },
                    "/networks/{network}/start" bind Method.GET to { req ->
                        // allow GET form for easy hyperlinks
                        doStartNetwork(req)
                    },

                    "/networks/{network}/stop" bind Method.POST to { req ->
                        doStopNetwork(req)
                    },
                    "/networks/{network}/stop" bind Method.GET to { req ->
                        // allow GET form for easy hyperlinks
                        doStopNetwork(req)
                    },


                    "/networks/{network}/status" bind Method.GET to { req ->
                        val network = req.path("network")!!
                        val context = RealContext(network)
                        //val messageSink = buildMessageSink(context)

                        val nodesStatus = NodesInfoTask(context).exec().sortedBy { it.name }
                        val isRunning = repo.isNetworkRunning(network)

                        val page = renderTemplate("networkStatus.md",
                                mapOf("nodesStatus" to nodesStatus,
                                        "isRunning" to isRunning,
                                        "networkName" to network))
                        html(page)
                    },

                    "/networks/{network}/tasks/history" bind Method.GET to { req ->
                        val network = req.path("network")!!
                        val context = RealContext(network)
                        val repo = taskRepos.getOrPut(context.networkName) {
                            SimpleTaskRepo("${context.workingDir}/tasks")
                        }

                        //<div class="logline">{{executionId}} {{taskId}} {{timestamp}} {{message}}</div>

                        val history = repo.all()
                                .reversed()
                                .map {
                                    mapOf("executionId" to it.executionId.toString().substring(0, 8),
                                            "taskId" to it.taskId.toString().substring(0, 8),
                                            "timestamp" to Date(it.timestamp),
                                            "message" to it.message,
                                            "executionColour" to it.executionId.toString().substring(0, 6))


                                }
                        val page = renderTemplate("taskLog.md",
                                mapOf("history" to history,
                                        "networkName" to network))
                        html(page)
                    },

                    "/networks/{network}/nodes/{node}/status" bind Method.GET to { req ->
                        val network = req.path("network")!!
                        val node = req.path("node")!!
                        val context = RealContext(network)
                        val messageSink = buildMessageSink(context)

                        val status = NodeInfoTask(context, node).exec()

                        val page = renderTemplate("nodeStatus.md",
                                mapOf("status" to status, "networkName" to network, "config" to config))
                        html(page)
                    },

                    "/networks/{network}/nodes/{node}/log" bind Method.GET to { req ->
                        val network = req.path("network")!!
                        val node = req.path("node")!!
                        val context = RealContext(network)
                        val messageSink = buildMessageSink(context)

                        val logs = NodeLogsTask(context, node).exec()
                        text(logs)
                    },


                    "/networks/{network}/cordapps/{name}/download" bind Method.GET to { req ->
                        val network = req.path("network")!!
                        val cordapp = req.path("name")!!
                        val context = RealContext(network)

                        val masterCopy = Paths.get(context.workingDir, ".cordapps", cordapp).normalize()
                                .toAbsolutePath()
                                .toFile()

                        //val logs = NodeLogsTask(context, node).exec()
                        binary(masterCopy.inputStream())
                    },

                    "/processes" bind Method.GET to { req ->
                        processManager.allProcesses()
                        val page = renderTemplate("processList.md",
                                mapOf("processes" to processManager.allProcesses()))
                        html(page)

                    },


                    "/style.css" bind Method.GET to {
                        val css = FileInputStream("src/main/resources/www/style.css").bufferedReader().use { it.readText() }
                        css(css)
                    },

                    "/ajax/test" bind Method.GET to {
                        text("time is ${System.currentTimeMillis()}")

                    }

            ),
            "/api" bind routes(
                    "/networks" bind Method.GET to { req ->
                        val networks = repo.networks()
                        json(JSONArray(networks))
                    },
                    "/networks/{network}/cordapps" bind Method.GET to { req ->
                        val network = req.path("network")!!
                        val cordapps = repo.deployedCordapps(network)
                        json(JSONArray(cordapps))
                    },
                    "/networks/{network}/nodes" bind Method.GET to { req ->
                        val network = req.path("network")!!
                        val nodes = repo.nodes(network)
                        json(JSONArray(nodes))
                    }
            ),
            "/ajax" bind routes(
                    "/networks/{network}/nodes/{node}/status" bind Method.GET to { req ->
                        val network = req.path("network")!!
                        val node = req.path("node")!!
                        val context = RealContext(network)
                        val messageSink = buildMessageSink(context)
//                        val executor = TaskExecutor(messageSink)
//                        val scopedRegistry = registry.overide(context)
                        val status = NodeStatusTask(context, node).exec()
                        json(status)
                    }

            )

    )

    private fun doStartNetwork(req: Request): Response {
        val network = req.path("network")!!
        val context = RealContext(network)
        val executor = buildExecutor(context)
        val scopedRegistry = registry.overide(context)

        return if (!repo.isNetworkRunning(network)) {
            val tasks = listOf(StopCordaNodesTask(scopedRegistry), StartCordaNodesTask(scopedRegistry))
            executor.exec(tasks)

            val page = renderTemplate("networkStarted.md",
                    mapOf("networkName" to network))
            html(page)

        } else {
            throw RuntimeException("network $network is already running")
        }
    }

    private fun doStopNetwork(req: Request): Response {
        val network = req.path("network")!!
        val context = RealContext(network)
        val executor = buildExecutor(context)
        val scopedRegistry = registry.overide(context)

        executor.exec(StopCordaNodesTask(scopedRegistry))

        val page = renderTemplate("networkStopped.md",
                mapOf("networkName" to network))
        return html(page)
    }

    override fun invoke(p1: Request) = routes(p1)

    private fun html(page: String): Response {
        return Response(Status.OK)
                .body(page)
                .header("Content-Type", "text/html; charset=utf-8")

    }

    private fun css(page: String): Response {
        return Response(Status.OK)
                .body(page)
                .header("Content-Type", "text/css; charset=utf-8")

    }

    private fun text(page: String): Response {
        return Response(Status.OK)
                .body(page)
                .header("Content-Type", "text/plain; charset=utf-8")

    }

    private fun json(data: Map<String, Any>): Response {
        return Response(Status.OK)
                .body(JSONObject(data).toString(2))
                .header("Content-Type", "application/json; charset=utf-8")

    }

    private fun json(data: JSONArray): Response {
        return Response(Status.OK)
                .body(data.toString(2))
                .header("Content-Type", "application/json; charset=utf-8")

    }

    private fun binary(data: InputStream): Response {
        return Response(Status.OK)
                .body(data)
                .header("Content-Type", "application/octet-stream")


    }


    private fun notFound(message: String): Response {
        return Response(Status.NOT_FOUND)
                .body(message)
                .header("Content-Type", "text/plain; charset=utf-8")

    }

    private fun renderTemplate(path: String, params: Map<String, Any?> = emptyMap()): String {
        val html = renderMustache(path, params)

        // merge with layout.html.html
        val layout = FileInputStream("src/main/resources/www/layout.html").bufferedReader().use { it.readText() }
        val result = layout.replace("<!--BODYTEXT-->", html, false)
        //println(result)
        return result
    }


    private fun renderMustache(path: String, params: Map<String, Any?>): String {
        try {
            // mustache processing
            val content = readFileAsText("src/main/resources/www/$path", params)

            // markdown processing
            if (path.endsWith(".md")) {
                val options = MutableDataSet()
                val parser = Parser.builder(options).build()
                val renderer = HtmlRenderer.builder(options).build()
                val document = parser.parse(content)
                return renderer.render(document)
            } else {
                return content
            }
        } catch (ex: Exception) {
            return "<pre>" + ex.message!! + "</pre>"
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


    private fun unpackCreateNetworkForm(req: Request): CreateNetworkRequest {
        try {
            val params = req.formAsMap()

            val name = params["networkName"]!!.single()!!
            val cordaVersion = params["cordaVersion"]!!.single()!!
            val organisations = params["organisations"]!!
                    .single()!!
                    .trim()
                    .split("\n")
                    .map { it.trim() }

            return CreateNetworkRequest(cordaVersion, name, organisations + listOf("O=Notary,L=London,C=GB"))
        } catch (ex: Exception) {
            throw RuntimeException("Incorrect params passed to Create Network - '${ex.message}'", ex)
        }
    }

    private fun tempDir(ctx: TaskContext): String {
        val tmpDir = "${ctx.workingDir}/tmp"
        File(tmpDir).mkdirs()
        return tmpDir
    }


    data class CreateNetworkRequest(val cordaVersion: String, val name: String, val organisations: List<String>)


}