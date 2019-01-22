package net.corda.workbench.refrigeratedTransportation.flow

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.CollectSignaturesFlow
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.SignTransactionFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.workbench.refrigeratedTransportation.RefrigerationContract
import net.corda.workbench.refrigeratedTransportation.Shipment

@InitiatingFlow
@StartableByRPC
class CompleteFlow(private val linearId: UniqueIdentifier) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()

        // Retrieve the shipment from the vault.
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(linearId))
        val items = serviceHub.vaultService.queryBy<Shipment>(queryCriteria).states

        if (items.isEmpty()) {
            throw IllegalArgumentException("Cannot find a shipment for $linearId")
        }

        val inputStateAndRef = items.first()
        val shipment = inputStateAndRef.state.data

        if (ourIdentity != shipment.supplyChainOwner) {
            throw IllegalArgumentException("Complete can only initiated by the supplyChainOwner")
        }

        // previous counterparty shouldn't need to know about this
        val actualParticipants = HashSet(shipment.participants)
        if (shipment.previousCounterparty != null && shipment.previousCounterparty != shipment.owner) {
            actualParticipants.remove(shipment.previousCounterparty)
        }

        // build txn
        val cmd = Command(RefrigerationContract.Commands.Complete(),
                actualParticipants.map { it -> it.owningKey })
        val builder = TransactionBuilder(notary = notary)

        builder.addInputState(inputStateAndRef)
                .addOutputState(shipment.completeShipment(), RefrigerationContract.ID)
                .addCommand(cmd)

        // verify
        builder.verify(serviceHub)
        val ptx = serviceHub.signInitialTransaction(builder)

        // make sure everyone signs
        val sessions = (actualParticipants - ourIdentity).map { initiateFlow(it) }.toSet()
        val stx = subFlow(CollectSignaturesFlow(ptx, sessions))

        // complete and notarise
        val finalTx = subFlow(FinalityFlow(stx))

        // let the observer know
        subFlow(ReportToObserverFlow(shipment.supplyChainObserver, finalTx))
        return finalTx
    }
}

/**
 *
 */
@InitiatedBy(CompleteFlow::class)
class CompleteFlowResponder(val flowSession: FlowSession) : FlowLogic<Unit>() {

    @Suspendable
    override fun call() {
        val signedTransactionFlow = object : SignTransactionFlow(flowSession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
                "This must be a shipment transaction" using (output is Shipment)
            }
        }
        subFlow(signedTransactionFlow)
    }
}