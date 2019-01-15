package net.corda.workbench.transactionBuilder

import io.github.classgraph.ClassGraph
import net.corda.core.utilities.loggerFor
import org.slf4j.Logger
import java.util.*
import io.github.classgraph.ClassInfo
import kotlin.collections.HashMap

class CordaClassLoader(val customerLoader: ClassLoader? = null) {


    companion object {
        private val logger: Logger = loggerFor<CordaAppLoader>()
    }

    inline fun <reified T> lookupClass(name: String): Class<T> {


        val classGraph = ClassGraph()
                .enableAllInfo()

        if (customerLoader != null) {
            classGraph.addClassLoader(customerLoader)
        }

        val results = ArrayList<ClassInfo>()

        // TODO - should be getting the scannable packages names for the app and not hardcoding
        classGraph
                .whitelistPackages("net.corda")
                .scan()
                .use { scanResult ->
                    scanResult.allClasses
                            .filter { it.simpleName == name }
                            .toCollection(results)
                }
        return Class.forName(results.single().name) as Class<T>
    }


}
