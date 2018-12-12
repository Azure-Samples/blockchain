#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
echo "Running 'corda-local-network' in $DIR"
cd $DIR

./gradlew jar -x test
java -cp build/libs/service-bus-listener.jar net.corda.workbench.serviceBus.TestDataSenderKt