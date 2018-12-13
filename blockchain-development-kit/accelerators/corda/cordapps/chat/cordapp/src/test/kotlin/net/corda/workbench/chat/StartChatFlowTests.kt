package net.corda.workbench.chat

import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.getOrThrow
import net.corda.testing.internal.chooseIdentityAndCert
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkNotarySpec
import net.corda.testing.node.MockNodeParameters
import net.corda.testing.node.StartedMockNode
import net.corda.workbench.chat.flow.*
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals


class StartChatFlowTests {

    companion object {

        lateinit var mockNetwork: MockNetwork
        lateinit var a: StartedMockNode
        lateinit var b: StartedMockNode

        private val allParties = ArrayList<Party>()

        @BeforeClass
        @JvmStatic
        fun setup() {
            mockNetwork = MockNetwork(listOf("net.corda.workbench.chat"),
                    notarySpecs = listOf(MockNetworkNotarySpec(CordaX500Name("Notary", "London", "GB"))))
            a = mockNetwork.createNode(MockNodeParameters())
            b = mockNetwork.createNode(MockNodeParameters())


            allParties.add(party(a))
            allParties.add(party(b))


            val startedNodes = arrayListOf(a, b)
            // For real nodes this happens automatically, but we have to manually register the flow for tests
            startedNodes.forEach { it.registerInitiatedFlow(StartChatFlowResponder::class.java) }

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
     * The basic happy path. The flow is run and is completed, signed by both parties
     */
    @Test
    fun flowHappyPath() {

        val to = party(b)
        val flow = StartChatFlow(to)
        val future = a.startFlow(flow)
        mockNetwork.runNetwork()

        // Return the unsigned(!) SignedTransaction object from the CreateFlow.
        val ptx: SignedTransaction = future.getOrThrow()
        println(ptx.tx)

        // Check the transaction is well formed...
        assert(ptx.tx.inputs.isEmpty())
        assert(ptx.tx.outputs.single().data is Message)
        val command = ptx.tx.commands.single()
        assert(command.value is ChatContract.Commands.Start)
        val msg = ptx.tx.outputs.single().data as Message
        assert(command.signers == msg.participants.map { it.owningKey })
        assert(msg.message == "hi")
    }
}
