package net.corda.reflections.resolvers

import net.corda.core.contracts.UniqueIdentifier
import java.util.*
import kotlin.reflect.KClass


class UniqueIdentifierResolver() : Resolver<UniqueIdentifier> {

    override fun type(): KClass<*> {
        return UniqueIdentifier::class
    }

    override fun resolveValue(inputValue: Any): UniqueIdentifier? {
        if (inputValue is String) {

            try {
                if (inputValue.length == 36) {
                    return UniqueIdentifier(null, UUID.fromString(inputValue))
                }
                if (inputValue.length >= 38) {
                    val internalId = inputValue.substring(inputValue.length - 36)
                    val externalId = inputValue.substring(0, inputValue.length - 37)
                    return UniqueIdentifier(externalId, UUID.fromString(internalId))
                }
            } catch (ignored: RuntimeException) {
                // should have some debug logging
            }
        }

        return null;
    }

}
