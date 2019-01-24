#!/usr/bin/env bash

./gradlew clean jar -x test

docker build -t corda-local-network  .

