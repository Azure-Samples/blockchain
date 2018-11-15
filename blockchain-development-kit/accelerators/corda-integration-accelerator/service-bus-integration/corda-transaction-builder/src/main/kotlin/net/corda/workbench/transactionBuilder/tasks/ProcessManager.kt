package net.corda.workbench.transactionBuilder.tasks

import net.corda.core.utilities.loggerFor
import org.slf4j.Logger
import kotlin.concurrent.thread


/**
 * Keeps track of running processes
 */

object ProcessManager {

    private val logger: Logger = loggerFor<ProcessManager>()

    private val sleepTime = 10
    private var monitoring = false
    private val processes = HashMap<Key, Process>()


    fun register(network: String, task: String, process: Process) {
        processes.put(Key(network, task), process)
    }

    fun monitor() {
        if (!monitoring) {
            logger.info("Process monitor started")
            monitoring = true
            thread(isDaemon = true) {
                while (monitoring) {
                    logger.info("Process monitor checking ${processes.size} processes...")
                    processes.forEach {
                        logger.info("  process: ${it.key.processName}, isAlive? ${it.value.isAlive}")
                    }
                    logger.debug("Process monitor sleeping for $sleepTime...")
                    Thread.sleep(sleepTime * 1000L)
                }
                logger.info("Process monitor stopped")
            }
        }
    }

    fun all(): List<Process> {
        return ArrayList(processes.values)
    }

    fun queryForNetwork(network: String): List<ProcessStatus> {
        val results = ArrayList<ProcessStatus>()
        processes.entries.forEach {
            if (it.key.network == network) {
                results.add(ProcessStatus(it.key.processName, it.value.isAlive))
            }
        }
        return results
    }

    fun queryForProcessOnNetwork(network: String, processName : String): Process?  {
       return processes.get(Key(network,processName))
    }


    data class Key(val network: String, val processName: String)

    data class ProcessStatus(val name: String, val isAlive: Boolean)


}