package net.corda.reflections.reflections

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateRef
import net.corda.core.contracts.TransactionState
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.core.serialization.SerializedBytes
import net.corda.core.transactions.CoreTransaction
import net.corda.core.transactions.SignedTransaction

/**
 * Various patterns needed to test reflections
 */


@InitiatingFlow
@StartableByRPC
class SimpleFlow(val data: String) : FlowLogic<String>() {

    @Suspendable
    override fun call(): String {
       return data.toUpperCase()
    }
}

@InitiatingFlow
class NotRpcFlow() : FlowLogic<String>() {

    @Suspendable
    override fun call(): String {
        return "result"
    }
}

@StartableByRPC
class NotInitiatingFlow() : FlowLogic<String>() {

    @Suspendable
    override fun call(): String {
        return "result"
    }
}

@InitiatingFlow
@StartableByRPC
class MultipleConstructorFlow(val p1: String, val p2 : Int) : FlowLogic<String>() {
    constructor(params : TwoParams) : this(params.name,params.age)

    @Suspendable
    override fun call(): String {
        return p1.toUpperCase()
    }
}



class FakeTransaction : CoreTransaction() {
    override val id: SecureHash
        get() = SecureHash.zeroHash
    override val inputs: List<StateRef>
        get() = emptyList()
    override val notary: Party?
        get() = null
    override val outputs: List<TransactionState<ContractState>>
        get() = emptyList()

}