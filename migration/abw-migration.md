# Migration of Azure Blockchain Workbench from ABS to another ledger

With the deprecation of Azure Blockchain Service, users of the Azure Blockchain Workbench, that have chosen to use Azure Blockchain Service will need to adjust the RPC endpoint configured in ABW to ensure that post migration, the workflow and applications deployed with ABW will continue to function. Depending on the users desire for future ABW work, there are a few options.

## Option 1 - Migration from Azure Blockchain Service (moderate)

Migration from ABS to QBS or another Quorum stack is considered a supported scenario, however it will require some work of the user as the settings to reconfigure Azure Blockchain Workbench are internal to the service. The steps to perform this are:

1. First, request the [export](https://docs.microsoft.com/en-us/azure/blockchain/service/migration-guide#export-data-from-azure-blockchain-service) of the blockchain data from the Azure Blockchain Service instance. Once the export is completed, the new instance of Quorum needs to be created and running.

2. Next, open the Azure portal to the resource group containing the Azure Blockchain Workbench deployed resources.

![Resource group view](media/resource-group.png)

3. Click on the SQL database, there should be only one.

![Sql database](media/sql.png)

4. Click on Query Editor.

![Query database](media/query.png)

5. Enter you DB username / password that was used to deploy the resources initially. `NOTE: You may need to whitelist the Azure IP for access.`

![SQL login](media/sqllogin.png)

6. Run the following command to view the Connections to ledgers. `SELECT * FROM [dbo].[Connection]`

![SQL Query run](media/queryrun.png)

7. Now update the update the connection with the new RPC endpoint. `UPDATE [dbo].[Connection] SET EndPointURL = <your new rpc endpoint> WHERE Id = <id of connection>`

![SQL update run](media/queryupdaterun.png)

8. Now navigate back to the resource group and open the Azure Key Vault resource.

![Azure Key Vault](media/keyvault.png)

9. Add your AAD profile to the Access Policies.

![Key vault access policy](media/accesspolicy.png)

10. Select `SET, GET, LIST` under Secrets for access.

![Key vault secret permissions](media/perms.png)

11. Click Add and then Save.

![Save secret policies](media/save.png)

12. Click on Secrets.

![Secrets](media/secrets.png)

13. Click on `blockchainRpcConnectionString` secret.

![Blockchain connection secret](media/secrets2.png)

14. Click add new version.

![Add new secret version](media/createSecretVersion.png)

15. Paste the new RPC endpoint in the secret and save.

16. Restart the VMSS nodes.

![Restart VMSS](media/restart.png)

## Option 2 - Creating a new Azure Blockchain Workbench instance, connecting to a migrated ABS instance (easy)

Another option is to create a new instance of Azure Blockchain Workbench, connected to the migrated of Quorum either via QBS or manually. The steps to perform this are:

1. First, request the export of the blockchain data from the Azure Blockchain Service instance. Once the export is completed, the new instance of Quorum needs to be created and running.

2. Next, create a new instance of Azure Blockchain Workbench and in the advanced settings use the RPC endpoint of the Quorum node created in step 1.

![Workbench custom rpc endpoint](media/workbench.png)

```
The ability to attach an existing ABW instance to a new blockchain ledger is not supported
```
