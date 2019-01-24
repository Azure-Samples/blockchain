#!/usr/bin/env bash

# A master script for building all components in the correct order.
# Can be used as the base for a CI build process or a quick way
# of building and testing locally


# Setup
script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
testflags="test"
#testflags="-x test"
cleanflags="clean"
echo "Running from $script_dir"
echo $testflags


###  Building shared jars

echo "Building 'commons'"
cd $script_dir/service-bus-integration/commons
./gradlew $cleanflags $testflags jar copyJarToLib &&
if (($?)); then exit -1 ; fi

echo "Building 'corda-reflections'"
cd $script_dir/service-bus-integration/corda-reflections
./gradlew $cleanflags $testflags jar copyJarToLib
if (($?)); then exit -1 ; fi


### Building Corda Apps

echo "Building 'chat' CorDapp"
ls $script_dir/cordapps/chat
cd $script_dir/cordapps/chat
./gradlew $cleanflags $testflags assemble
if (($?)); then exit -1 ; fi

cp cordapp/build/libs/chat-0.1.jar ../jars/chat.jar


echo "Building 'simple-marketplace' CorDapp"
cd $script_dir/cordapps/simple-marketplace
./gradlew $cleanflags $testflags assemble
if (($?)); then exit -1 ; fi
cp cordapp/build/libs/cordapp-simpleMarketplace-0.1.jar ../jars/simple-marketplace.jar


echo "Building 'refrigerated-transportation' CorDapp"
cd $script_dir/cordapps/refrigerated-transportation
./gradlew $cleanflags $testflags assemble
if (($?)); then exit -1 ; fi
cp cordapp/build/libs/cordapp-0.1.jar ../jars/refrigerated-transportation.jar


echo "Building 'basic-provenance' CorDapp"
cd $script_dir/cordapps/basic-provenance
./gradlew $cleanflags $testflags assemble
if (($?)); then exit -1 ; fi
cp cordapp/build/libs/cordapp-example-0.1.jar ../jars/basic-provenance.jar


### Building Services

echo "Building 'corda-local-network' "
cd $script_dir/service-bus-integration/corda-local-network
./gradlew $cleanflags $testflags jar
if (($?)); then exit -1 ; fi
docker build -t corda-local-network  .
if (($?)); then exit -1 ; fi


echo "Building 'corda-transaction-builder' "
cd $script_dir/service-bus-integration/corda-transaction-builder
./gradlew $cleanflags $testflags jar
if (($?)); then exit -1 ; fi
docker build -t corda-transaction-builder  .
if (($?)); then exit -1 ; fi


echo "Building 'service-bus-listener' "
cd $script_dir/service-bus-integration/service-bus-listener
./gradlew $cleanflags $testflags jar
if (($?)); then exit -1 ; fi
docker build -t service-bus-listener  .
if (($?)); then exit -1 ; fi

echo "Success - everything was built"












