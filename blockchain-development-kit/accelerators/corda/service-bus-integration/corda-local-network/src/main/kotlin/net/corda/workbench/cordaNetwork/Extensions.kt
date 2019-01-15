package net.corda.workbench.transactionBuilder

import com.github.mustachejava.DefaultMustacheFactory
import java.io.*
import org.apache.commons.codec.binary.Hex
import java.security.MessageDigest
import org.eclipse.jetty.util.security.Credential.MD5.digest
import java.math.BigInteger


fun readFileAsText(path: String, substitutions : Map<String,Any?> = emptyMap()): String {
    val raw =  FileInputStream(path).bufferedReader().use { it.readText() }

    if (substitutions.isNotEmpty()){
        val mf = DefaultMustacheFactory()
        val mustache = mf.compile(StringReader(raw),"template.mustache")

        val bos = ByteArrayOutputStream()
        val writer = OutputStreamWriter(bos)
        mustache.execute(writer, substitutions).flush()

        return bos.toString()
    }
    else {
        return raw;
    }
}


fun File.copyInputStreamToFile(inputStream: InputStream) {
    inputStream.use { input ->
        this.outputStream().use { fileOut ->
            input.copyTo(fileOut)
        }
    }
}

fun File.md5Hash() : String {
    val messageDigest = MessageDigest.getInstance("MD5")
    messageDigest.reset()
    messageDigest.update(this.readBytes())
    //val resultByte = messageDigest.digest()
    //return String(Hex.encodeHex(resultByte))

    return String.format("%032x", BigInteger(1, messageDigest.digest()))

}