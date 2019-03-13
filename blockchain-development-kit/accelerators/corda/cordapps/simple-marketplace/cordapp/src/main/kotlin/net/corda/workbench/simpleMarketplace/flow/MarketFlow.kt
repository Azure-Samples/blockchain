package net.corda.workbench.simpleMarketplace.flow

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.Command
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.ProgressTracker.Step
import net.corda.workbench.simpleMarketplace.contract.MarketPlaceContract
import net.corda.workbench.simpleMarketplace.contract.MarketPlaceContract.Companion.ID
import net.corda.workbench.simpleMarketplace.state.AcceptedState
import net.corda.workbench.simpleMarketplace.state.AvailableItem


object MarketFlow {
    @InitiatingFlow
    @StartableByRPC
    class Initiator(val _item: AvailableItem,
                    val offeredPrice : Double,
                    val otherParty: Party) : FlowLogic<SignedTransaction>() {

        companion object {
            object GENERATING_TRANSACTION : Step("Generating transaction based on new IOU.")
            object VERIFYING_TRANSACTION : Step("Verifying contract constraints.")
            object SIGNING_TRANSACTION : Step("Signing transaction with our private key.")
            object GATHERING_SIGS : Step("Gathering the counterparty's signature.") {
                override fun childProgressTracker() = CollectSignaturesFlow.tracker()
            }

            object FINALISING_TRANSACTION : Step("Obtaining notary signature and recording transaction.") {
                override fun childProgressTracker() = FinalityFlow.tracker()
            }

            fun tracker() = ProgressTracker(
                    GENERATING_TRANSACTION,
                    VERIFYING_TRANSACTION,
                    SIGNING_TRANSACTION,
                    GATHERING_SIGS,
                    FINALISING_TRANSACTION
            )
        }

        override val progressTracker = tracker()

        @Suspendable
        override fun call(): SignedTransaction {

            val notary = serviceHub.networkMapCache.notaryIdentities[0]


            progressTracker.currentStep = GENERATING_TRANSACTION

            val _accepted = AcceptedState(serviceHub.myInfo.legalIdentities.first(), otherParty, _item, offeredPrice)
            val txCommand = Command(MarketPlaceContract.Commands.CreateTransfert(), _accepted.participants.map { it.owningKey })
            _accepted.ChangeStateTypeToApproved()
            val txBuilder = TransactionBuilder(notary)
                    .addOutputState(_accepted, ID)
                    .addCommand(txCommand)


            progressTracker.currentStep = VERIFYING_TRANSACTION

            txBuilder.verify(serviceHub)


            progressTracker.currentStep = SIGNING_TRANSACTION

            val partSignedTx = serviceHub.signInitialTransaction(txBuilder)


            progressTracker.currentStep = GATHERING_SIGS
            val otherPartyFlow = initiateFlow(otherParty)
            val fullySignedTx = subFlow(CollectSignaturesFlow(partSignedTx, setOf(otherPartyFlow), GATHERING_SIGS.childProgressTracker()))


            progressTracker.currentStep = FINALISING_TRANSACTION

            return subFlow(FinalityFlow(fullySignedTx, FINALISING_TRANSACTION.childProgressTracker()))
        }
    }

    @InitiatedBy(Initiator::class)
    class Acceptor(val otherPartyFlow: FlowSession) : FlowLogic<SignedTransaction>() {
        @Suspendable
        override fun call(): SignedTransaction {
            val signTransactionFlow = object : SignTransactionFlow(otherPartyFlow) {
                override fun checkTransaction(stx: SignedTransaction) = requireThat {
                    val output = stx.tx.outputs.single().data
                    "This must be an MarketPlace transaction." using (output is AcceptedState)
                  
                }

            }

            return subFlow(signTransactionFlow)
        }
    }
}
