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

## Publishing to DockerHub

This will publish the current image with a specified tag of 'beta'.

```bash
./publishDocker.sh beta
```

This can be run with

```bash
docker run -d -p 1112:1112  ianmorgan/corda-transaction-builder:beta
```
