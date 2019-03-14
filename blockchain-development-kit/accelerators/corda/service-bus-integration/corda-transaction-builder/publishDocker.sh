#!/usr/bin/env bash


docker tag corda-transaction-builder:latest ianmorgan/corda-transaction-builder:$1
docker push ianmorgan/corda-transaction-builder:$1