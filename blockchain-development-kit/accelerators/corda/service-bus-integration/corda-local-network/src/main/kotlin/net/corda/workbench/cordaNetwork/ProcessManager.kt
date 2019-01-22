package net.corda.workbench.cordaNetwork

import kotlin.concurrent.thread

/**
 * Keeps track of running processes
 */

object ProcessManager {
    private val sleepTime = 10
    private var monitoring = false
    private val processes = HashMap<Key, Process>()

    fun register(network: String, task: String, process: Process) {
        val key = Key(network, task)
        println("Process $key registered ")
        processes[key] = process
    }

    fun monitor() {
        if (!monitoring) {
            println("Process monitor started")
            monitoring = true
            thread(isDaemon = true) {
                while (monitoring) {
                    println("Process monitor checking ${processes.size} processes...")
                    processes.forEach {
                        println("  process: ${it.key}, isAlive? ${it.value.isAlive}")
                    }
                    println("Process monitor sleeping for $sleepTime...")
                    Thread.sleep(sleepTime * 1000L)
                }
                println("Process monitor stopped")
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

    fun queryForNodeOnNetwork(network: String, processName: String): Process? {
        return processes[Key(network, processName)]
    }

    fun removeProcess(process: Process) {
        // not the nicest code :(
        processes.entries.forEach {
            if (it.value == process) {
                processes.remove(it.key)
                println("Process ${it.key} removed")
                return
            }
        }
    }

    data class Key(val network: String, val processName: String)

    data class ProcessStatus(val name: String, val isAlive: Boolean)

}