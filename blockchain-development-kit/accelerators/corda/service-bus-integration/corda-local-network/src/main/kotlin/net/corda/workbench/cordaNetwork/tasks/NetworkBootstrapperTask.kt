package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.BaseTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import net.corda.workbench.cordaNetwork.runCommand

/**
 * Runs the network bootstrapper, streaming results
 */
class NetworkBootstrapperTask(val ctx: TaskContext) : BaseTask() {

    override fun exec(executionContext: ExecutionContext) {
        "java -jar src/main/bin/corda-network-bootstrapper-3.2-corda-executable.jar ${ctx.workingDir}".runCommand()
    }
}
