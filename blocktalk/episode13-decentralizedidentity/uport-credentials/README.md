[![npm](https://img.shields.io/npm/dt/ethr-did.svg)](https://www.npmjs.com/package/uport-credentials)
[![npm](https://img.shields.io/npm/v/ethr-did.svg)](https://www.npmjs.com/package/uport-credentials)
[![Join the chat at](https://img.shields.io/badge/Riot-Join%20chat-green.svg)](https://chat.uport.me/#/login)
[![Twitter Follow](https://img.shields.io/twitter/follow/uport_me.svg?style=social&label=Follow)](https://twitter.com/uport_me)

[DID Specification](https://w3c-ccg.github.io/did-spec/) | [Getting Started](/docs/guides/index.md)

# uPort Credentials Library

**Required Upgrade to uport-credentials@1.0.0 or uport@^0.6.3**

**^0.6.3 (uport) to support new both new uPort Mobile Clients and legacy uPort Mobile Clients - [View Details](https://github.com/uport-project/uport-js/releases/tag/v0.6.3)**

**v1.0.0 (uport-credentials) to support only new uPort Mobile Clients and to use new features and fixes. In the future only v1.0.0 onwards will be supported.**

:bangbang: :warning: **v1.0.0** is released at the npm next tag at **uport-credentials@next**. While **^0.6.3** remains at **uport** on npm.  Only the newest uPort Mobile Client release will work with **v1.0.0**. It will become the default release once the newest uPort Mobile Client release is widely adopted (~ 2 weeks). Reference master branch for docs and info on current default release **^0.6.3**. Documentation for **v1.0.0** can only be found here and in the docs folder. The [developer site](https://developer.uport.me) will not contain **v1.0.0** documentation until it is the default release :warning: :bangbang:


## Integrate uPort Into Your Application 

uPort provides a set of tools for creating and managing identities that conform to the decentralized identifier (DID) specification, and for requesting and exchanging verified data between identities. 

uPort Credentials simplifies the process of identity creation within JavaScript applications; additionally, it allows applications to easily sign and verify data — signed by other identities to facilitate secure communication between parties. These pieces of data take the form of signed JSON Web Tokens (JWTs), they have specific fields designed for use with uPort clients, described in the uPort specifications, collectively referred to as verifications.
 
To allow for maximum flexibility, uPort Credential’s only deals with creation and validation of verifications. To pass verifications between a JavaScript application and a user via the uPort mobile app, we have developed the [uPort Transports library](https://github.com/uport-project/uport-transports), use it in conjunction with uPort Credentials when necessary.

To hit the ground running with uPort Credentials, visit the [Getting Started guide](/docs/guides/index.md). 

For details on uPort's underlying architecture, read our [spec repo](https://github.com/uport-project/specs) or check out the [uPort identity contracts](https://github.com/uport-project/uport-identity).

This library is part of a suite of tools maintained by the uPort Project, a ConsenSys formation.  For more information on the project, visit [uport.me](https://uport.me)
