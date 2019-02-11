---
title: "Library Reference"
index: 10
category: "uport-credentials"
type: "reference"
source: "https://github.com/uport-project/uport-credentials/blob/develop/docs/reference/index.md"
---

<a name="Credentials"></a>

## Credentials
The Credentials class allows you to easily create the signed payloads used in uPort, including
credentials and signed mobile app requests (e.g., selective disclosure requests
for private data). It also provides signature verification over signed payloads.

**Kind**: global class  

* [Credentials](#Credentials)
    * [new Credentials([settings])](#new_Credentials_new)
    * _instance_
        * [.createDisclosureRequest([params], expiresIn)](#Credentials+createDisclosureRequest) ⇒ <code>Promise.&lt;Object, Error&gt;</code>
        * [.createVerification([credential])](#Credentials+createVerification) ⇒ <code>Promise.&lt;Object, Error&gt;</code>
        * [.createVerificationSignatureRequest(unsignedClaim, [opts])](#Credentials+createVerificationSignatureRequest) ⇒ <code>Promise.&lt;Object, Error&gt;</code>
        * [.createTxRequest(txObj, [opts])](#Credentials+createTxRequest) ⇒ <code>String</code>
        * [.createDisclosureResponse([payload])](#Credentials+createDisclosureResponse) ⇒ <code>Promise.&lt;Object, Error&gt;</code>
        * [.processDisclosurePayload(response)](#Credentials+processDisclosurePayload)
        * [.authenticateDisclosureResponse(token, [callbackUrl])](#Credentials+authenticateDisclosureResponse) ⇒ <code>Promise.&lt;Object, Error&gt;</code>
        * [.verifyDisclosure(token)](#Credentials+verifyDisclosure) ⇒ <code>Promise.&lt;Object, Error&gt;</code>
        * [.contract(abi)](#Credentials+contract) ⇒ <code>Object</code>
    * _static_
        * [.createIdentity()](#Credentials.createIdentity) ⇒ <code>Object</code>

<a name="new_Credentials_new"></a>

### new Credentials([settings])
Instantiates a new uPort Credentials object

The following example is just for testing purposes. *You should never store a private key in the source code.*


| Param | Type | Description |
| --- | --- | --- |
| [settings] | <code>Object</code> | Optional settings |
| [settings.did] | <code>DID</code> | Application [DID](https://w3c-ccg.github.io/did-spec/#decentralized-identifiers-dids) (unique identifier) for your application |
| [settings.privateKey] | <code>String</code> | A hex encoded 32 byte private key |
| [settings.signer] | <code>SimpleSigner</code> | A signer object, see [Signer Functions](https://github.com/uport-project/did-jwt#signer-functions) |
| [settings.ethrConfig] | <code>Object</code> | Configuration object for Ethr DID Resolver. See [ethr-did-resolver](https://github.com/uport-project/ethr-did-resolver) |
| [settings.muportConfig] | <code>Object</code> | Configuration object for muport DID Resolver. See [muport-did-resolver](https://github.com/uport-project/muport-did-resolver) |
| [settings.address] | <code>Address</code> | DEPRECATED your uPort address (may be the address of your application's uPort identity) |
| [settings.networks] | <code>Object</code> | DEPRECATED networks config object, e.g., {  '0x94365e3b': { rpcUrl: 'https://private.chain/rpc', address: '0x0101.... }} |
| [settings.registry] | <code>UportLite</code> | DEPRECATED a registry object from UportLite |

<a name="Credentials+createDisclosureRequest"></a>

### credentials.createDisclosureRequest([params], expiresIn) ⇒ <code>Promise.&lt;Object, Error&gt;</code>
Creates a [Selective Disclosure Request JWT](https://github.com/uport-project/specs/blob/develop/messages/sharereq.md)

**Kind**: instance method of [<code>Credentials</code>](#Credentials)  
**Returns**: <code>Promise.&lt;Object, Error&gt;</code> - a promise which resolves with a signed JSON Web Token or rejects with an error  

| Param | Type | Default | Description |
| --- | --- | --- | --- |
| [params] | <code>Object</code> | <code>{}</code> | Request params object |
| params.requested | <code>Array</code> |  | An array of attributes for which you are requesting credentials to be shared for |
| params.verified | <code>Array</code> |  | An array of attributes for which you are requesting verified credentials to be shared for |
| params.notifications | <code>Boolean</code> |  | Boolean if you want to request the ability to send push notifications |
| params.callbackUrl | <code>String</code> |  | The URL which you want to receive the response of this request |
| params.networkId | <code>String</code> |  | Network ID of Ethereum chain of identity e.g., 0x4 for Rinkeby |
| params.accountType | <code>String</code> |  | Ethereum account type: "general", "segregated", "keypair", "devicekey" or "none" |
| expiresIn | <code>Number</code> |  | Seconds until expiry |

**Example**  
```js
const req = { requested: ['name', 'country'],
               callbackUrl: 'https://myserver.com',
               notifications: true }
 credentials.createDisclosureRequest(req).then(jwt => {
     ...
 })

 
```
<a name="Credentials+createVerification"></a>

### credentials.createVerification([credential]) ⇒ <code>Promise.&lt;Object, Error&gt;</code>
Create a credential (a signed JSON Web Token)

**Kind**: instance method of [<code>Credentials</code>](#Credentials)  
**Returns**: <code>Promise.&lt;Object, Error&gt;</code> - a promise which resolves with a credential (JWT) or rejects with an error  

| Param | Type | Description |
| --- | --- | --- |
| [credential] | <code>Object</code> | An unsigned claim object |
| credential.sub | <code>String</code> | Subject of credential (a valid DID) |
| credential.claim | <code>String</code> | Claim about subject single key value or key mapping to object with multiple values (ie { address: {street: ..., zip: ..., country: ...}}) |
| credential.exp | <code>String</code> | Time at which this claim expires and is no longer valid (seconds since epoch) |

**Example**  
```js
credentials.createVerification({
  sub: '5A8bRWU3F7j3REx3vkJ...', // uPort address of user, likely a MNID
  exp: <future timestamp>,
  claim: { name: 'John Smith' }
 }).then( credential => {
  ...
 })
```
<a name="Credentials+createVerificationSignatureRequest"></a>

### credentials.createVerificationSignatureRequest(unsignedClaim, [opts]) ⇒ <code>Promise.&lt;Object, Error&gt;</code>
Creates a request a for a DID to [sign a verification](https://github.com/uport-project/specs/blob/develop/messages/verificationreq.md)

**Kind**: instance method of [<code>Credentials</code>](#Credentials)  
**Returns**: <code>Promise.&lt;Object, Error&gt;</code> - A promise which resolves with a signed JSON Web Token or rejects with an error  

| Param | Type | Description |
| --- | --- | --- |
| unsignedClaim | <code>Object</code> | Unsigned claim object which you want the user to attest |
| [opts] | <code>Object</code> |  |
| [opts.aud] | <code>String</code> | The DID of the identity you want to sign the attestation |
| [opts.sub] | <code>String</code> | The DID which the unsigned claim is about |
| [opts.riss] | <code>String</code> | The DID of the identity you want to sign the Verified Claim |
| [opts.callbackUrl] | <code>String</code> | The URL to receive the response of this request |

**Example**  
```js
const unsignedClaim = {
   claim: {
     "Citizen of city X": {
       "Allowed to vote": true,
       "Document": "QmZZBBKPS2NWc6PMZbUk9zUHCo1SHKzQPPX4ndfwaYzmPW"
     }
   },
   sub: "2oTvBxSGseWFqhstsEHgmCBi762FbcigK5u"
 }
 const aud = '0x123...'
 const sub = '0x456...'
 const callbackUrl = 'https://my.cool.site/handleTheResponse'
 credentials.createVerificationSignatureRequest(unsignedClaim, {aud, sub, callbackUrl}).then(jwt => {
   // ...
 })
```
<a name="Credentials+createTxRequest"></a>

### credentials.createTxRequest(txObj, [opts]) ⇒ <code>String</code>
Given a transaction object, similarly defined as the web3 transaction object,
 it creates a JWT transaction request and appends additional request options.

**Kind**: instance method of [<code>Credentials</code>](#Credentials)  
**Returns**: <code>String</code> - a transaction request jwt  

| Param | Type | Description |
| --- | --- | --- |
| txObj | <code>Object</code> | A web3 style transaction object |
| [opts] | <code>Object</code> |  |
| [opts.callbackUrl] | <code>String</code> | The URL to receive the response of this request |
| [opts.exp] | <code>String</code> | Time at which this request expires and is no longer valid (seconds since epoch) |
| [opts.networkId] | <code>String</code> | Network ID for which this transaction request is for |
| [opts.label] | <code>String</code> |  |

**Example**  
```js
const txObject = {
   to: '0xc3245e75d3ecd1e81a9bfb6558b6dafe71e9f347',
   value: '0.1',
   fn: "setStatus(string 'hello', bytes32 '0xc3245e75d3ecd1e81a9bfb6558b6dafe71e9f347')",
 }
 connect.createTxRequest(txObject, {callbackUrl: 'http://mycb.domain'}).then(jwt => {
   ...
 })

 
```
<a name="Credentials+createDisclosureResponse"></a>

### credentials.createDisclosureResponse([payload]) ⇒ <code>Promise.&lt;Object, Error&gt;</code>
Creates a [Selective Disclosure Response JWT](https://github.com/uport-project/specs/blob/develop/messages/shareresp.md).

This can either be used to share information about the signing identity or as the response to a
[Selective Disclosure Flow](https://github.com/uport-project/specs/blob/develop/flows/selectivedisclosure.md),
where it can be used to authenticate the identity.

**Kind**: instance method of [<code>Credentials</code>](#Credentials)  
**Returns**: <code>Promise.&lt;Object, Error&gt;</code> - a promise which resolves with a signed JSON Web Token or rejects with an error  

| Param | Type | Default | Description |
| --- | --- | --- | --- |
| [payload] | <code>Object</code> | <code>{}</code> | Request params object |
| payload.req | <code>JWT</code> |  | A selective disclosure Request JWT if this is returned as part of an authentication flow |
| payload.own | <code>Object</code> |  | An object of self-attested claims about the signer (e.g., name etc) |
| payload.verified | <code>Array</code> |  | An array of attestation JWT's to include |
| payload.nad | <code>MNID</code> |  | An Ethereum address encoded as an [MNID](https://github.com/uport-project/mnid) |
| payload.capabilities | <code>Array</code> |  | An array of capability JWT's to include |

**Example**  
```js
credentials.createDisclosureResponse({own: {name: 'Lourdes Valentina Gomez'}}).then(jwt => {
     ...
 })

 
```
<a name="Credentials+processDisclosurePayload"></a>

### credentials.processDisclosurePayload(response)
Parse a selective disclosure response and verify signatures on each signed claim ("verification") included.

**Kind**: instance method of [<code>Credentials</code>](#Credentials)  

| Param | Type | Description |
| --- | --- | --- |
| response | <code>Object</code> | A selective disclosure response payload, with associated did doc |
| response.payload | <code>Object</code> | A selective disclosure response payload, with associated did doc |
| response.doc | <code>Object</code> |  |

<a name="Credentials+authenticateDisclosureResponse"></a>

### credentials.authenticateDisclosureResponse(token, [callbackUrl]) ⇒ <code>Promise.&lt;Object, Error&gt;</code>
Authenticates [Selective Disclosure Response JWT](https://github.com/uport-project/specs/blob/develop/messages/shareresp.md) from uPort
 client as part of the [Selective Disclosure Flow](https://github.com/uport-project/specs/blob/develop/flows/selectivedisclosure.md).

 It Verifies and parses the given response token and verifies the challenge response flow.

**Kind**: instance method of [<code>Credentials</code>](#Credentials)  
**Returns**: <code>Promise.&lt;Object, Error&gt;</code> - a promise which resolves with a parsed response or rejects with an error.  

| Param | Type | Default | Description |
| --- | --- | --- | --- |
| token | <code>String</code> |  | A response token |
| [callbackUrl] | <code>String</code> | <code></code> | callbackUrl |

**Example**  
```js
const resToken = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksifQ.eyJyZXF1Z....'
 credentials.authenticateDisclosureResponse(resToken).then(res => {
     const credentials = res.verified
     const name =  res.name
     ...
 })

 
```
<a name="Credentials+verifyDisclosure"></a>

### credentials.verifyDisclosure(token) ⇒ <code>Promise.&lt;Object, Error&gt;</code>
Verify and return profile from a [Selective Disclosure Response JWT](https://github.com/uport-project/specs/blob/develop/messages/shareresp.md).

The main difference between this and `authenticateDisclosureResponse()` is that it does not verify the challenge.
This can be used to verify user profiles that are shared through other methods such as QR codes and messages.

**Kind**: instance method of [<code>Credentials</code>](#Credentials)  
**Returns**: <code>Promise.&lt;Object, Error&gt;</code> - a promise which resolves with a parsed response or rejects with an error.  

| Param | Type | Description |
| --- | --- | --- |
| token | <code>String</code> | A response token |

**Example**  
```js
const resToken = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksifQ.eyJyZXF1Z....'
 credentials.verifyDisclosure(resToken).then(profile => {
     const credentials = profile.verified
     const name =  profile.name
     ...
 })

 
```
<a name="Credentials+contract"></a>

### credentials.contract(abi) ⇒ <code>Object</code>
Builds and returns a contract object which can be used to interact with
 a given contract. Similar to web3.eth.contract but with promises. Once specifying .at(address)
 you can call the contract functions with this object. Each call will create a request.

**Kind**: instance method of [<code>Credentials</code>](#Credentials)  
**Returns**: <code>Object</code> - contract object  

| Param | Type | Description |
| --- | --- | --- |
| abi | <code>Object</code> | Contract ABI |

<a name="Credentials.createIdentity"></a>

### Credentials.createIdentity() ⇒ <code>Object</code>
Generate a DID and private key, effectively creating a new identity that can sign and verify data

**Kind**: static method of [<code>Credentials</code>](#Credentials)  
**Returns**: <code>Object</code> - keypair
          - {String} keypair.did         An Ethr-DID string for the new identity
          - {String} keypair.privateKey  The identity's private key, as a string  
**Example**  
```js
const {did, privateKey} = Credentials.createIdentity()
const credentials = new Credentials({did, privateKey, ...})
```
