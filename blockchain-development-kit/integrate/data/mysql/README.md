---
topic: sample
languages:
  - python
products:
  - azure
  - azure-cosmos-db
---

# Build a Flask app using Azure Cosmos DB for MongoDB API

![Flask sample build badge](https://img.shields.io/badge/build-passing-brightgreen.svg) ![Flask sample code coverage badge](https://img.shields.io/badge/coverage-100%25-brightgreen.svg) ![Flask sample MIT license badge](https://img.shields.io/badge/license-MIT-green.svg)

This content was taken from [this repo](https://github.com/Azure-Samples/CosmosDB-Flask-Mongo-Sample) as a demonstration of how we can update the samples template to provide more context to developers.

This sample shows you how to use the Azure Cosmos DB for MongoDB API to store and access data from a Flask application.

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

- Download the [Azure Cosmos DB Emulator](https://docs.microsoft.com/azure/cosmos-db/local-emulator). The emulator is currently only supported on Windows.
- Install [Visual Studio Code](https://code.visualstudio.com/Download) for your platform.
- Install the Don Jayamanne's [Python Extension](https://marketplace.visualstudio.com/items?itemName=donjayamanne.python)

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

Let's take a quick review of what's happening in the app. Open the `app.py` file under the root directory and you find that these lines of code create the Azure Cosmos DB connection. The following code uses the connection string for the local Azure Cosmos DB Emulator. The password needs to be split up as seen below to accommodate for the forward slashes that cannot be parsed otherwise.

* Initialize the MongoDB client, retrieve the database, and authenticate.

    ```python
    client = MongoClient("mongodb://127.0.0.1:10250/?ssl=true") #host uri
    db = client.test    #Select the database
    db.authenticate(name="localhost",password='C2y6yDjf5' + r'/R' + '+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw' + r'/Jw==')
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