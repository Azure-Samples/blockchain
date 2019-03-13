package net.corda.workbench.chat

import net.corda.core.contracts.UniqueIdentifier
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


class ChatFlowTests {

    companion object {

        lateinit var mockNetwork: MockNetwork
        lateinit var alice: StartedMockNode
        lateinit var bob: StartedMockNode

        private val allParties = ArrayList<Party>()

        @BeforeClass
        @JvmStatic
        fun setup() {
            mockNetwork = MockNetwork(listOf("net.corda.workbench.chat"),
                    notarySpecs = listOf(MockNetworkNotarySpec(CordaX500Name("Notary", "London", "GB"))))
            alice = mockNetwork.createNode(MockNodeParameters())
            bob = mockNetwork.createNode(MockNodeParameters())


            allParties.add(party(alice))
            allParties.add(party(bob))


            val startedNodes = arrayListOf(alice, bob)
            // For real nodes this happens automatically, but we have to manually register the flow for tests
            startedNodes.forEach { it.registerInitiatedFlow(StartChatFlowResponder::class.java) }
            startedNodes.forEach { it.registerInitiatedFlow(ChatFlowResponder::class.java) }


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


        // 1. Alice starts a chat with bob
        val linearId = UniqueIdentifier()
        val to = party(bob)
        val startFlow = StartChatFlow(to, linearId)
        val future1 = alice.startFlow(startFlow)
        mockNetwork.runNetwork()

        // Return the unsigned(!) SignedTransaction object from the CreateFlow.
        val ptxStart: SignedTransaction = future1.getOrThrow()
        println(ptxStart.tx)


        // 2. Bob answers
        val chatFlow = ChatFlow("yes alice?", linearId)
        val future = bob.startFlow(chatFlow)
        mockNetwork.runNetwork()

        // Return the unsigned(!) SignedTransaction object from the CreateFlow.
        val ptxChat: SignedTransaction = future.getOrThrow()
        println(ptxChat.tx)

        // Check the transaction is well formed...
        assert(ptxChat.tx.inputs.count() == 1)
        assert(ptxChat.tx.outputs.single().data is Message)
        val command = ptxChat.tx.commands.single()
        assert(command.value is ChatContract.Commands.Chat)
        val msg = ptxChat.tx.outputs.single().data as Message
        assert(command.signers == msg.participants.map { it.owningKey })
        assert(msg.message == "yes alice?")


        // 3. And Alice replies back to bob
        val chatFlow2 = ChatFlow("nothing, bob", linearId)
        val future2= alice.startFlow(chatFlow2)
        mockNetwork.runNetwork()

        // Return the unsigned(!) SignedTransaction object from the CreateFlow.
        val ptxChat2: SignedTransaction = future2.getOrThrow()
        println(ptxChat2.tx)

        // Check the transaction is well formed...
        assert(ptxChat2.tx.inputs.count() == 1)
        assert(ptxChat2.tx.outputs.single().data is Message)
        val command2 = ptxChat2.tx.commands.single()
        assert(command2.value is ChatContract.Commands.Chat)
        val msg2 = ptxChat2.tx.outputs.single().data as Message
        assert(command2.signers == msg2.participants.map { it.owningKey })
        assert(msg2.message == "nothing, bob")
    }
}
