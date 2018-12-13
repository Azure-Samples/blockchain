package net.corda.workbench.refrigeratedTransportation

import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.getOrThrow
import net.corda.testing.internal.chooseIdentityAndCert
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkNotarySpec
import net.corda.testing.node.MockNodeParameters
import net.corda.testing.node.StartedMockNode
import net.corda.workbench.refrigeratedTransportation.flow.CompleteFlow
import net.corda.workbench.refrigeratedTransportation.flow.CompleteFlowResponder
import net.corda.workbench.refrigeratedTransportation.flow.CreateFlow
import net.corda.workbench.refrigeratedTransportation.flow.CreateFlowResponder
import net.corda.workbench.refrigeratedTransportation.flow.ReportToObserverFlowResponder
import net.corda.workbench.refrigeratedTransportation.flow.TransferResponsibilityFlow
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

/**
 * Practical exercise instructions Flows part 1.
 * Uncomment the unit tests and use the hints + unit test body to complete the FLows such that the unit tests pass.
 */
class CompleteFlowTests {

    companion object {

        lateinit var mockNetwork: MockNetwork
        lateinit var a: StartedMockNode
        lateinit var b: StartedMockNode
        lateinit var c: StartedMockNode
        lateinit var d: StartedMockNode
        lateinit var e: StartedMockNode

        @BeforeClass
        @JvmStatic
        fun setup() {
            mockNetwork = MockNetwork(listOf("net.corda.workbench.refrigeratedTransportation"),
                    notarySpecs = listOf(MockNetworkNotarySpec(CordaX500Name("Notary", "London", "GB"))))
            a = mockNetwork.createNode(MockNodeParameters())
            b = mockNetwork.createNode(MockNodeParameters())
            c = mockNetwork.createNode(MockNodeParameters())
            d = mockNetwork.createNode(MockNodeParameters())
            e = mockNetwork.createNode(MockNodeParameters())

            val startedNodes = arrayListOf(a, b, c, d, e)
            // For real nodes this happens automatically, but we have to manually register the flow for tests
            startedNodes.forEach { it.registerInitiatedFlow(CreateFlowResponder::class.java) }
            startedNodes.forEach { it.registerInitiatedFlow(ReportToObserverFlowResponder::class.java) }
            startedNodes.forEach { it.registerInitiatedFlow(CompleteFlowResponder::class.java) }


            mockNetwork.runNetwork()
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            //mockNetwork.
            mockNetwork.stopNodes()
        }

        fun party(node: StartedMockNode): Party {
            return node.info.chooseIdentityAndCert().party
        }
    }

    /**
     * The basic happy path.
     */
    @Test
    fun flowHappyPath() {

        val owner = party(a)
        val device = party(b)
        val supplyChainOwner = party(c)
        val supplyChainObserver = party(d)

        val item = Shipment(owner = owner, device = device,
                supplyChainOwner = supplyChainOwner, supplyChainObserver = supplyChainObserver,
                minHumidity = 10, maxHumidity = 99, minTemperature = 0, maxTemperature = 30)
        val createFlow = CreateFlow(item)
        val future1 = a.startFlow(createFlow)
        mockNetwork.runNetwork()

        val ptx1: SignedTransaction = future1.getOrThrow()
        println(ptx1.tx)

        val transferFlow = TransferResponsibilityFlow(item.linearId, Transfer(party(e)))
        val future2 = a.startFlow(transferFlow)
        mockNetwork.runNetwork()

        // Return the unsigned(!) SignedTransaction object from the CreateFlow.
        val ptx2: SignedTransaction = future2.getOrThrow()
        println(ptx2.tx)

        val completeFlow = CompleteFlow(item.linearId)
        val future3 = c.startFlow(completeFlow)
        mockNetwork.runNetwork()

        val ptx3: SignedTransaction = future3.getOrThrow()

        assert(ptx3.tx.inputs.size == 1)
        assert(ptx3.tx.outputs.single().data is Shipment)

        val command = ptx3.tx.commands.single()
        assert(command.value is RefrigerationContract.Commands.Complete)

        val result = ptx3.tx.outputs.single().data as Shipment
        assert(result.state == StateType.Completed)

    }
}
