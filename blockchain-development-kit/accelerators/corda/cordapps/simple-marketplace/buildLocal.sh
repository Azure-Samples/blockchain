#!/usr/bin/env bash

#
# Build and copy cordapp to the pre-installed examples
#
./gradlew clean test assemble
cp cordapp/build/libs/cordapp-simpleMarketplace-0.1.jar ../jars/simple-marketplace.jar
