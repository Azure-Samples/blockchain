---
urlFragment: azure-blockchain-auth-samples
topic: sample
products:
  - azure
languages:
  - python
  - csharp
  - javascript
---

# Auth Samples and Code Snippets for Workbench

## Overview
### Steps for Workbench Authentication

1. Create a Service Principle

Instruction for Creating a Service Principle on the Azure Portal:

Go to Azure Active Directory --> App Registrations
<br>
<img src="media/auth_samples_1.png" width="400">
<br>

Create a New Application Registration
<br>
<img src="media/auth_samples_2.png" width="250">
<br>
<img src="media/auth_samples_3.png" width="300">
<br>

To get the Client Id: Go to the Application you have just created --> Settings --> Keys
<br>
<img src="media/auth_samples_4.png" width="300">
<br>
Generate a New Key and be sure to save this. This is your Client App Secret.

2. Get the latest version of the ADAL Package (Instructions Provided in Samples)
3. Use the code samples provided as a guideline of how to get a bearer token for your Workbench Application.

#### [Bearer Token Retrieval tool](./bearer-token-retrieval)
A simple tool that allows you to obtain a bearer token for your Workbench instance to be used for development purposes or through Postman.
