# BlockTalk - Hyperledger Fabric Example
This session covered the basics of getting started with the Truffle Ganache and how this applies to Dapp development.  In the video, the team from Truffle spoke about how Ganache works, and gave a brief demonstration.

# Prerequisites
To run this demo you will need to have an Ubuntu 16.04LTS machine with the following software installed.  You can create an instance in Azure [here](https://portal.azure.com/#create/Canonical.UbuntuServer1604LTS-ARM)

- Install Docker and Docker Compose
  - Create a script on the server named `installDocker.sh`
  - Copy the following code to that script
    ```
    sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common
    curl -fsSL --max-time 10 --retry 3 --retry-delay 3 --retry-max-time 60 https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
    sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
    sudo apt-get update
    sudo apt-get install -y docker-ce
    sudo systemctl enable docker
    sleep 5
    sudo curl -L --max-time 10 --retry 3 --retry-delay 3 --retry-max-time 60 "https://github.com/docker/compose/releases/download/1.22.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    ```
  - Mark the script as executable `sudo chmod +x installDocker.sh`
  - Run the script `./installDocker.sh`
  - Enable local user to run docker
    ```
    sudo gpasswd -a $USER docker
    newgrp docker
    ```
- Install Go
  - Install build tools 
    ```
    sudo apt-get update 
    sudo apt-get install bison build-essential -y
    ```
  - Install GVM
    ```
    bash < <(curl -s -S -L https://raw.githubusercontent.com/moovweb/gvm/master/binscripts/gvm-installer)
    [[ -s "$HOME/.gvm/scripts/gvm" ]] && source "$HOME/.gvm/scripts/gvm"
    logout
    <log back in>
    gvm install go1.7.3 --binary
    gvm use go1.7.3

- Install nodejs
  - curl -sL https://deb.nodesource.com/setup_8.x | sudo bash -
  - sudo apt-get install nodejs -y

- Install Python
  - sudo apt-get install python

# Install Fabric Samples/Binaries
- curl -sSL http://bit.ly/2ysbOFE | bash -s 1.2.1


# Run the Hyperledger Fabric Network
- Navigate to the samples for the network `cd fabric-samples/first-network`
- Generate the network artifacts `./byfn.sh generate`
- Start the network `./byfn.sh up`