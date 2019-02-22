---
topic: sample
languages:
  - .net
products:
  - azure
  - azure-blockchain
---

# Connect an Azure Ethereum Blockchain to a MySQL Database using the Ethereum Logic App Connector

![Flask sample MIT license badge](https://img.shields.io/badge/license-MIT-green.svg)

This sample shows you how to use the Azure Ethereum Logic App and an Azure function store blockchain events in a MySQL database

## Contents

| File/folder | Description |
|-------------|-------------|
| `src`       | Sample source code. |
| `.gitignore` | Define what to ignore at commit time. |
| `CHANGELOG.md` | List of changes to the sample. |
| `CONTRIBUTING.md` | Guidelines for contributing to the sample. |
| `README.md` | This README file. |
| `LICENSE`   | The license for the sample. |

## Prerequisites

- An Ethereum RPC endpoint 
- An Azure MySQL database

## Setup

1. Clone or download this sample repository
3. Run the following command to install the required Python modules in the context of the sample folder.
    ```bash
    pip install -r .\requirements.txt
    ```
4. Open the sample folder in Visual Studio Code or your IDE of choice.

## Running the sample

1. Make sure the Azure Cosmos DB Emulator is running.
2. Open a terminal window and `cd` to the directory that the app is saved in.
3. Set the environment variable for the Flask app with `set FLASK_APP=app.py` on Windows, or `export FLASK_APP=app.py` if you are using macOS.
4. Run the app with `flask run` and point your browser to `http://127.0.0.1:5000/`.
5. Add and remove tasks and see them added and changed in the collection.

## Deploy to Azure

<a href="https://deploy.azure.com/?repository=https://github.com/heatherbshapiro/To-Do-List---Flask-MongoDB-Example" target="_blank">
<img src="http://azuredeploy.net/deploybutton.png"/>
</a>

To deploy this app, you can create a new web app in Azure and enable continuous deployment with a fork of this GitHub repo. Follow the [App Service continuous deployment tutorial](https://docs.microsoft.com/azure/app-service-web/app-service-continuous-deployment) to set up continuous deployment with GitHub in Azure.

When deploying to Azure, you should remove your application keys and make sure the section below is not commented out:

```python
    client = MongoClient(os.getenv("MONGOURL"))
    db = client.test    #Select the database
    db.authenticate(name=os.getenv("MONGO_USERNAME"),password=os.getenv("MONGO_PASSWORD"))
```

You then need to add your MONGOURL, MONGO_PASSWORD, and MONGO_USERNAME to the application settings. You can follow the [website configuration tutorial](https://docs.microsoft.com/azure/app-service-web/web-sites-configure#application-settings) to learn more about Application Settings in Azure Web Apps.

## Key concepts

Let's take a quick review of what's happening in this example. 

* Contract events that happen on the blockchain are sent to an Azure Event Hub
* An Azure Logic App monitors that Event Hub for notifications of new contract events
* Upon a receipt of a new contract event the Ethereum Logic App Connector queries the blockchain at the address of a contract we are monitoring and pulls the current contract status
* The Ethereum Logic App Connector then forwards the current contract status to a custom Azure Function
    ```JSON
        "body": {
        "PreviousCounterparty": "0x0123...",
        "SupplyChainObserver": "0x4567...",
        "Counterparty": "0x0000...",
        "SupplyChainOwner": "0x89ab...",
        "InitiatingCounterparty": "0xcdef",
        "State": "2"
    }
    ```

* The Azure Function is parsing the contract status JSON and inserts that into a MySQL table
    ```c#
    string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
            dynamic data = JsonConvert.DeserializeObject(requestBody);
    ```

    ```c#
    command.CommandText = @"INSERT INTO contractaction (previouscounterparty, supplychainobserver, counterparty, 
                    supplychainowner, initiatingcounteraparty, state) VALUES (@_previouscounterparty, @_supplychainobserver, 
                    @_counterparty, @_supplychainowner, @_initiatingcounteraparty, @_state);";
    ```

* Retrieve the collection or create it if it does not already exist.

    ```python
    todos = db.todo #Select the collection
    ```

* Create the app

    ```Python
    app = Flask(__name__)
    title = "TODO with Flask"
    heading = "ToDo Reminder"
    ```

## Next steps

You can learn more about our service on the [official documentation site](https://docs.microsoft.com/azure).