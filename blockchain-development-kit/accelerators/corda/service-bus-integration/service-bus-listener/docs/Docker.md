# Docker 
[Index](Index.md)

## Building a container 

Run the buildDocker script. This builds the app and creates a new container locally

```bash
./buildDocker.sh
```

## Running (simple)

```bash
docker run -d -p 1113:1113  service-bus-listener
```

## Running (advanced)

The following runs with a local volume for the `application.conf` file, 
which is one way of supporting custom configurations.

```bash
# make a copy of the default config
mkdir ~/.config
cp src/main/resources/application.conf ~/.config/application.conf

# edit config

#run docker injecting in the custom config 
docker run -d -p 1113:1113 -v ~/.config:/home/app/config service-bus-listener
```


