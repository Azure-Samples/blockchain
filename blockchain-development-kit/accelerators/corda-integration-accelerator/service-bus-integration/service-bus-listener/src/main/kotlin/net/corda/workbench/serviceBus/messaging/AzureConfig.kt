package net.corda.workbench.serviceBus.messaging

import com.typesafe.config.Config

/**
 * AzureConfig details for talking to a azure service bus
 */
data class AzureConfig(val endpoint : String, val ingressQueue : String, val egressQueue : String) {

 constructor(config : Config)  :
       this(endpoint= config.getString("azureServiceBus.endpoint"),
             ingressQueue= config.getString("azureServiceBus.ingressQueue"),
             egressQueue= config.getString("azureServiceBus.egressQueue"))

}