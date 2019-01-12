package net.corda.workbench.serviceBus

import com.github.mustachejava.DefaultMustacheFactory
import java.io.*


/**
 * A place for useful extension functions and helper methods.
 */


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

