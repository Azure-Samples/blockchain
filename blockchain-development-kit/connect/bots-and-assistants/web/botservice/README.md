# Basic Bot template
This bot has been created using [Microsoft Bot Framework](https://dev.botframework.com),
- Use [LUIS](https://luis.ai) to implement core AI capabilities
- Implement a multi-turn conversation using Dialogs
- Handle user interruptions for such things as Help or Cancel
- Prompt for and validate requests for information from the user

# Prerequisite to run this bot locally
- Download the bot code from the Build blade in the Azure Portal
- Update the `appsettings.json` file in the root of the bot project with the botFilePath and botFileSecret 
- You can find the botFilePath and botFileSecret in the Azure App Service application settings.

Your appsettings.json file should look like this
```bash
{
    "botFilePath": "<copy value from App settings>",
    "botFileSecret": "<copy value from App settings>"
}
```


## Run in Visual Studio
- Open the .sln file with Visual Studio.
- Press F5.
## Run in Visual Studio Code
- Open the bot project folder with Visual Studio Code.
- Bring up a terminal.
- Type 'dotnet run'.
## Testing the bot using Bot Framework Emulator
[Microsoft Bot Framework Emulator](https://aka.ms/botframework-emulator) is a desktop application that allows bot developers to test and debug
their bots on localhost or running remotely through a tunnel.
- Install the Bot Framework Emulator from [here](https://aka.ms/botframework-emulator).
### Connect to bot using Bot Framework Emulator
- Launch the Bot Framework Emulator
- File -> Open bot and navigate to the bot project folder
- Select `<your-bot-name>.bot` file

# Deploy this bot to Azure
## Publish from Visual Studio
- Open the .PublishSettings file you find in the PostDeployScripts folder
- Copy the userPWD value
- Right click on the Project and click on "Publish..."
- Paste the password you just copied and publish

## Publish using the CLI tools
You can use the [MSBot](https://github.com/microsoft/botbuilder-tools) Bot Builder CLI tool to clone and configure any services this sample depends on. 
To install all Bot Builder tools - 

Ensure you have [Node.js](https://nodejs.org/) version 8.5 or higher

```bash
npm i -g msbot chatdown ludown qnamaker luis-apis botdispatch luisgen
```
To clone this bot, run
```
msbot clone services -f deploymentScripts/msbotClone -n <BOT-NAME> -l <Azure-location> --subscriptionId <Azure-subscription-id>
```
# Further reading
- [Bot Framework Documentation](https://docs.botframework.com)
- [Bot basics](https://docs.microsoft.com/en-us/azure/bot-service/bot-builder-basics?view=azure-bot-service-4.0)
- [Activity processing](https://docs.microsoft.com/en-us/azure/bot-service/bot-builder-concept-activity-processing?view=azure-bot-service-4.0)
- [LUIS](https://luis.ai)
- [Prompt Types](https://docs.microsoft.com/en-us/azure/bot-service/bot-builder-prompts?view=azure-bot-service-4.0&tabs=javascript)
- [Azure Bot Service Introduction](https://docs.microsoft.com/en-us/azure/bot-service/bot-service-overview-introduction?view=azure-bot-service-4.0)
- [Channels and Bot Connector Service](https://docs.microsoft.com/en-us/azure/bot-service/bot-concepts?view=azure-bot-service-4.0)
- [QnA Maker](https://qnamaker.ai)

