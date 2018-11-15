package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.BaseTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import sun.security.x509.X500Name
import java.io.File

/**
 * Takes a simple list of parties names in standard certificate format, e.g.
 *   'O=ContosoLtd,L=Seatle,C=US' and builds a full set of node configs using the
 *   conventions passed in the context.
 */
class ConfigBuilderTask(val ctx: TaskContext, private val parties: List<String>) : BaseTask() {

    override fun exec(executionContext: ExecutionContext) {
        File(ctx.workingDir).mkdirs()
        var basePort = 10000
        for (party in parties) {
            val x500 = X500Name(party)

            val configFileName = "${ctx.workingDir}/${x500.organization.toLowerCase()}_node.conf"
            File(configFileName).writeText(generateNodeConfig(party, basePort, "notary".equals(x500.organization, true)))
            basePort += 10

        }
    }

    fun generateNodeConfig(nodename: String, baseport: Int, isNotary: Boolean = false): String {

        val config = """
            myLegalName="$nodename"
            p2pAddress="corda-local-network:$baseport"
            rpcSettings {
                address="corda-local-network:${baseport + 1}"
                adminAddress="corda-local-network:${baseport + 2}"
            }
            rpcUsers=[
            {
                password=test
                permissions=[
                    ALL
                ]
                user=user1
            }]
            webAddress="corda-local-network:${baseport + 3}"
            sshd {
                port = ${baseport + 4}
            }
        """.trimIndent()

        if (isNotary) {
            return config + "\nnotary {\n" +
                    "    validating=false\n" +
                    "}\n"
        } else {
            return config
        }
    }
}
