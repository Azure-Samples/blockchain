# Docker 
[Index](Index.md)

## Building a container 

Run the buildDocker script. This builds the app and creates a new container locally

```bash
./buildDocker.sh
```

## Running (simple)

```bash
docker run -d -p 1112:1112  corda-transaction-builder
```

To also expose the agents directly, map the agent ports. _This is really 
for debugging / monitoring, the main API is on port 1112_

```bash
docker run -d -p 1112:1112  -p 10200-10220:10200-10220  corda-transaction-builder
```


