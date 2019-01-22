package net.corda.reflections.reflections

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ClassInfoList

abstract class BaseRunner() {

    protected fun scanJars(packageName: String): List<ClassInfo> {

        val results = ArrayList<ClassInfo>()
        ClassGraph()
                .enableAllInfo()
                .whitelistPackages(packageName)
                .scan()
                .use { scanResult ->
                    scanResult.allClasses
                            .filter { isFlowLogic(it.superclasses) }
                            .toCollection(results)
                }

        return results

    }

    private fun isFlowLogic(superClasses: ClassInfoList): Boolean {
        for (clazz in superClasses) {
            if (clazz.name == "net.corda.core.flows.FlowLogic") return true
            if (clazz.name == "net.corda.workbench.dsl.CordaStateMachine") return true
        }
        return false
    }

}