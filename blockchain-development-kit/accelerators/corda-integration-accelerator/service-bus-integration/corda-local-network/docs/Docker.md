# Docker 
[Index](Index.md)

## Building a container 

Run the buildDocker script. This builds the app and creates a new container locally

```bash
./buildDocker.sh
```

## Running (simple)

```bash
docker run -d -p 1114:1114 -p 10000-10200:10000-10200 corda-local-network
```

To examine the data files and logs

```bash
docker exec -it <container name> /bin/bash
# will now have a shell inside the container 

cd /root/.corda-local-network
# Will now have a directory for each network

cd default/alice_node
# Assuming a network named 'default' with 'Alice' as a node 

ls coradapps 
# See the apps deployed

cat logs/node-5b39b1cee457.log
# view the logs. n.b the "5b39b1cee457" part is the container name and will 
# on each 'docker run'  
```

You should also be able to connect to a node via SSH. Assuming the simple Alice & Bob network, then Alice would be
at port 10004

```bash
ssh -p 10004 localhost -l user1 
```

As certs are regenerated then ssh may think there is a 'man in middle' attack and abort the login, 
in which case one option is below (_don't try this on prod_) 

```bash
ssh -p 10004 localhost -l user1 -o UserKnownHostsFile=/dev/null 
```

the password is 'test'

a simple command to run is 'flow list'


## Running (advanced)

The following creates a container with 3GB memory (on my Mac this is enough to run the 
refrigeration example with 8 nodes) and uses a local volume for the data. 

Note that by default the non-server install of Docker will have limited memory (it's 2GB on
a Mac) so this may need to be increased beforehand.


```bash
mkdir ~/.docker-corda-local-network

docker run -d -p 1114:1114 -p 10000-10200:10000-10200 -m 3G \
-v ~/.docker-corda-local-network:/root/.corda-local-network corda-local-network
```
