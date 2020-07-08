FROM golang:1.11-stretch

ENV IPFS_VERSION ""
ENV BOOTNODEIP ""
ENV NODEID ""
ENV SWARM_KEY ""

COPY dockerconfig.sh /

RUN chmod +x /dockerconfig.sh

ENTRYPOINT [ "/bin/sh", "-c", "/dockerconfig.sh" ] 