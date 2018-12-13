#!/usr/bin/env bash

#
# Rebuild as quickly as possible and skipping all tests
#

# Make sure we are in the correct folder
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
echo "Running 'buildQuick.sh' in $DIR"
cd $DIR

echo ">>> Building commons..."
cd $DIR/../../service-bus-integration/commons
./gradlew jar copyJarToLib

echo ">>> Building corda-reflections..."
cd $DIR/../../service-bus-integration/corda-reflections
./gradlew jar copyJarToLib

echo ">>> Building 'chat' CorDapp..."
cd $DIR/../../cordapps/chat
./gradlew assemble
cp cordapp/build/libs/cordapp-0.1.jar lib/chat.jar

echo ">>> Building 'refrigerated-transportation' CorDapp..."
cd $DIR/../../cordapps/refrigerated-transportation
./gradlew assemble
cp cordapp/build/libs/cordapp-0.1.jar lib/refrigerated-transportation.jar

echo ">>> Building corda-local-network..."
cd $DIR/../../service-bus-integration/corda-local-network
./gradlew jar

echo ">>> Building corda-transaction-builder..."
cd $DIR/../../service-bus-integration/corda-transaction-builder
./gradlew jar

echo ">>> Building service-bus-listener..."
cd $DIR/../../service-bus-integration/service-bus-listener
./gradlew jar




