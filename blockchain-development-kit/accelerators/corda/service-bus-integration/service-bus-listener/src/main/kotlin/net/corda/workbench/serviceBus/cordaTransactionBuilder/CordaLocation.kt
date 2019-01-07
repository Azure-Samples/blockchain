package net.corda.workbench.serviceBus.cordaTransactionBuilder

/**
 * The information needed to locate a corda node
 */
data class CordaLocation(val network: String,
                         val cordapp: String,
                         val node: String)