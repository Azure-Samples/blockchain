package net.corda.reflections.reflections

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ClassInfoList
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.reflections.annotations.Description
import kotlin.reflect.KClass


class FlowMetaDataExtractor(private val packageName: String = "net.corda") {

    var flowClasses: List<ClassInfo>

    init {
        flowClasses = scanJars()
    }


    fun availableFlows(): List<String> {
        val results = ArrayList<String>()
        flowClasses.forEach {
            @Suppress("UNCHECKED_CAST")
            val kclazz = Class.forName(it.name).kotlin as KClass<Any>

            if (isRunnableFlow(kclazz)) {
                results.add(it.simpleName)
            }
        }

        return results
    }

    fun primaryConstructorMetaData(flowName: String): Map<String, Any> {

        val clazzInfo = flowClasses.single() { it.simpleName == flowName }

        @Suppress("UNCHECKED_CAST")
        val kclazz = Class.forName(clazzInfo.name).kotlin as KClass<Any>
        assertIsRunnableFlow(kclazz)

        val reflectionsKt = ReflectionsKt()
        return reflectionsKt.primaryConstructorMetaData(kclazz)

    }

    fun allConstructorMetaData(flowName: String): List<Map<String, Any>> {

        val clazzInfo = flowClasses.single() { it.simpleName == flowName }

        @Suppress("UNCHECKED_CAST")
        val kclazz = Class.forName(clazzInfo.name).kotlin as KClass<Any>
        assertIsRunnableFlow(kclazz)

        val reflectionsKt = ReflectionsKt()
        return reflectionsKt.allConstructorMetaData(kclazz)
    }

    fun flowAnnotations(flowName: String): Map<String, Any> {
        val clazzInfo = flowClasses.single() { it.simpleName == flowName }

        @Suppress("UNCHECKED_CAST")
        val kclazz = Class.forName(clazzInfo.name).kotlin as KClass<Any>
        assertIsRunnableFlow(kclazz)

        val annotations = kclazz.annotations

        val result = HashMap<String,Any>()
        for (annotation in annotations) {
            when(annotation) {
                is Description -> {
                    result["description"] = annotation.text
                }
            }
        }

        return result;
    }


    private fun scanJars(): List<ClassInfo> {

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
        }
        return false
    }

    private fun isRunnableFlow(kclazz: KClass<*>): Boolean {
        return (kclazz.hasAnnotation(StartableByRPC::class) && kclazz.hasAnnotation(InitiatingFlow::class))
    }

    private fun assertIsRunnableFlow(kclazz: KClass<*>) {
        if (!isRunnableFlow(kclazz)) {
            throw RuntimeException("${kclazz.simpleName} must have @StartableByRPC and @InitiatingFlow annotations")
        }
    }

}

