# Quick Start 
[Index](Index.md)

The steps below outline how to set up an environment and run it locally. They assume a unix based
dev environment. 

For a description a complete transaction see [End to End Processing](EndToEndProcessing.md).

For some worked scenarios see [here](../../scenarios).

For building locally see [here](../../deployment/local/README.md)

For server deployments there is an example [Docker](../../deployment/docker/workbench/README.md) script.
 
## Initial Setup

### Working Gradle Wrapper 

The services will pull down their own dependencies, but you will need a 
1.8 Java JDK in your path.

```bash
cd service-bus-listener
./gradlew --version 

# will return similar to below 

------------------------------------------------------------
Gradle 4.9
------------------------------------------------------------

Build time:   2018-07-16 08:14:03 UTC
Revision:     efcf8c1cf533b03c70f394f270f46a174c738efc

Kotlin DSL:   0.18.4
Kotlin:       1.2.41
Groovy:       2.4.12
Ant:          Apache Ant(TM) version 1.9.11 compiled on March 23 2018
JVM:          1.8.0_181 (Oracle Corporation 25.181-b13)
OS:           Mac OS X 10.12.6 x86_64

```

Later JDK can cause problems with Corda, which is embedded in the services. 
See https://docs.gradle.org/4.9/userguide/gradle_wrapper.html for the more on configuring 
Gradle


### Local DNS 

The services assume known host names. Make sure this is your '/etc/hosts'

```bash 
127.0.0.1 	corda-local-network
127.0.0.1	corda-transaction-builder
127.0.0.1	service-bus-listener
```

### Azure Service Bus

There will need to two queues, one for ingress and egress for replies. This is needed 
for the full end to end integration via the 'service-bus-listener'.

### Docker

Docker is required for the optional Docker deploy steps. A recent stable version should 
be sufficient. A "dev" install of Docker is generally given limited memory and more 
may be requited. Try 4GB initially.  

### Other 

Make sure there is plenty of free memory. The example emulates a full Corda network 
with 8 nodes. 16GB will be fine. 8GB will probably work. The services use a lot of 
ports (the Dockerfile are a good reference for the exact rules), so you may 
experience problems if other dev tools are running. 

## Corda Local Network 

Follow the [Quick Start](../corda-local-network/docs/QuickStart.md). This will get you a 
running local network with the example [Refrigerated Transport](../../cordapps/refrigerated-transportation/README.md)
installed. 

There is no need to change the default configurations.

## Corda Transaction Builder

Follow the [Quick Start](../corda-transaction-builder/docs/QuickStart.md). This will get you 
a working transaction builder with the example app deployed. You can create transactions and 
run simple queries to any node in network using simple REST like commands. 

There is no need to change the default configurations.

## Service Bus Listener 

Update the config with your service bus connection and queue names. For simplicity 
just edit the [reference config](../service-bus-listener/src/main/resources/reference.conf), but
please follow the proper rules documented in the file for server deploys.


Follow the [Quick Start](../service-bus-listener/docs/QuickStart.md). This will get you a working 
service. 

 



