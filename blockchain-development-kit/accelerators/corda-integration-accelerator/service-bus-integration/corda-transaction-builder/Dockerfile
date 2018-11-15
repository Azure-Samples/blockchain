FROM openjdk:8u181-jre
LABEL maintainer="ian.j.morgan@gmail.com"

# Port for the agent
EXPOSE 1112


RUN mkdir -p /home/app/src/main/bin
WORKDIR /home/app

# Build a shell script to run java
RUN echo "#!/bin/bash" > /home/app/runIt.sh
RUN echo "java -Xmx256m -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGC -jar /home/app/corda-transaction-builder.jar" >> /home/app/runIt.sh
RUN chmod +x /home/app/runIt.sh

# These are the jars we need
COPY ./build/libs/corda-transaction-builder.jar  /home/app/corda-transaction-builder.jar



ENTRYPOINT ["./runIt.sh"]
