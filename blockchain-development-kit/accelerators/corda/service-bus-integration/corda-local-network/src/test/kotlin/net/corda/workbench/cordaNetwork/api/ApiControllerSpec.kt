package net.corda.workbench.cordaNetwork.api

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import net.corda.workbench.commons.event.FileEventStore
import net.corda.workbench.commons.processManager.ProcessManager
import net.corda.workbench.commons.registry.Registry
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.json.JSONArray
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.io.File
import java.io.FileInputStream

@RunWith(JUnitPlatform::class)
object ApiControllerSpec : Spek({

    val baseUrl = "http://corda-local-network:11114/"
    val networkName = "controllerspec"
    lateinit var registry : Registry

    describe("The API Controller") {

        beforeGroup {
            println("## beforeGroup  - cleanup")
            val deleted = File(System.getProperty("user.home") + "/.corda-local-network/$networkName").deleteRecursively()
            assertThat(deleted, equalTo(true))


            registry = Registry()
            val es = FileEventStore()
            val processManager = ProcessManager()
            registry.store(es).store(processManager)

            // start app, but don't assign output. all communication will be done via REST
            net.corda.workbench.cordaNetwork.Javalin(11114).init(registry)
            println("app started...")
        }

        afterGroup {
            println("killing all running processes")
            registry.retrieve(ProcessManager::class.java).killAll()
        }

        context("Building and starting a network") {

            afterEachTest {
                // cleanup after each run
                khttp.delete(url = baseUrl + networkName)

                println("killing all running processes")
                registry.retrieve(ProcessManager::class.java).killAll()
                //.killAll()
            }


            it("should build and run new network") {
                val payload = """
               ["O=Notary,L=London,C=GB", "O=Alice,L=New York,C=US","O=Bob,L=Paris,C=FR"]
"""
                // create nodes
                val response1 = khttp.post(baseUrl + "$networkName/nodes/create", data = JSONArray(payload))
                println("Completed create nodes:  $response1")
                assert.that(response1.statusCode, equalTo(200))

                // deploy app
                val bytes = FileInputStream("src/test/resources/cordapps/refrigerated-transportation.jar").readBytes()
                println("there are ${bytes.size} bytes")

                val response2 = khttp.post(baseUrl + "$networkName/apps/RefrigeratedTransportation/deploy",
                        data = bytes, headers = mapOf("Content-Type" to "application/octet-stream"))
                println("Completed deploy app:  $response2")
                assert.that(response2.statusCode, equalTo(200))

                // check tasks history
                val response3 = khttp.get(baseUrl + "$networkName/tasks/history")
                println("Read task histrory:  $response3")
                println(response3.text)
                assert.that(response3.statusCode, equalTo(200))

                // start
                val response4 = khttp.post(baseUrl + "$networkName/start", data = JSONArray(payload))
                println("Completed start app:  $response4")
                assert.that(response4.statusCode, equalTo(200))
                // force a delay, otherwise cleanup get caught in race conditions (
                Thread.sleep(5000)
            }
        }
    }
})
