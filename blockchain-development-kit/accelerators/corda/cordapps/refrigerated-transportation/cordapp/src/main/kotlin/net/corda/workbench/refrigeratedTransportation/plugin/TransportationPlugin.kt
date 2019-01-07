package net.corda.workbench.refrigeratedTransportation.plugin

import net.corda.core.messaging.CordaRPCOps
import net.corda.webserver.services.WebServerPluginRegistry
import net.corda.workbench.refrigeratedTransportation.api.TransportationApi
import java.util.function.Function

class TransportationPlugin : WebServerPluginRegistry {
    /**
     * A list of classes that expose web APIs.
     */
    override val webApis: List<java.util.function.Function<CordaRPCOps, out Any>> = listOf(Function(::TransportationApi))
//
//    /**
//     * A list of directories in the resources directory that will be served by Jetty under /web.
//     * The template's web frontend is accessible at /web/template.
//     */
//    override val staticServeDirs: Map<String, String> = mapOf(
//            // This will serve the iouWeb directory in resources to /web/template
//            "iou" to javaClass.classLoader.getResource("iouWeb").toExternalForm()
//    )
}