package net.corda.workbench.transactionBuilder

import com.github.mustachejava.DefaultMustacheFactory
import io.javalin.Context
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.OutputStreamWriter
import java.io.StringReader


fun Context.booleanQueryParam(name: String, default: Boolean = false): Boolean {
    val param = this.queryParam(name)
    if (param != null) {
        if (param.equals("Y", true)) return true
        if (param.equals("true", true)) return true
        return false
    }
    return default

}


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
