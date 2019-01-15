package net.corda.workbench.cordaNetwork.events

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.event.FileEventStore
import net.corda.workbench.transactionBuilder.events.EventFactory
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.util.*

@RunWith(JUnitPlatform::class)
object RepoSpec : Spek({

    lateinit var es: EventStore
    lateinit var repo: Repo

    describe("A simple event sourced based repo") {

        beforeEachTest {
            es = FileEventStore()
            repo = Repo(es)
        }

        it("should return available networks") {
            es.storeEvent(EventFactory.NETWORK_CREATED("networkA"))
            es.storeEvent(EventFactory.NETWORK_CREATED("networkB"))
            es.storeEvent(EventFactory.NETWORK_STARTED("networkA"))


            val networks = repo.networks()
            assertThat(networks.size, equalTo(2))
            assertThat(networks[0], equalTo(NetworkInfo("networkA", "Running")))
            assertThat(networks[1], equalTo(NetworkInfo("networkB", "Never Started")))
        }


        it("should return available nodes for network") {
            es.storeEvent(EventFactory.NODES_CREATED("net1",
                    listOf("O=Alice,L=London,C=GB", "O=Bob,L=New York,C=US")))

            val nodes = repo.nodes("net1")
            assertThat(nodes.size, equalTo(2))
            assertThat(nodes[0], equalTo(NodeInfo("O=Alice,L=London,C=GB")))
        }

        it("should return single running nodes") {
            es.storeEvent(EventFactory.NODE_STARTED("net1",
                    "alice", 1234L, UUID.randomUUID()))

            val nodes = repo.runningNodes()
            assertThat(nodes.size, equalTo(1))
            assertThat(nodes[0], equalTo(RunningNode("net1", "alice", 1234L)))
        }

        it("should return running nodes that haven't been stopped") {
            es.storeEvent(EventFactory.NODE_STARTED("net1", "alice", 10001L, UUID.randomUUID()))
            es.storeEvent(EventFactory.NODE_STARTED("net1", "bob", 10002L, UUID.randomUUID()))
            es.storeEvent(EventFactory.NODE_STARTED("net2", "alice", 10003L, UUID.randomUUID()))
            es.storeEvent(EventFactory.NODE_STOPPED("net1", "alice", 10001L, "Manual shutdown"))

            val nodes = repo.runningNodes()
            assertThat(nodes.size, equalTo(2))
            assertThat(nodes[0], equalTo(RunningNode("net1", "bob", 10002L)))
            assertThat(nodes[1], equalTo(RunningNode("net2", "alice", 10003L)))
        }

        it("should return latest state for running node if multiple events") {
            es.storeEvent(EventFactory.NODE_STARTED("net1", "alice", 123L, UUID.randomUUID()))
            es.storeEvent(EventFactory.NODE_STARTED("net1", "alice", 999L, UUID.randomUUID()))
            es.storeEvent(EventFactory.NODE_STARTED("net1", "bob", 100L, UUID.randomUUID()))
            es.storeEvent(EventFactory.NODE_STOPPED("net1", "bob", 100L, "Manual shutdown"))
            es.storeEvent(EventFactory.NODE_STOPPED("net1", "bob", 100L, "Forced shutdown"))
            es.storeEvent(EventFactory.NODE_STOPPED("net1", "charlie", 321, "Mistake"))

            val nodes = repo.runningNodes()
            assertThat(nodes.size, equalTo(1))
            assertThat(nodes[0], equalTo(RunningNode("net1", "alice", 999L)))
        }


        it("should return running status of the network") {
            es.storeEvent(EventFactory.NETWORK_STARTED("net1"))
            es.storeEvent(EventFactory.NETWORK_STOPPED("net1"))
            es.storeEvent(EventFactory.NETWORK_STARTED("net2"))

            assertThat(repo.isNetworkRunning("net1"), equalTo(false))
            assertThat(repo.isNetworkRunning("net2"), equalTo(true))
            assertThat(repo.isNetworkRunning("netX"), equalTo(false))
        }


        it("should return list of deployed cordapps") {
            es.storeEvent(EventFactory.CORDAPP_DEPLOYED("net1","app1.jar",123,"hash1"))
            es.storeEvent(EventFactory.CORDAPP_DEPLOYED("net2","app1.jar",123,"hash1"))
            es.storeEvent(EventFactory.CORDAPP_DEPLOYED("net1","app2.jar",999,"hash2"))
            es.storeEvent(EventFactory.CORDAPP_DEPLOYED("net1","app1.jar",321,"hash3"))

            assertThat(repo.deployedCordapps("net1").size, equalTo(2))
            assertThat(repo.deployedCordapps("net1")[0].name, equalTo("app1.jar"))
            assertThat(repo.deployedCordapps("net1")[0].size, equalTo(321))
            assertThat(repo.deployedCordapps("net1")[0].md5Hash, equalTo("hash3"))
            assertThat(repo.deployedCordapps("net1")[1].name, equalTo("app2.jar"))
            assertThat(repo.deployedCordapps("net1")[1].size, equalTo(999))
            assertThat(repo.deployedCordapps("net1")[1].md5Hash, equalTo("hash2"))

            assertThat(repo.deployedCordapps("net2").size, equalTo(1))
            assertThat(repo.deployedCordapps("net2")[0].name, equalTo("app1.jar"))
            assertThat(repo.deployedCordapps("net2")[0].size, equalTo(123))
            assertThat(repo.deployedCordapps("net2")[0].md5Hash, equalTo("hash1"))

            assertThat(repo.deployedCordapps("netX").size, equalTo(0))

        }



    }

})