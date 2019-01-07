package net.corda.reflections.reflections

import net.corda.core.identity.Party
import net.corda.reflections.resolvers.InMemoryPartyResolver
import net.corda.reflections.resolvers.Resolver
import net.corda.reflections.resolvers.UniqueIdentifierResolver
import java.lang.reflect.InvocationTargetException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.functions
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.staticFunctions

/**
 * Reflections helper when working with Kotlin KClass.
 */
class ReflectionsKt(val partyResolver: Resolver<Party> = InMemoryPartyResolver()) {
    private val resolvers: List<Resolver<*>>

    init {
        resolvers = ArrayList()
        resolvers.add(partyResolver)
        resolvers.add(UniqueIdentifierResolver())
    }


    /**
     * Create instances via a constructor passing a map of params
     */
    fun <T : Any> invokeConstructor(clazz: KClass<T>, params: Map<String, Any?>): T {

        for (constructor in clazz.constructors) {
            val matched = matchParamsToFunctions(params, constructor)
            if (matched != null) {
                return constructor.callBy(matched)
            }
        }

        throw RuntimeException("Could not find a matching constructor")
    }

    /**
     * Create instance by calling the constructor with list of parameters. In this
     * case there is NO higher level logic for coercing scalars to the necessary type (
     * e.g Int -> Long or resolving instances of classes, e.g. "Alice" to a Party
     */
    fun <T : Any> invokeConstructor(clazz: KClass<T>, paramsResolved: List<Any?>): T {


        for (constructor in clazz.constructors) {
            try {
                // dumb algorithm that just keeps trying.
                return constructor.call(*paramsResolved.toTypedArray())
            } catch (ignored: Exception) {
                ignored.printStackTrace()
            }
        }

        throw RuntimeException("Could not find a matching constructor")
    }

    /**
     * Build a list of constructor params, but don't call the constructor. The list
     * is returned in order expected by the constructor
     */
    fun buildConstructorParams(clazz: KClass<Any>, params: Map<String, Any?>): List<Any?> {
        for (constructor in clazz.constructors) {
            val matched = matchParamsToFunctions(params, constructor)
            if (matched != null) {
                val result = ArrayList<Any?>()
                for (entry in matched.entries) {
                    result.add(entry.value)
                }
                return result
            }
        }
        throw RuntimeException("Could not find a matching constructor")
    }

    fun extractFunctionMetaData(function: KFunction<*>): Map<String, Any> {
        val result = HashMap<String, Any>()
        for (cParam in function.parameters) {
            if (cParam.kind == KParameter.Kind.VALUE) {
                @Suppress("UNCHECKED_CAST")
                val kclazz = cParam.type.classifier as KClass<Any>
                var matched = false

                // try resolvers
                for (r in resolvers) {
                    if (r.type() == kclazz) {
                        result[cParam.name!!] = ParamMetaData(
                                kclazz = r.type(), optional = cParam.isOptional,
                                nullable = cParam.type.isMarkedNullable
                        )
                        matched = true
                        break;
                    }
                }

                // try known scalars
                if (!matched && kclazz.isScalar()) {
                    result[cParam.name!!] = ParamMetaData(
                            kclazz = kclazz, optional = cParam.isOptional,
                            nullable = cParam.type.isMarkedNullable
                    )
                    matched = true
                }

                // try as an object
                if (!matched) {
                    if (kclazz.constructors.size != 1) {
                        throw RuntimeException("$kclazz must have a single public construtor")
                    }
                    result[cParam.name!!] = extractFunctionMetaData(kclazz.constructors.first())
                }
            }
        }

        return result
    }

    fun extractFunctionMetaData(kclazz: KClass<*>, functionName: String): Map<String, Any> {
        val f = kclazz.functions.single() { it.name == functionName }
        return extractFunctionMetaData(f)
    }

    fun primaryConstructorMetaData(kclazz: KClass<*>): Map<String, Any> {
        val f = kclazz.primaryConstructor!!
        return extractFunctionMetaData(f)
    }

    fun allConstructorMetaData(kclazz: KClass<*>): List<Map<String, Any>> {
        val result = ArrayList<Map<String, Any>>()

        for (c in kclazz.constructors) {
            try {
                result.add(extractFunctionMetaData(c))
            } catch (ignored: Exception) {
                // should at least log this
            }
        }
        return result
    }


    /**
     * Call a function
     */
    inline fun <reified T> callFunction(obj: Any, method: String, params: Map<String, Any?> = emptyMap()): T {
        for (function in obj::class.functions.filter { it.name == method }) {
            val matched = matchParamsToFunctions(params, function)

            if (matched != null) {
                matched[function.parameters[0]] = obj
                return function.callBy(matched) as T
            }

        }
        throw RuntimeException("Could not find a matching function")
    }

    /**
     * The core function for matching a map of supplied params to a function,
     * that can then be called via reflections, applying the
     * type coercion rules if necessary.
     */
    @Suppress("UNCHECKED_CAST")
    fun matchParamsToFunctions(params: Map<String, Any?>, function: KFunction<*>): HashMap<KParameter, Any?>? {
        if ((params.size <= function.totalParamCount()) && (params.size >= function.mandatoryParamCount())) {
            val working = HashMap(params)
            val paramMap = LinkedHashMap<KParameter, Any?>()    // must preserve order of params

            for (cParam in function.parameters) {
                if (working.containsKey(cParam.name)) {
                    val inputParam = working[cParam.name]
                    val expectedKClass = cParam.type.classifier as KClass<Any>

                    if (inputParam != null) {

                        val resolved = buildFromResolver(expectedKClass.javaObjectType, inputParam)

                        if (resolved != null) {
                            working.remove(cParam.name)
                            paramMap.put(cParam, resolved)

                        } else if (cParam.type.classifier == inputParam::class) {
                            // types match exactly
                            working.remove(cParam.name)
                            paramMap.put(cParam, inputParam)
                        } else if (inputParam is Map<*, *>) {
                            // is a map, which needs to be converted to an object
                            try {

                                val obj = invokeConstructor(
                                        cParam.type.classifier as KClass<Any>,
                                        inputParam as Map<String, Any?>
                                )
                                working.remove(cParam.name)
                                paramMap.put(cParam, obj)
                            } catch (ignored: RuntimeException) {
                            }
                        } else if (inputParam is List<*>) {
                            val result = ArrayList<Any?>()

                            val listType = cParam.type.arguments[0].type!!

                            for (item in inputParam) {
                                if (listType.isScalar()) {
                                    // todo - should be allowing coercions here
                                    result.add(item)
                                } else {
                                    if (listType.classifier == List::class) {
                                        // hmm - getting a bit complicated
                                        //val lst = matchParamsToFunctions(listType.classifier as KClass<Any>,)
                                    } else {

                                        val obj = invokeConstructor(
                                                listType.classifier as KClass<Any>,
                                                item as Map<String, Any?>
                                        )
                                        result.add(obj)
                                    }
                                }
                            }
                            working.remove(cParam.name)
                            paramMap.put(cParam, result)
                            // is a list, which needs to be converted to a list


                        } else if ((cParam.type.classifier as KClass<Any>).isEnum()) {
                            val enum = cParam.type.classifier as KClass<Any>

                            @Suppress("UPPER_BOUND_VIOLATED", "UNCHECKED_CAST")
                            val value = java.lang.Enum.valueOf<Any>(enum.java, inputParam as String)

                            working.remove(cParam.name)
                            paramMap.put(cParam, value)

                        } else if (inputParam is String) {
                            val result = buildFromString(cParam.type.classifier as KClass<Any>, inputParam)

                            if (result != null) {
                                working.remove(cParam.name)
                                paramMap.put(cParam, result)
                            }

                        } else {
                            // try supported type coercions
                            val coerced = inputParam.coerce(cParam.type.classifier)
                            if (cParam.type.classifier == coerced::class) {
                                working.remove(cParam.name)
                                paramMap[cParam] = coerced
                            }
                        }

                    } else {
                        if (cParam.type.isMarkedNullable) {
                            working.remove(cParam.name)
                            paramMap[cParam] = null
                        }
                    }
                }
            }

            // have found a match for each param
            if (working.isEmpty()) {
                return paramMap
            } else {
                println("unmatched params are: ")
                working.forEach { println("  " + it.key) }
            }
        }

        return null
    }


    fun buildFromString(clazz: KClass<Any>, value: String): Any? {

        // look for a static style factory method, e.g. UUID.fromString()
        try {
            for (function in clazz.staticFunctions) {
                if (function.parameters.size == 1
                        && function.parameters[0].type.classifier == String::class
                        && function.returnType.classifier == clazz
                ) {
                    return function.call(value)
                }
            }

            // look for a constructor taking a single String param
            for (constructor in clazz.constructors) {
                if (constructor.mandatoryParamCount() == 1
                        && constructor.parameters[0].type.classifier == String::class
                ) {
                    return constructor.call(value)
                }
            }
        } catch (ignored: InvocationTargetException) {
        }

        return null

    }

    fun buildFromResolver(clazz: Class<Any>, value: Any): Any? {
        for (r in resolvers) {
            if (r.matchesOutputType(clazz)) {
                try {
                    val result = r.resolveValue(value)
                    if (result != null) return result
                } catch (ignored: Exception) {
                    // todo, should be debug logging
                }
            }
        }
        return null
    }


}


