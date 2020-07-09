#!/bin/bash

#############
# Parameters
#############
AZUREUSER=$1
ARTIFACTS_URL_PREFIX=$2
DNS_NAME=$3
BOOT_NODE=$4
SWARM_KEY=$5
MEMBER_ID=$6

#############
# Constants
#############
HOMEDIR="/home/$AZUREUSER"
IPFS_VERSION="0.5.1"

#####################################
# Install Docker engine and compose
#####################################
sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common
curl -fsSL --max-time 10 --retry 3 --retry-delay 3 --retry-max-time 60 https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt-get update
sudo apt-get install -y docker-ce
sudo systemctl enable docker
sleep 5
sudo curl -L --max-time 10 --retry 3 --retry-delay 3 --retry-max-time 60 "https://github.com/docker/compose/releases/download/1.26.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

##########################
# Build image
##########################
docker build . -t ipfs

##########################
# Generate SWARM key
##########################
echo -e "`tr -dc 'a-f0-9' < /dev/urandom | head -c64`" > swarm.key

if [ -z ${SWARM_KEY} ]; then
    SWARM_KEY="$(cat swarm.key)"
fi

if [ -z ${BOOT_NODE} ]; then
    BOOT_NODE=$(ip addr | grep 'state UP' -A2 | sed -n '3p' | awk '{print $2}' | cut -f1  -d'/')
fi

#################################################
# Patch the docker compose file
#################################################
sed -i "s/#IPFS_VERSION/$IPFS_VERSION/" docker-compose.yml || exit 1;
sed -i "s/#BOOTNODE/$BOOT_NODE/" docker-compose.yml || exit 1;
sed -i "s/#NODEID/$MEMBER_ID/" docker-compose.yml || exit 1;
sed -i "s/#SWARMKEY/$SWARM_KEY/" docker-compose.yml || exit 1;

#################################################
# Startup the IPFS node
#################################################
COMPOSE_HTTP_TIMEOUT=200 docker-compose up -d