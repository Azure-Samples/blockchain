#!/usr/bin/env bash


docker tag corda-local-network:latest ianmorgan/corda-local-network:$1
docker push ianmorgan/corda-local-network:$1