# Private IPFS networks on Azure

[![Deploy To Azure](https://raw.githubusercontent.com/Azure/azure-quickstart-templates/master/1-CONTRIBUTION-GUIDE/images/deploytoazure.svg?sanitize=true)](https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2FAzure-Samples%2Fblockchain%2Fcaleteet-ipfs-template%2Fledger%2Ftemplate%2Fipfs%2Fcommon%2FazureDeploy.json) [![Visualize](https://raw.githubusercontent.com/Azure/azure-quickstart-templates/master/1-CONTRIBUTION-GUIDE/images/visualizebutton.svg?sanitize=true)](http://armviz.io/#/?load=https%3A%2F%2Fraw.githubusercontent.com%2FAzure-Samples%2Fblockchain%2Fcaleteet-ipfs-template%2Fledger%2Ftemplate%2Fipfs%2Fcommon%2FazureDeploy.json)

This template deploys a single Ubuntu VM using the 18.04LTS image. This will create a new virtual network and subnet and will install and configure the IPFS software.

# Creating the network | Initial node

To create a new IPFS network, first create a single VM that will serve as the bootnode for additional nodes.

`NOTE: The additional nodes do __NOT__ need to be in the same Azure Virtual Network or even in the same Azure Subscription. The template does not setup the network connectivity (peering, vnet, etc.) which rely on standard Azure Networking concepts.`

# Connecting IPFS nodes in a network

The deployment by default will create a new VM running a private network.

If you are new to IPFS, see:

- [IPFS concepts](https://docs.ipfs.io/concepts/)
- [Tutorials](https://docs.ipfs.io/how-to/)
- [HTTP API](https://docs.ipfs.io/reference/http/api/)
- [IPFS CLI](https://docs.ipfs.io/reference/cli/)

If you are new to template deployment, see:

- [Azure Resource Manager documentation](https://docs.microsoft.com/en-us/azure/azure-resource-manager/)
