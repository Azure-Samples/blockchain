# AAD Bearer Token Retrieval

## Overview
This is a simple tool that allows you to obtain a bearer token for your Workbench instance to be used for development purposes or through Postman.

> Note: This token is short lived and **should not** be hard-coded in source code.


## Prerequisite
* [NodeJS](https://nodejs.org/)
* Azure Active Directory App Registration (You will need the App Id)
* Please add `http://localhost:3000` to the reply urls of your AAD app

## Execution Instructions

Navigate to this directory and run the following commands:

```bash
npm install
npm start
```

This should open the following browser tab on [http://localhost:3000](http://localhost:3000)

![Fresh login page](media/fresh-page.png)


Type in your AAD AppId and the AAD domain name that you wold like to use. Leave the domain name blank if you want to use your native tenant (`common`)

![Page with input](media/inputs.png)

Upon logging in you will see the bearer token generated for your AppId (The audience of this token is your appId)

![page with token](media/token.png)

> Note: This application will remember the input values in the browsers `localStorage` for your convenience. You can clear them by pressing the `Reset` button.
