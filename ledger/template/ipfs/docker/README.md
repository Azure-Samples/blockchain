# IPFS on Azure - Docker

This is the source for the customized deployment of IPFS using the experimental private mode.

## Build

```
docker build . -t ipfs
```

## Use

This custom image has a few required parameters to bootstrap the nodes.

| Variable     | Type   | Description                                         | Required |
| ------------ | ------ | --------------------------------------------------- | -------- |
| IPFS_VERSION | string | Build version to use                                | &#x2713; |
| BOOTNODEIP   | string | Internal IP of initial bootnode                     | &#x2713; |
| NODEID       | string | The node id (sequence), used to id the initial node | &#x2713; |

`NOTE: If the deployment is going to Azure via the template in this repository, these values will be added automatically.`
