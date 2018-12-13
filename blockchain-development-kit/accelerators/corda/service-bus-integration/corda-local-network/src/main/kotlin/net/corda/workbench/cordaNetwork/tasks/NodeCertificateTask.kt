package net.corda.workbench.cordaNetwork.tasks

import net.corda.workbench.commons.taskManager.DataTask
import net.corda.workbench.commons.taskManager.ExecutionContext
import net.corda.workbench.commons.taskManager.TaskContext
import java.io.FileInputStream
import java.security.KeyStore
import java.security.PrivateKey
import java.util.Base64

/**
 * A Data task which returns the nodes public cert
 *
 * TODO - need to check i'm returning the correct cert here!!
 */
class NodeCertificateTask(val ctx: TaskContext,
                          private val nodeName: String) : DataTask<String> {

    override fun exec(executionContext: ExecutionContext): String {

        val fis = FileInputStream(ctx.workingDir + "/" + standardiseNodeName(nodeName) + "/certificates/nodekeystore.jks")

        val keystore = KeyStore.getInstance(KeyStore.getDefaultType())
        val password = "cordacadevpass"
        val passwordCharacters = password.toCharArray()
        keystore.load(fis, passwordCharacters)
        val alias = "identity-private-key"
        val key = keystore.getKey(alias, passwordCharacters)

        if (key is PrivateKey) {
            val cert = keystore.getCertificate(alias)
            val publicKey = cert.publicKey
            return String(Base64.getEncoder().encode(publicKey.encoded))
        } else {
            throw RuntimeException("couldn't find a cert for node $nodeName on network ${ctx.networkName}")
        }

    }

    fun standardiseNodeName(name: String): String {
        return if (name.endsWith("_node")) name else name.toLowerCase() + "_node"
    }
}
