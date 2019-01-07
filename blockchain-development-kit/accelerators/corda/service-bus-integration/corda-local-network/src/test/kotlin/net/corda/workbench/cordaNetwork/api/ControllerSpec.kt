package net.corda.workbench.cordaNetwork.api

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
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
object ControllerSpec : Spek({

    val baseUrl = "http://corda-local-network:11114/"

    describe("The API Controller") {

        beforeGroup {
            File("~/.corda-local-network/default").deleteRecursively()
            // start app, but don't assign output. all communication will be done via REST
            net.corda.workbench.cordaNetwork.Javalin(11114).init()
            println("app started...")
        }

        afterGroup {
            // TODO moritzplatt 15/11/2018 -- do nodes need to be stopped?
        }

        context("Building and starting a network") {

            beforeEachTest {
                // cleanup before each run
                khttp.delete(url = baseUrl + "default")
            }


            it("should build and run new network") {
                val payload = """
               ["O=Notary,L=London,C=GB", "O=Alice,L=New York,C=US","O=Bob,L=Paris,C=FR"]
"""
                // create nodes
                val response1 = khttp.post(baseUrl + "default/nodes/create", data = JSONArray(payload))
                println("Completed create nodes:  $response1")
                assert.that(response1.statusCode, equalTo(200))

                // deploy app
                val bytes = FileInputStream("src/test/resources/cordapps/refrigerated-transportation.jar").readBytes()
                println("there are ${bytes.size} bytes")

                val response2 = khttp.post(baseUrl + "default/apps/RefrigeratedTransportation/deploy",
                        data = bytes, headers = mapOf("Content-Type" to "application/octet-stream"))
                println("Completed deploy app:  $response2")
                assert.that(response2.statusCode, equalTo(200))

                // check tasks history
                val response3 = khttp.get(baseUrl + "default/tasks/history")
                println("Read task histrory:  $response3")
                println(response3.text)
                assert.that(response3.statusCode, equalTo(200))

                // start
                val response4 = khttp.post(baseUrl + "default/start", data = JSONArray(payload))
                println("Completed start app:  $response4")
                assert.that(response4.statusCode, equalTo(200))

            }
        }
    }
})
