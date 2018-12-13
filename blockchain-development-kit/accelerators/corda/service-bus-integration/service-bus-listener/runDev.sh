#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
echo "Running 'corda-local-network' in $DIR"
cd $DIR

./gradlew build run -x test