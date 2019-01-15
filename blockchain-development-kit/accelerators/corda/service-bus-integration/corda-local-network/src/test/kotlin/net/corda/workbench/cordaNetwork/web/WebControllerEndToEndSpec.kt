package net.corda.workbench.cordaNetwork.web

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import net.corda.workbench.commons.event.FileEventStore
import net.corda.workbench.commons.processManager.ProcessManager
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.cordaNetwork.AppConfig
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.body.form
import org.http4k.hamkrest.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.util.*

/**
 * Run full "end-to-end" style tests, i.e a sequence of interactions
 * with minimal or no mocking. Might be slower to run and more "brittle" than
 * the unit tests
 */
@RunWith(JUnitPlatform::class)
object WebControllerEndToEndSpec : Spek({

    //val baseUrl = "http://corda-local-network:1115/"

    val es = FileEventStore()
    val processManager = ProcessManager()
    val registry = Registry().store(es).store(processManager).store(AppConfig())
    val controller = WebController(registry)

    describe("Creating and starting a new network") {

        beforeEachTest {
            es.truncate()
            processManager.killAll()
        }

        afterEachTest {
            processManager.killAll()
        }

        it("should create and start and network") {


            val name = "testnet-" + UUID.randomUUID().toString().substring(0, 8)
            println(name)

            // 1 - Create Network
            val response1 = controller(Request(Method.POST, "/web/networks/create")
                    .form("networkName", name)
                    .form("organisations", "Alice\nBob"))

            assertThat(response1, hasStatus(Status.OK))
            assertThat(response1, hasBody(containsSubstring("$name Created")))

            // 2 - check status
            val response2 = controller(Request(Method.GET, "/web/networks/$name/status"))
            assertThat(response2, hasStatus(Status.OK))
            assertThat(response2, hasBody(containsSubstring(name)))
            assertThat(response2, hasBody(containsSubstring("alice")))
            assertThat(response2, hasBody(containsSubstring("bob")))
            //todo - check status is not running

            // 3 - start the network
            val response3 = controller(Request(Method.POST, "/web/networks/$name/start"))
            assertThat(response3, hasStatus(Status.OK))
            assertThat(response3, hasBody(containsSubstring(name)))


            for (i in 1..10) {
                println("checking node status")
                val response = controller(Request(Method.GET, "/ajax/networks/$name/nodes/alice/status"))
                assertThat(response, hasStatus(Status.OK))
                println(response.bodyString())
                Thread.sleep(5000)
            }


            // 3 - todo , deploy app


            // 4 - start the network
        }


    }
})


