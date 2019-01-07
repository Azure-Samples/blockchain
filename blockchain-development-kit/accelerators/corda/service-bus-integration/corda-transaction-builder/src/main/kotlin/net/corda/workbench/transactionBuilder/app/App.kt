package net.corda.workbench.transactionBuilder.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.javalin.translator.json.JavalinJacksonPlugin

import net.corda.reflections.serializers.jackson.Jackson


/**
 * Entry point for App
 */
fun main (args : Array<String>){

    val mapper = ObjectMapper()
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
    mapper.registerModule(Jackson.defaultModule())

    //mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
    JavalinJacksonPlugin.configure(mapper)

    JavalinApp(1112).init()
}