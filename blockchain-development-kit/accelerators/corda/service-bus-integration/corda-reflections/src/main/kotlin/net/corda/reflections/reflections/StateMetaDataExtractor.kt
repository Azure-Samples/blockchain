package net.corda.reflections.reflections

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ClassInfoList


class StateMetaDataExtractor(private val packageName: String = "net.corda") {

    var stateClasses: List<ClassInfo>

    init {
        stateClasses = scanJars()
    }


    fun availableStates(): List<String> {
        val results = ArrayList<String>()
        stateClasses.forEach {
            //            @Suppress("UNCHECKED_CAST")
//            val kclazz = Class.forName(it.name).kotlin as KClass<Any>

            results.add(it.simpleName)
        }

        return results
    }


    private fun scanJars(): List<ClassInfo> {

        val results = ArrayList<ClassInfo>()
        ClassGraph()
                .enableAllInfo()
                .whitelistPackages(packageName)
                .scan()
                .use { scanResult ->
                    scanResult.allClasses
                            .filter { isLinearState(it.interfaces) }
                            .toCollection(results)
                }
        return results

    }

    private fun isLinearState(interfaces: ClassInfoList): Boolean {
        for (clazz in interfaces) {
            if (clazz.name == "net.corda.core.contracts.LinearState") return true
        }
        return false
    }


}

