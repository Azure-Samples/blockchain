#!/bin/bash

# Download the binary for ifps
wget -O go-ipfs.tar.gz "https://dist.ipfs.io/go-ipfs/v${IPFS_VERSION}/go-ipfs_v${IPFS_VERSION}_linux-amd64.tar.gz" || exit 1;

# Extract the bits and install
tar xvfz go-ipfs.tar.gz
cd go-ipfs
./install.sh

# clean up
rm -rf go*

# initialize ipfs
ipfs init

# configure API to listen on 0.0.0.0
ipfs config Addresses.API /ip4/0.0.0.0/tcp/5001

# clear out default bootnodes
ipfs bootstrap rm --all

# dump all env vars
echo "IPFS_VERSION: ${IPFS_VERSION}"
echo "BOOTNODE_IP: ${BOOTNODEIP}"
echo "NODE_ID: ${NODEID}"

# create swarm.key file
echo "/key/swarm/psk/1.0.0/" > swarm.key
echo "/base16/" >> swarm.key
echo ${SWARM_KEY} >> swarm.key
cp swarm.key ~/.ipfs/swarm.key

# validate if first node, gen key or if not query first node for configuration
if [ "${NODEID}" == 0 ]; then
    # nothing to do
    echo "Node 1 nothing to do!"
else
    # wait for node0, then patch in configuration
    for LOOPCOUNT in `seq 1 5`; do
        status=`curl -X POST http://"${BOOTNODEIP}":5001/api/v0/id | head -n 1`
        echo "STATUS: ${status}"
        if [[ -z "$status" ]]; then
            echo "Sleeping..."
            sleep 5;
            continue;
        else
            echo "Writing boot node config.."
            IPFS_BOOTNODE_ID=`curl -X POST http://"${BOOTNODEIP}":5001/api/v0/id | awk -F"[,:}]" '{for(i=1;i<=NF;i++){if($i~/\042'ID'\042/){print $(i+1)}}}' | tr -d '"'`
            echo "BOOTNODE_ID: ${IPFS_BOOTNODE_ID}"
            echo "Replicate the key"
            
        fi
    done
    # add local node to boot from
    echo "Adding bootstrap config...."
    echo "/ip4/${BOOTNODEIP}/tcp/4001/ipfs/${IPFS_BOOTNODE_ID}"
    ipfs bootstrap add /ip4/"${BOOTNODEIP}"/tcp/4001/ipfs/"${IPFS_BOOTNODE_ID}"
fi

# force usages of private networks
export LIBP2P_FORCE_PNET=1

# start the ipfs daemon
ipfs daemon