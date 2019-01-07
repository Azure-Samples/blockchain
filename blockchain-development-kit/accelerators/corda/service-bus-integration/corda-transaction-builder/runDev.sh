#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
echo "Running 'corda-transaction-builder' in $DIR"

cd $DIR
./gradlew jar -x test
./gradlew run -x test