package net.corda.reflections.resolvers

import net.corda.core.identity.Party
import net.corda.reflections.app.RPCHelper
import kotlin.reflect.KClass

/**
 * Knows how to take a simple org name and resolve it to a Party.
 */


class RpcPartyResolver(val rpcHelper: RPCHelper) : Resolver<Party> {
    override fun type(): KClass<*> {
        return Party::class
    }

    override fun resolveValue(inputValue: Any): Party? {
        return if (inputValue is String) {
            rpcHelper.lookupParty(inputValue)
        } else {
            null
        }
    }
}


class InMemoryPartyResolver(val parties: List<Party> = emptyList()) : Resolver<Party> {

    override fun resolveValue(inputValue: Any): Party? {
        return if (inputValue is String) {
            parties.first { inputValue.equals(it.name.organisation, true) }
        } else {
            null
        }
    }

    override fun type(): KClass<*> {
        return Party::class
    }
}