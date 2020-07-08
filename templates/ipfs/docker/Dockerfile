FROM golang:1.11-stretch

LABEL maintainer="cale.teeter@microsoft.com"

ENV IPFS_VERSION ""
ENV BOOTNODEIP ""
ENV NODEID ""
ENV SWARM_KEY ""

COPY config.sh /

RUN chmod +x /config.sh

ENTRYPOINT [ "/bin/sh", "-c", "/config.sh" ] 