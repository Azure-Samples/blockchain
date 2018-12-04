FROM openjdk:8u181-jre
LABEL maintainer="ian.j.morgan@gmail.com"

# Port for the agent
EXPOSE 1113


RUN mkdir -p /home/app/src/main/bin
RUN mkdir -p /home/app
WORKDIR /home/app

# Build a shell script to run java
RUN echo "#!/bin/bash" > /home/app/runIt.sh
RUN echo "ls -ltr" >> /home/app/runIt.sh
RUN echo "ls -ltr config" >> /home/app/runIt.sh
RUN echo "java -cp config:service-bus-listener.jar -Xmx256m -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGC net.corda.workbench.serviceBus.AppKt" >> /home/app/runIt.sh
RUN chmod +x /home/app/runIt.sh

# These are the jars we need
COPY ./build/libs/service-bus-listener.jar  /home/app/service-bus-listener.jar


ENTRYPOINT ["./runIt.sh"]
