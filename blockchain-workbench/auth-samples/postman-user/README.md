# Using Postman with your user credentials

## Overview
You can Postman's OAuth 2.0 authentication mechanism to obtain a bearer token by logging in using your user credentials. This is similar to how Workbench's UI obtains the bearer token, and the token will have the same claims as your user.

In this example we will walk you through how to obtain a bearer toke using your user credential and call the Workbench API.

## Instructions

1. Open Postman. You can download [Postman here](https://www.getpostman.com/downloads/).

In this example we are going to make a `GET` call to `/api/v1/users` to get all the users. Set the operation to `GET` and the url to `<workbench_url>/api/v1/users`

![open postman](./media/initial.png)

2. Navigate to the `Authorization` tab and select `OAuth 2.0` from the dropdown menu. Then click on `Get New Access Token`

![authorization tap](./media/authorization.png)

3. Now you need to set the following parameters:
  * Token Name: The name for your token
  * Grant Type: `Implicit`
  * Callback URL: The URL for Workbench UI
  * Auth URL: `https://login.microsoftonline.com/<tenant_name>/oauth2/authorize?resource=<workbench_appId>`
  * Client id: Workbench's AppId
  * Scope: empty
  * State: A randomly generated GUID, you can use this [Online GUID Generator](https://www.guidgenerator.com/)
  * Client Authentication: `Send client credential in body`
  * Click on `Request Token`

![parameters tap](./media/parameters.png)

4. You should be prompted for your user credential, after completing that step you will receive a bearer token.

![token](./media/token.png)

5. Scroll down and click on `Use Token`

![use-token](./media/use-token.png)

6. Click on send and you should get a list of users

![get call result](./media/call.png)

> Note: This token will expire in an hour, you need to click on `Request Token` token again  to get a new token. Postman will save your parameters and client session so you will not be prompted to login again.