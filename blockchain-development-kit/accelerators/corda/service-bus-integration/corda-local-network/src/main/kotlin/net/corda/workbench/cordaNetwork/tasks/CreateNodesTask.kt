package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.event.EventStore
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.commons.taskManager.BaseTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.cordaNetwork.events.Repo
import net.corda.workbench.transactionBuilder.events.EventFactory
import sun.security.x509.X500Name
import java.io.IOException
import java.lang.RuntimeException

/**
 * Combine all the actions for creating nodes for a network
 */
class CreateNodesTask(private val registry: Registry,
                      private val parties: List<String>,
                      private val options: Options = Options()) : BaseTask() {
    private val ctx = registry.retrieve(TaskContext::class.java)!!
    private val es = registry.retrieve(EventStore::class.java)!!


    override fun exec(executionContext: ExecutionContext) {

        // rather crude test for existing nodes so we don't allocate the same ports
        // twice
        val repo = Repo(es)
        var nodeCount = 0
        for (network in repo.networks()) {
            try {
                nodeCount += repo.nodes(network.name).size
            } catch (ignored: Exception) {
            }
        }
        if (nodeCount + parties.size > 50) {
            throw RuntimeException("The limit of no more than 50 nodes across all network has been exceeded")
        }

        // allow for partial names etc
        val standardisedParties = parties.map { standardise(it) }

        ConfigBuilderTask(registry, standardisedParties, 10000 + nodeCount * 4)
                .exec(executionContext)
        NetworkBootstrapperTask(ctx, options.cordaVersion)
                .exec(executionContext)

        es.storeEvent(EventFactory.NODES_CREATED(ctx.networkName, standardisedParties))

    }

    private fun standardise(party: String): String {
        try {
            X500Name(party)
            return party
        } catch (ioex: IOException) {
            val standardised = "O=${party!!},L=London,C=GB"
            X500Name(standardised)
            return standardised
        }
    }


    data class Options(val cordaVersion: String = "3.2")


}