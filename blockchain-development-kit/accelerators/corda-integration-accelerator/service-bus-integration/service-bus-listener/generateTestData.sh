#!/usr/bin/env bash

./gradlew jar -x test

java -cp build/libs/service-bus-listener.jar net.corda.workbench.serviceBus.TestDataSenderKt