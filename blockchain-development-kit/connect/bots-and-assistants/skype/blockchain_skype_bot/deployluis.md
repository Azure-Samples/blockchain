# Deploy Luis Application

```bash
luis import application --in DeploymentScripts/blockchain.luis --appName blockchain --authoringKey ff5f2c845237409fb367862c957411ad
```

The app Id is in the output of the command

```
{
  "id": "ff828503-a169-4ceb-8ed8-97c78fdd1917",
  "name": "blockchain",
  "description": "Basic bot BotBuilder V4 sample.",
  "culture": "en-us",
  "usageScenario": "",
  "domain": "",
  "versionsCount": 1,
  "createdDateTime": "2018-11-02T17:53:11Z",
  "endpoints": {},
  "endpointHitsCount": 0,
  "activeVersion": "0.1"
}
```

Train the Luis application

```bash
luis train version --appId ff828503-a169-4ceb-8ed8-97c78fdd1917 --versionId 0.1 --authoringKey ff5f2c845237409fb367862c957411ad
```

Publish the luis application to an endpoint

```bash
 luis publish version --appId bdd4de4d-96ee-4cbd-84e2-6cfddbb20196 --versionId 0.1 --authoringKey ff5f2c845237409fb367862c957411ad
```
