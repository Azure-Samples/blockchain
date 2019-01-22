package net.corda.reflections.workbench

import net.corda.core.identity.CordaX500Name
import net.corda.core.serialization.CordaSerializable

/**
 * Represents the data that is expected in a Workbench Contract Message
 *
 * TODO - should be moved to its own jar - this is workbench specific
 */

@CordaSerializable
data class ContractProperty (val name : String, val value : Any?)

@CordaSerializable
data class TxnResult (val txnHash : String ,
                      val owner : CordaX500Name,
                      val otherParties : List<CordaX500Name> = emptyList(),
                      val contractProperties : List<ContractProperty> = emptyList())


/**
 * As TxnResult but with the list replaced by a @CordaSerializable data class
 * rather than a list of ContractProperty
 */

@CordaSerializable
data class TxnResultTyped<T> (val txnHash : String ,
                      val owner : CordaX500Name,
                      val otherParties : List<CordaX500Name> = emptyList(),
                      val contractProperties : T)