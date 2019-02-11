---
title: "uPort Credentials"
index: 0
category: "uport-credentials"
type: "landing"
source: "https://github.com/uport-project/uport-credentials/blob/develop/docs/index.md"
---
# uPort Credentials 

## Create & Request Attested Data

uPort is a self-sovereign digital identity platform&mdash;anchored on the Ethereum blockchain. The uPort technology primarily consists of smart contracts, developer libraries, and a mobile app. uPort identities are fully owned and controlled by the creator&mdash;independent of centralized third-parties for creation, control or validation.

Using the uPort Credentials library allows you to:

-   Create and verify authentication requests

-   Request verified claims

-   Verify claims for your users

-   Ask users to sign Ethereum transactions

-   Create Ethereum smartcontract function call requests without web 3.0

uPort provides a simple solution for your users to log in to your app and share private credentials, such as identity information and contact details.

Just like you can ask a user for verified data about themselves, you can also help a user build their identity by [verifying their data](https://github.com/uport-project/specs/blob/develop/flows/verification.md).

The uport-credentials library is primarily used as a server-side library, where keys can be securely stored. For an end-to-end solution, use this library in combination with [uport-transports](https://github.com/uport-project/uport-transports) in the browser to communicate between your application and your user's uPort app.
