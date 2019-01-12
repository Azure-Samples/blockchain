package net.corda.reflections.reflections

import io.github.classgraph.ClassInfo
import net.corda.core.concurrent.CordaFuture
import net.corda.core.contracts.ContractState
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.flows.StateMachineRunId
import net.corda.core.identity.Party
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.DataFeed
import net.corda.core.messaging.FlowProgressHandle
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.PageSpecification
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.Sort
import net.corda.reflections.resolvers.InMemoryPartyResolver
import net.corda.reflections.resolvers.Resolver
import rx.Observable
import rx.functions.Action1
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

/**
 * Runs a Corda flow invoked via reflections
 */
interface RpcCaller {
    fun <T> startTrackedFlowDynamic(logicType: Class<out FlowLogic<T>>, vararg args: Any?): FlowProgressHandle<T>

    fun <T : ContractState> vaultQueryBy(criteria: QueryCriteria = QueryCriteria.VaultQueryCriteria(),
                                         paging: PageSpecification = PageSpecification(),
                                         sorting: Sort = Sort(emptySet()),
                                         contractStateType: Class<out T>): Vault.Page<T>


}

/**
 * Makes a live call using the RPC library
 */
class LiveRpcCaller(val rpc: CordaRPCOps) : RpcCaller {
    override fun <T : ContractState> vaultQueryBy(criteria: QueryCriteria, paging: PageSpecification, sorting: Sort, contractStateType: Class<out T>): Vault.Page<T> {
        val result = rpc.vaultQueryBy<ContractState>(criteria, paging, sorting, contractStateType)
        @Suppress("UNCHECKED_CAST")
        return result as Vault.Page<T>
    }

    override fun <T> startTrackedFlowDynamic(logicType: Class<out FlowLogic<T>>, vararg args: Any?): FlowProgressHandle<T> {
        return rpc.startTrackedFlowDynamic(logicType, *args)
    }
}

/**
 * Calls the flow directly. Just for basic unit test cases
 */
class FakeRpcCaller(val reflections: ReflectionsKt = ReflectionsKt()) : RpcCaller {
    @Suppress("UNCHECKED_CAST")
    override fun <T> startTrackedFlowDynamic(logicType: Class<out FlowLogic<T>>, vararg args: Any?): FlowProgressHandle<T> {

        val listOfArgs = args.toList()
        val flow = reflections.invokeConstructor(logicType.kotlin as KClass<FlowLogic<T>>, listOfArgs)
        val result = flow.call()
        return FakeFlowProgressHandler(result)
    }

    override fun <T : ContractState> vaultQueryBy(criteria: QueryCriteria, paging: PageSpecification, sorting: Sort, contractStateType: Class<out T>): Vault.Page<T> {
        return Vault.Page(states = emptyList(),
                statesMetadata = emptyList(),
                totalStatesAvailable = 0,
                stateTypes = Vault.StateStatus.UNCONSUMED,
                otherResults = emptyList()
        )
    }
}

/**
 * enough for unit tests
 */
class FakeFlowProgressHandler<T>(val value: T) : FlowProgressHandle<T> {
    override val id: StateMachineRunId
        get() = TODO("not implemented")
    override val progress: Observable<String>
        get() = TODO("not implemented")
    override val returnValue: CordaFuture<T>
        get() = FakeFuture<T>(value)
    override val stepsTreeFeed: DataFeed<List<Pair<Int, String>>, List<Pair<Int, String>>>?
        get() = TODO("not implemented")
    override val stepsTreeIndexFeed: DataFeed<Int, Int>?
        get() = TODO("not implemented")

    override fun close() {
        TODO("not implemented")
    }
}

/**
 * enough for unit tests
 */
class FakeFuture<T>(val value: T) : CordaFuture<T> {
    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        return false;
    }

    override fun get(): T {
        return value
    }

    override fun get(timeout: Long, unit: TimeUnit?): T {
        return value
    }

    override fun isCancelled(): Boolean {
        return false
    }

    override fun isDone(): Boolean {
        return true
    }

    override fun <W> then(callback: (CordaFuture<T>) -> W) {
        TODO("not implemented")
    }

    override fun toCompletableFuture(): CompletableFuture<T> {
        TODO("not implemented")
    }
}


class FlowRunner(packageName: String = "net.corda",
                 val partyResolver: Resolver<Party> = InMemoryPartyResolver(),
                 val rpcCaller: RpcCaller = FakeRpcCaller(),
                 val progressHandler: Action1<String>? = null) : BaseRunner() {

    var flowClasses: List<ClassInfo> = scanJars(packageName)


    inline fun <reified T> run(flowName: String, params: Map<String, Any?> = emptyMap()): T? {

        val clazzInfo = flowClasses.first { it.simpleName == flowName }

        @Suppress("UNCHECKED_CAST")
        val kclazz = Class.forName(clazzInfo.name).kotlin as KClass<Any>

        if (!kclazz.hasAnnotation(StartableByRPC::class)) {
            throw RuntimeException("Flow must have 'StartableByRPC' annotation")
        }
        if (!kclazz.hasAnnotation(InitiatingFlow::class)) {
            throw RuntimeException("Flow must have 'InitiatingFlow' annotation")
        }

        @Suppress("UNCHECKED_CAST")
        val args = ReflectionsKt(partyResolver).buildConstructorParams(kclazz, params).toTypedArray()
        @Suppress("UNCHECKED_CAST")
        try {
            val tracker = rpcCaller.startTrackedFlowDynamic(kclazz.java as Class<FlowLogic<T>>, *args)

            if (progressHandler != null) {
                @Suppress("UNCHECKED_CAST")
                tracker.progress.subscribe { progressHandler.call(it) }
            }

            return tracker.returnValue.get(30, TimeUnit.SECONDS);
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
    }


}

