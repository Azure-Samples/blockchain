package net.corda.workbench.cordaNetwork.web

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import net.corda.workbench.commons.event.FileEventStore
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.transactionBuilder.events.EventFactory
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

/**
 * Quick to run unit tests, with any slow internal processing mocked out.
 */
@RunWith(JUnitPlatform::class)
object WebControllerUnitSpec : Spek({

    //val baseUrl = "http://corda-local-network:1115/"

    val es = FileEventStore()
    val registry = Registry().store(es)
    val controller = WebController(registry)

    describe("The Web Controller") {

        beforeEachTest {
            es.truncate()
        }

        it("should redirect to home page") {
            val response = controller(Request(Method.GET, "/"))

            assertThat(response, hasStatus(Status.PERMANENT_REDIRECT))
            assertThat(response, hasHeader("Location", "/web/home"))
        }

        it("should render home page") {
            val response = controller(Request(Method.GET, "/web/home"))

            assertThat(response, hasStatus(Status.OK))
            // todo - would be nice to add some JQuery style matchers driven by https://jsoup.org/
            // https://github.com/ianmorgan/canfactory-html
            assertThat(response, hasBody(containsSubstring("Corda Local Network")))
        }

        it("should render list of networks on home page") {
            es.storeEvent(EventFactory.NETWORK_CREATED("NetworkA"))
            val response = controller(Request(Method.GET, "/web/home"))

            assertThat(response, hasStatus(Status.OK))
            assertThat(response, hasBody(containsSubstring("Available Networks")))
            assertThat(response, hasBody(containsSubstring("NetworkA")))
        }

        it("should render list of nodes for network") {
            es.storeEvent(EventFactory.NETWORK_CREATED("net1"))
            es.storeEvent(EventFactory.NODES_CREATED("net1", listOf("O=Alice,L=London,C=GB", "O=Bob,L=New York,C=US")))
            val response = controller(Request(Method.GET, "/web/networks/net1"))

            assertThat(response, hasStatus(Status.OK))
            assertThat(response, hasBody(containsSubstring("Network net1")))
            assertThat(response, hasBody(containsSubstring("Alice")))
            assertThat(response, hasBody(containsSubstring("Bob")))
        }

        it("should render create network page") {
            val response = controller(Request(Method.GET, "/web/networks/create"))

            assertThat(response, hasStatus(Status.OK))
            assertThat(response, hasBody(containsSubstring("Create Network")))
        }


        it("should create a network") {
            val response = controller(Request(Method.POST, "/web/networks/create")
                    .form("networkName", "testnet01")
                    .form("organisations", "O=Alice,L=New York,C=US \n Bob"))

            assertThat(response, hasStatus(Status.OK))
            assertThat(response, hasBody(containsSubstring("testnet01 Created")))

        }


    }
})


