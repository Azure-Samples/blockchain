# Slow Read Performance from Workbench API

Symptoms
=================
When there are many users interacting with Workbench, pages load/refresh slowly.  Interactions with the system take longer than normal to complete.  This does NOT cover contract creation and contract actions taking a long time to complete.

Mitigation via scale up/out
=================
If you observe increased response time from Workbench's API, increasing the number of App Service instances and scaling up the Azure SQL DB can increase performance and bring the response times back down.  This guide helps you evaluate scale settings based on your expected user load.  The guidance is based on performance measurement of Workbench under various loads.  The measurement is described below so you can adjust the guidance based on your actual user workload.

The following upper limits were used to decide when the Workbench deployment should be scaled up/out.
* P50 latency measured over 5 minutes of sustained load is at most 5 seconds.
* P90 latency measured over 5 minutes of sustained load is at most 7.5 seconds.

For the purpose of performance analysis, only idle user load was applied as this is the minimum load a typical user would apply on the system.  User actions like creating contracts or taking action were not included in this analysis as these vary by use case and it expected to generally be much lower in volume compared to polling for updates that the website does automatically.  The only pages that poll the API for updates are the "Contract list" and "Contract details" pages.  It is assumed that each user is only observing and will keep two browser tabs/windows open, one showing the list of contracts (contract instances) of an Application and one showing details of a contract.  The Workbench website polls the API to check for updates every 5 seconds.   System load measured is measured in RPS (requests per second) and each idle user generates a load of 0.4 RPS.  The table below details the two API calls that were made to measure latency and throughput as well as the number of objects loaded into Workbench at that level:

| *Workbench website page and API endpoint*                                                                  | *Refresh interval* | *Open pages* | *# Objects*              |
|----------------------------------------------------------------------------------------------------------|------------------|------------|------------------------|
| Page: Contracts list Endpoint: /api/v1/contracts?workflowId=<Workflow-Id>&sortBy=Timestamp&top=50&skip=0 | 5 sec            | 1          | 442 contract instances |
| Page: Contract detail Endpoint: /api/v1/contracts/<Contract-Id                                           | 5 sec            | 1          | 19 contract actions    | 

Using the above assumptions, a mapping between number of concurrent users and load (req/sec) on the system is:

| *# Concurrent users* | *Effective load (requests/second)* |
|--------------------|----------------------------------|
| 10                 | 4                                |
| 20                 | 8                                |
| 30                 | 12                               |
| ...                | ...                              |

Scale up/out recommendation
=================
The table below shows recommended scale up and out settings based on expected load on Workbench.  The number of users is based on an assumed workload described above (one contracts and one contract details pages polling every 5 seconds for each user).  Please use the RPS number instead if your per user workload is different or you are optimizing for programmatic API access.  This only covers read operations (GET).

| *# Users*   | *RPS*     | *AppService Instances* | *Azure SQL DTUs* |
|-----------|---------|----------------------|----------------|
| 0 - 30    | 0 - 12  | 1                    | 10             |
| 30 - 50   | 12 - 20 | 2                    | 10             |
| 50 - 65   | 20 - 26 | 3                    | 20             |
| 65 - 100  | 26 - 40 | 4                    | 20             |
| 100 - 120 | 40 - 48 | 5                    | 20             |
| > 120     | > 48    | --                   | --             |

Scale up/out via Azure Portal
=================
You can increase the number of App Services instances (scale out) as follows:
1. In the Azure portal, navigate to the resource group where you deployed Workbench into.
2. Select the API service as shown below:
3. Navigate to the "Scale out (App Service plan)" section and change the instance count as shown below.
4. Note that you can change this setting by modifying either of the App Services or the App Service plan itself as this setting.
5. Enable autoscale is another option if you expect load to go change slowly.  Note that Azure SQL DB does not autoscale and must be scaled up based on your expected peak load.

You can scale up the Azure SQL DB instance as follows:
1. In the Azure portal, navigate to the resource group where you deployed Workbench into.
2. Select the SQL DB as shown below:
3. Navigate to the "Configure" section and change the "DTUs" as shown below:
