package net.corda.workbench.cordaNetwork.tasks


import net.corda.workbench.commons.taskManager.TaskContext


/**
 * Runs the network bootstrapper, streaming results
 */
class NetworkBootstrapperDownloadTask(val ctx: TaskContext, version: String = "3.2") : AbstractDownloadTask() {

    override val fileName: String = "$downloadCache/corda-network-bootstrapper-$version-corda-executable.jar"
    override val externalUrl: String = "https://azureblockchainworkbench.blob.core.windows.net/artifacts/corda/corda-network-bootstrapper-$version-corda-executable.jar"

}
