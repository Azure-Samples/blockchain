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
import net.corda.workbench.refrigeratedTransportation.Transfer

@InitiatingFlow
@StartableByRPC
class TransferResponsibilityFlow(private val linearId: UniqueIdentifier, private val transfer: Transfer) : FlowLogic<SignedTransaction>() {

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


        if (ourIdentity != shipment.initiatingCounterparty) {
            throw IllegalArgumentException("Transfer can only initiated by the initiatingCounterparty")
        }

        if (transfer.newCounterparty == shipment.counterparty) {
            throw IllegalArgumentException("Transfer cannot be to the same counterparty")
        }

        // build txn
        val cmd = Command(RefrigerationContract.Commands.Transfer(),
                shipment.participants.map { it -> it.owningKey })
        val builder = TransactionBuilder(notary = notary)
                .addInputState(inputStateAndRef)
                .addOutputState(shipment.transferResponsibility(transfer.newCounterparty), RefrigerationContract.ID)
                .addCommand(cmd)

        // verify
        builder.verify(serviceHub)
        val ptx = serviceHub.signInitialTransaction(builder)

        // make sure everyone signs
        val sessions = (shipment.participants - ourIdentity).map { initiateFlow(it) }.toSet()
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
@InitiatedBy(TransferResponsibilityFlow::class)
class TransferResponsibilityFlowResponder(val flowSession: FlowSession) : FlowLogic<Unit>() {
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