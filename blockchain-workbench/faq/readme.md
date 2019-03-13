## Frequently Asked Questions

##### Q: I am using a free tier subscription and my Workbench deployments fail due to insufficient VM cores quota, is there anything I can do to work around this?

A: Delete your failed deployment.  Change the VM SKU in step 8 of the "Deploy Blockchain Workbench" section of the [deployment guide](https://docs.microsoft.com/en-us/azure/blockchain/workbench/deploy#deploy-blockchain-workbench) to a "Standard DS1 v2" and ensure you do not have any other virtual machines deployed on your subscription before deploying Workbench again.

##### Q: How to I upgrade my old AAD application to be compatible with Workbench 1.5.0 and later
The AAD application manifest has slightly changed for Workbench 1.5.0 and later, we've provided [upgrade instructions and script](../scripts/aad-upgrade/readme.md) to help you upgrade your old AAD application.

##### Q: I'm trying to access Workbench's API programmatically (using a service principal) but I get errors when calling the API.
If you are getting 204 (NO CONTENT), 401 (UNAUTHORIZED), 403 (FORBIDDEN), or 404 (NOT FOUND) when you access Workbench's API using a service principal it may be due to service principal not having admin privileges or not being assigned to the correct application role to take that action. Please take a look at [Workbench service principal](../scripts/workbench-serviceprincipal/readme.md) for creating and giving a service principal admin privileges. There is also this [great article](https://medium.com/@malirezaie/how-to-enable-programmatic-interaction-with-azure-blockchain-workbench-apis-56c0d95c79c0) that discusses programatic access of Workbench and application role assignment.


##### Q: Workbench performance is very slow for me. How can I scale up/out to improve performance? 

A: When there are many users interacting with Workbench, pages load/refresh slowly. Refer to our [scale up/out guidance](./performance.md). 

##### Q: I'm getting error messages when taking actions such as creating a contract or taking action on a contract. How can I troubleshoot the problem? 

A: We leverage Application Insights to give you detailed troubleshooting information. Refer to our [troubleshooting guide](https://aka.ms/workbenchtroubleshooting) for instructions and more information.

##### Q: I'm having trouble setting up AAD integration with Workbench. What steps do I need to take to do this post deployment? 

A: You can refer to our [AAD setup instructions](../scripts/aad-setup/readme.md) for instructions and more information.

##### Q: I'm having trouble with my Workbench deployment failing due to a password validation failure. What are the password requirements?

A: Your password must meet the following criteria. Please retry your deployment with these in mind.
- Your password must be atleast 8 and no more than 128 characters in length.
- Your password must contain uppercase letters, lowercase letters, and numbers.
- Your password cannot contain all or part of the sql login name. Part of a login name is defined as 3 or more consecutive alphanumeric characters.

