package net.corda.workbench.chat

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.CommandWithParties
import net.corda.core.contracts.Contract
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction


class ChatContract : Contract {
    // This is used to identify our contract when building a transaction
    companion object {
        @JvmStatic
        val ID = "net.corda.workbench.chat.ChatContract"
    }

    // A transaction is considered valid if the verify() function of the contract of each of the transaction's input
    // and output states does not throw an exception.
    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands>()

        when (command.value) {
            is Commands.Start -> validateStart(tx, command)
            is Commands.Chat -> validateChat(tx, command)
            is Commands.Bye -> validateComplete(tx, command)
        }
    }

    private fun validateStart(tx: LedgerTransaction, command: CommandWithParties<Commands>) {
        requireThat {
            // Generic constraints
            "No inputs should be consumed when starting a Chat." using (tx.inputs.isEmpty())
            "Only one output state should be created when starting a Chat." using (tx.outputs.size == 1)

            // Check the signing
            val out = tx.outputsOfType<Message>().single()
            val signers = command.signers.toSet()
            "InterlocutorA must sign Chat transaction." using (signers.contains(out.interlocutorA.owningKey))
            "InterlocutorB must sign Chat transaction." using (signers.contains(out.interlocutorB.owningKey))
            "Incorrect number of signers." using (signers.size == 2)

            // check the message
            "Must start with 'Hi' message" using (out.message.equals("hi", ignoreCase = true))
        }
    }

    private fun validateChat(tx: LedgerTransaction, command: CommandWithParties<Commands>) {

        requireThat {
            // Generic constraints .
            "Only one input state should be consumed when chatting." using (tx.inputs.size == 1)
            "Only one output state should be created when chatting." using (tx.outputs.size == 1)



            val out = tx.outputsOfType<Message>().single()
            val signers = command.signers.toSet()
            println("Signers are : ${signers.size}")
            "InterlocutorA must sign Chat transaction." using (signers.contains(out.interlocutorA.owningKey))
            "InterlocutorB must sign Chat transaction." using (signers.contains(out.interlocutorB.owningKey))
            "Incorrect number of signers." using (signers.size == 2)

            // are we in the correct state
            val input = tx.inputsOfType<Message>().single()
            "Can only chat if neither side has said 'bye'" using (!input.message.equals("bye", ignoreCase = true))


            // check the message
            "Must have some text in the message" using (out.message.isNotBlank())
            "No rude words" using (!out.message.contains("bitcoin", ignoreCase = true))
        }
    }


    private fun validateComplete(tx: LedgerTransaction, command: CommandWithParties<Commands>) {
        requireThat {
            // Generic constraints around the Telemetry transaction.
            "Only one input state should be consumed when saying bye." using (tx.inputs.size == 1)
            "Only one output state should be created when saying bye." using (tx.outputs.size == 1)

            val out = tx.outputsOfType<Message>().single()
            val signers = command.signers.toSet()
            "InterlocutorA must sign Bye transaction." using (signers.contains(out.interlocutorA.owningKey))
            "InterlocutorB must sign Bye transaction." using (signers.contains(out.interlocutorB.owningKey))
            "Incorrect number of signers." using (signers.size == 2)


            // Are we in the correct state
            val input = tx.inputsOfType<Message>().single()
            "Can only say 'bye' once" using (!input.message.equals("bye", ignoreCase = true))
            "Must say bye" using (out.message.equals("bye", ignoreCase = true))
        }
    }


    // Used to indicate the transaction's intent.
    interface Commands : CommandData {
        /**
         * Start a chat. Must be polite and say "hi"
         */
        class Start : TypeOnlyCommandData(), Commands

        /**
         * Add a message to the chat. Either side can initiate
         */
        class Chat : TypeOnlyCommandData(), Commands

        /**
         * End the chat. Must be polite and say "bye". Either side can initiate
         */
        class Bye : TypeOnlyCommandData(), Commands


    }
}




