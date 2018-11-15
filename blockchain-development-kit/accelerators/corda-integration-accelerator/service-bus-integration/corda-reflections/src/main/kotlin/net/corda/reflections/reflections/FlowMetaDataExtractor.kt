package net.corda.reflections.reflections

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ClassInfoList
import kotlin.reflect.KClass




class FlowMetaDataExtractor(val packageName: String = "net.corda") {

    var flowClasses:  List<ClassInfo>
    init {
        flowClasses = scanJars()
    }


    fun primaryConstructorMetaData(flowName: String): Map<String,Any> {

        val clazzInfo = flowClasses.single(){it.simpleName == flowName}

        @Suppress("UNCHECKED_CAST")
        val kclazz = Class.forName(clazzInfo.name).kotlin as KClass<Any>

        val reflectionsKt = ReflectionsKt()

        return reflectionsKt.primaryConstructorMetaData(kclazz)

    }

    fun allConstructorMetaData(flowName: String): List<Map<String,Any>> {

        val clazzInfo = flowClasses.single(){it.simpleName == flowName}

        @Suppress("UNCHECKED_CAST")
        val kclazz = Class.forName(clazzInfo.name).kotlin as KClass<Any>

        val reflectionsKt = ReflectionsKt()

        return reflectionsKt.allConstructorMetaData(kclazz)


    }

    private fun scanJars() : List<ClassInfo>{

        val results = ArrayList<ClassInfo>()
        ClassGraph()
                .enableAllInfo()
                .whitelistPackages(packageName)
                .scan()
                .use { scanResult ->
                    scanResult.allClasses
                            .filter {isFlowLogic(it.superclasses) }
                            .toCollection(results)
                }
        return results

    }

    private fun isFlowLogic(superClasses : ClassInfoList) : Boolean {
        for (clazz in superClasses){
            if (clazz.name == "net.corda.core.flows.FlowLogic") return true
        }
        return false
    }

}

