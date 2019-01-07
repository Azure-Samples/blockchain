## Frequently Asked Questions

##### Q: I am using a free tier subscription and my Workbench deployments fail due to insufficient VM cores quota, is there anything I can do to work around this?

A: Delete your failed deployment.  Change the VM SKU in step 8 of the "Deploy Blockchain Workbench" section of the [deployment guide](https://docs.microsoft.com/en-us/azure/blockchain/workbench/deploy#deploy-blockchain-workbench) to a "Standard DS1 v2" and ensure you do not have any other virtual machines deployed on your subscription before deploying Workbench again.

##### Q: Workbench performance is very slow for me. How can I scale up/out to improve performance? 

A: When there are many users interacting with Workbench, pages load/refresh slowly. Refer to our [scale up/out guidance](./performance.md). 

##### Q: I'm getting error messages when taking actions such as creating a contract or taking action on a contract. How can I troubleshoot the problem? 

A: We leverage Application Insights to give you detailed troubleshooting information. Refer to our [troubleshooting guide](https://aka.ms/workbenchtroubleshooting) for instructions and more information.
