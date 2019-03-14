#!/usr/bin/env bash

#
# Build and copy cordapp to the pre-installed examples
#
./gradlew clean assemble
cp cordapp/build/libs/cordapp-0.1.jar ../jars/refrigerated-transportation.jar
