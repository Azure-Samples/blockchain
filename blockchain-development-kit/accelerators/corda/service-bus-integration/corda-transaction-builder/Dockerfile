FROM openjdk:8u181-jre
LABEL maintainer="ian.j.morgan@gmail.com"

# Port for the main app
EXPOSE 1112

# Port for the web UI
EXPOSE 1116


# Enough ports for 20 running agents. Clients shouldn't normally be connecting to the these directly,
# they all proxy through on port 1112, but sometimes it useful to connect directly to the agent externally,
# so we leave the option open
EXPOSE 10200 10201 10202 10203 10204 10205 10206 10207 10208 10209 10210 10211 10212 10213 10214 10215 10216 10217 10218 10219


RUN mkdir -p /home/app/build/libs
RUN mkdir -p /home/app/src/main/resources/www
WORKDIR /home/app


# Build a shell script to run java
RUN echo "#!/bin/bash" > /home/app/runIt.sh
RUN echo "java -Xmx256m -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGC -jar /home/app/corda-transaction-builder.jar" >> /home/app/runIt.sh
RUN chmod +x /home/app/runIt.sh

# These are the jars we need
COPY src/main/resources/www  /home/app/src/main/resources/www
COPY ./build/libs/corda-transaction-builder.jar  /home/app/corda-transaction-builder.jar

# Docker emmulate the same directory structure as gradle so agent can build the same classpath on both deploys
RUN ln /home/app/corda-transaction-builder.jar /home/app/build/libs/corda-transaction-builder.jar


ENTRYPOINT ["./runIt.sh"]
