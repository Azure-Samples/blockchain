package net.corda.workbench.chat.flow

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.utilities.ProgressTracker

@StartableByRPC
class PingChatAppFlow() : FlowLogic<String>() {

    companion object {
        object RUNNING : ProgressTracker.Step("Running")

        fun tracker() = ProgressTracker(RUNNING)
    }

    override val progressTracker: ProgressTracker = tracker()

    @Suspendable
    override fun call(): String {
        progressTracker.currentStep = RUNNING
        return "pong"
    }
}