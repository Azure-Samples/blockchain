package net.corda.workbench.serviceBus

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.javalin.translator.json.JavalinJacksonPlugin



fun main (args : Array<String>){

    val mapper = ObjectMapper()
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
    //mapper.registerModule(Jackson.defaultModule())

    //mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
    JavalinJacksonPlugin.configure(mapper)

    Javalin(1113).init()
}