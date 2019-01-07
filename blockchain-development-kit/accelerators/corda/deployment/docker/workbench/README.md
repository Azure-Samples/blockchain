The most basic docker compose scripts. Runs all containers 
with default and container based storage (so everything is lost when the containers
are removed).

You will need to build the containers manually, see the ./buildDocker.sh script 
for each of the services

```bash
# to start
docker-compose up -d
docker ps 

# to stop
docker-compose down
```

# Logspout 

An easy way of tracking docker logs from multiple containers. See also
https://github.com/gliderlabs/logspout

```bash
# to start 
docker run -d --name="logspout" \
	--volume=/var/run/docker.sock:/var/run/docker.sock \
	--publish=127.0.0.1:8000:80 \
	gliderlabs/logspout
	
# monitor logs
curl http://127.0.0.1:8000/logs
```
