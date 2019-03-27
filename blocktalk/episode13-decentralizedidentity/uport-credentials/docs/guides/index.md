---
title: "Getting Started with uPort Credentials"
index: 1
category: "uport-credentials"
type: "guide"
source: "https://github.com/uport-project/uport-credentials/blob/develop/docs/guides/index.md"
---

# Getting Started

## Configure Your Application
 
In your application, you must first configure your uPort object with an identifier and a private key (or signer function). There are several ways to instantiate a credentials object. The most common approach is to save a DID and private key on a server for your application and create a credentials instance from your application's unique private key. Signed JWTs for requests and verifications can then be passed to a client-side application, and presented to a user using a QR code or via another [transport](http://github.com/uport-project/uport-transports).
 
```javascript
import { Credentials } from 'uport-credentials'
 
// For ethereum based addresses (ethr-did)
const credentials = new Credentials({
  appName: 'App Name',
  did: 'did:ethr:0x....',
  privateKey: process.env.PRIVATE_KEY
})
```
## Generate an Ethereum Keypair 
 
At times, you might want identities to be created dynamically. This can be accomplished with the static `Credentials.createIdentity()` method, which generates an Ethereum keypair and returns an object containing the associated DID and private key.
```javascript
// Create a credentials object for a new identity
const {did, privateKey} = Credentials.createIdentity()
const credentials = new Credentials({
  appName: 'App Name', did, privateKey
})
```
 
Finally, we continue to support older uPort identities described by an [MNID](http://github.com/uport-project/mnid)-encoded Ethereum address. These identifiers can be expressed as a DID via the 'uport' DID method: `did:uport:<mnid>`
```javascript
// For legacy application identity created on App Manager
const credentials = new Credentials({
  appName: 'App Name',
  did: 'did:uport:2nQtiQG...',  //append MNID encoded address using did:uport method 
  privateKey: process.env.PRIVATE_KEY
})
```

## Requesting Information From Your Users
 
You can request information from your user, by creating a Selective Disclosure Request JWT. When this is presented to a user via a QR code or another [transport](https://github.com/uport-project/uport-transports), they will be prompted to approve sharing the request attributes. All requests will return a user's `DID`.
 
```javascript
credentials.createDisclosureRequest().then(requestToken => {
  // send requestToken to browser or transport
})
```
 
A selective disclosure request JWT can ask for specific private data, and/or provide a URL to which a mobile app should send a response.
 
```javascript
credentials.createDisclosureRequest().then({
  requested: ['name', 'phone', 'identity_no'],
  callbackUrl: 'https://....' // URL to send the response of the request to
}).then(requestToken => {
  // send requestToken to browser or transport
})
```
 
If you need to know the users address on a specific Ethereum network, specify its `networkId` (currently defaults to Mainnet `0x1`). In this case be aware that the `address` returned will be the address on the public network (currently Mainnet) for the user's profile. The requested network address will be in the `networkAddress` field and will be MNID encoded.
 
```javascript
// Request an address on Rinkeby
credentials.requestDisclosure({networkId: '0x4'}).then(requestToken => {
  // send requestToken to browser or transport
})
```
 
When a response JWT is received, it can be parsed and verified via the `verifyDisclosureResponse()` method, which checks the validity of the signature on the JWT, as well as the validity of the original disclosure request, which is expected as part of the response. 
 
```javascript
credentials.verifyDisclosureResponse(responseToken).then(verifiedData => {
  // Do stuff with verified data or transport
})
```
 
### Stateless Challenge/ Response
 
To ensure that the response received was created as a response to your selective disclosure request above, the original request is included in the response from the mobile app.
 
The verification rule for the Selective Disclosure Response is that the issuer of the embedded request must match the did in your Credentials object and that the original request has not yet expired.  This is to be sure that when requesting data from a user, only a response to your initial request will be accepted as valid.  If you would like to consume an arbitrary signed JWT that is not part of a particular selective disclosure flow, you can use the `verifyDisclosure()` method to skip the challenge/response check.

### Requesting Push Notification Tokens From Your Users
 
As part of the selective disclosure request, you can also ask for permission from your users to communicate directly with their app.  With a push token, you can configure a [transport](https://github.com/uport-project/uport-transports) to send JWTs via push.
 
```javascript
credentials.createRequest({
  requested:[...],
  notifications: true
}).then(requestToken => {
  // send to the browser
})
```
If the user approves the use of push notifications, the selective disclosure response will contain a `pushToken` field, which can be saved when the response is received and verified.
 
```javascript
credentials.verifyDisclosureResponse(responseToken).then(verifiedData => {
  // Store push token securely
  doSomethingWith(verifiedData.pushToken)
})
```
 
## Attesting Information About Your Users
In addition to requesting and verifying information, you can also sign new data on behalf of your application and share it with your users in the form of _attestations_, also known as _verifications_.  By presenting an attestation to a user, you are making a claim about them, and are _attesting_ to its truth with your application's signature.  Exactly what information your app should attest to depends the context -- If you're a financial institution, you may be able to attest to KYC related information such as national identity numbers. If you're an educational institution, you may want to attest to your user's achievements in a way that they can securely share.  Anyone with access to your application's `did` can verify that a particular attestation came from your app.
 
Attesting to information about your users helps to add real value to your application, and your users will use uPort to build up their own digital identity.
 
### Creating an Attestation
 
```javascript
credentials.createVerification({
  sub: '0x...', // uport address of user
  exp: <future timestamp>, // If your information is not permanent make sure to add an expires timestamp
  claims: {name: 'John Smith'}
}).then(attestation => {
  // send attestation to user
})
```
As with a verification request, you will want to send this JWT to your user. You can do this in the browser via QR, using push with a previously requested pushToken, or via another [transport](https://github.com/uport-project/uport-transports) of your choosing.
 
## Asking Users to Sign Ethereum Transactions
 
Finally, as uPort is based in the Ethereum blockchain, uPort Credentials can be used to request that a user call a particular Ethereum smart contract function.  Smart contracts live on the blockchain at a certain address and expose public functions that can be called according to their ABI.  Using the `Credentials.contract` method, you can create an object from a contract abi that will create transaction request JWTs for each contract method, that can be presented to a user's mobile application like any other JWT described above.  This is just a wrapper around `Credentials.createTxRequest()`, which generates the txObject for a particular contract method call.
 
```javascript
import abi from './myContractAbi.json'
const myContract = Credentials.contract(abi).at(contractAddress)
// creates a request for the user to call the transfer() function on the smart contract
const txRequest = myContract.transfer(...).then(txRequestToken => {
  // send tx request token to user
})
```
