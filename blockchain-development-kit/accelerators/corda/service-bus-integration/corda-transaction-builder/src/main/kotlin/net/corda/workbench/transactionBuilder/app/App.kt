package net.corda.workbench.transactionBuilder.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.javalin.translator.json.JavalinJacksonPlugin

import net.corda.reflections.serializers.jackson.Jackson
import net.corda.workbench.commons.event.FileEventStore
import net.corda.workbench.commons.processManager.ProcessManager
import net.corda.workbench.commons.registry.Registry
import net.corda.workbench.transactionBuilder.clients.AgentClientFactoryImpl
import net.corda.workbench.transactionBuilder.clients.LocalNetworkClientImpl
import net.corda.workbench.transactionBuilder.events.Repo
import org.http4k.server.Jetty
import org.http4k.server.asServer


/**
 * Entry point for App
 */
fun main (args : Array<String>){

    val mapper = ObjectMapper()
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
    mapper.registerModule(Jackson.defaultModule())

    //mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
    JavalinJacksonPlugin.configure(mapper)

    val registry = Registry()
    val dataDir = System.getProperty("user.home") + "/.corda-transaction-builder/events"
    val es = FileEventStore().load(dataDir)
    registry.store(es)
    registry.store(Repo(es))
    registry.store(LocalNetworkClientImpl())
    registry.store(AgentClientFactoryImpl(es))
    registry.store(ProcessManager())

    val server =  WebController2(registry).asServer(Jetty(1116)).start()
    println ("$server started!")


    JavalinApp(1112).init(registry)
}