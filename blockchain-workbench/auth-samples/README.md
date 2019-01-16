
# Auth Samples and Code Snippets for Workbench

## Overview
This folder contains samples for accessing Workbench's API in different languages and frameworks.

## Authentication Samples 

#### [Postman using your user account](./postman-user)
Instructions for using Postman's OAuth 2.0 authentication mechanism to obtain a bearer token by logging in using your user credentials. This is similar to how Workbench's UI obtains the bearer token, and the token will have the same claims as your user.

#### [Postman using service principal](./postman-sp)
Instructions for using Postman to call the AAD token service and obtain a bearer token as using a service principal.

#### [C# .NetCore](./netcore)
Instructions and code samples for calling Workbench's API in C# (.NetCore).

#### [Node.js](./nodejs)
Instructions and code samples for calling Workbench's API in JavaScript (Node.js).

#### [Python](./python)
Instructions and code samples for calling Workbench's API in Python.

#### [~~Bearer Token Retrieval tool~~ (Deprecated)](./bearer-token-retrieval)
This tool is deprecated, please use [postman as your user](./postman-user)
