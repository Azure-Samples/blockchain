package net.corda.workbench.serviceBus.messaging

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isBlank
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.serviceBus.cordaTransactionBuilder.FakeTransactionBuilderClient
import net.corda.workbench.serviceBus.cordaTransactionBuilder.TransactionBuilderClient
import net.corda.workbench.serviceBus.repo.InMemoryWorkbenchRepo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.util.*


@RunWith(JUnitPlatform::class)
object IngressMessageProcessorSpec : Spek({

    describe("It should process message ") {
        // default wiring
        val reg = Registry()
        val id = UUID.randomUUID().toString()
        val substitutions = mapOf("linearId" to id)
        val helper = DataSetHelper("refrigeratedTransportation", "happyPath", substitutions)


        context("Good messages") {
            it("should process a 'create' message") {
                // setup
                reg.store(InMemoryWorkbenchRepo())
                reg.store(FakeTransactionBuilderClient())

                val client = reg.retrieve(TransactionBuilderClient::class.java) as FakeTransactionBuilderClient
                client.addResult(helper.rawCordaResponse("01-createResponse.json"))

                // build and run a message
                val msg = helper.ingressMessage("01-create.json")
                val egressQueueClient = FakeQueueClient()
                val processor = IngressMessageProcessor(reg, msg, egressQueueClient)
                processor.run()

                // verify generated messages
                val msgs = egressQueueClient.messages
                assert.that(msgs.size, equalTo(3))

                val checkMsg1 = helper.checkEgressMessage(msgs[0], "01a-submitted.json", substitutions)
                assert.that(checkMsg1, isBlank)
                val checkMsg2 = helper.checkEgressMessage(msgs[1], "01b-committed.json", substitutions)
                assert.that(checkMsg2, isBlank)

                val checkMsg3 = helper.checkEgressMessage(msgs[2], "01c-transaction.json", substitutions)
                //assert.that(checkMsg3, isBlank)
            }

            xit("should process happy path lifeccyle") {
                // todo - will need full set of mocked repsonse
                // to make this test pass

                // setup
                reg.store(InMemoryWorkbenchRepo())
                reg.store(FakeTransactionBuilderClient())

                val id = UUID.randomUUID().toString()
                val substitutions = mapOf("linearId" to id)
                val helper = DataSetHelper("refrigeratedTransportation", "happyPath", substitutions)


                val params = mapOf("linearId" to id)
                val egressQueue = FakeQueueClient()
                processMsg(reg, "happyPath", "01-create.json", params, egressQueue)
                processMsg(reg, "happyPath", "02-telemetry.json", params, egressQueue)
                processMsg(reg, "happyPath", "03-transfer.json", params, egressQueue)
                processMsg(reg, "happyPath", "04-complete.json", params, egressQueue)

                val client = reg.retrieve(TransactionBuilderClient::class.java) as FakeTransactionBuilderClient

                val msgs = egressQueue.messages
                assert.that(msgs.size, equalTo(12))

            }

            it ("should generate a failed message if problems making Corda call"){
                // setup
                val transactionBuilderClient = FakeTransactionBuilderClient()
                reg.store(InMemoryWorkbenchRepo())
                reg.store(transactionBuilderClient)
                transactionBuilderClient.addException(RuntimeException("Problem with Corda"))

                // build and run a message
                val msg = helper.ingressMessage("01-create.json")
                val egressQueueClient = FakeQueueClient()
                val processor = IngressMessageProcessor(reg, msg, egressQueueClient)
                processor.run()

                // verify generated messages
                val msgs = egressQueueClient.messages
                assert.that(msgs.size, equalTo(2))

                val checkMsg1 = helper.checkEgressMessage(msgs[0], "01a-submitted.json", substitutions)
                assert.that(checkMsg1, isBlank)
                val checkMsg2 = helper.checkEgressMessage(msgs[1], "01x-failed.json", substitutions)
                assert.that(checkMsg2, isBlank)

            }




        }
    }
})



