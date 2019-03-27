# What's new in 1.0?

With the release of uPort Credentials `v1.0.0`, there are a number of changes to our API -- the main differences to watch out for are described in this document, and the full API reference can be found [here](https://developer.uport.me/uport-js/reference/index).  The primary changes consist of function name changes, with the hope of improving clarity.  We have also clarified the role of this library as the primary method for *creating* and *verifying* messages in the form of verifiable claims, all of which are described in the [uPort specs repo](https://github.com/uport-project/specs).

### New static method `createIdentity`
With the new identity architecture used in this release, it is now possible to create a uPort identity without any on-chain interactions.  This static method creates a new keypair of a `did` and `privateKey`, which are all that are necessary to create a new identity.  A new `Credentials` object can then be instantiated with a brand new identity as follows:
```javascript
const {did, privateKey} = Credentials.createIdentity()
const credentials = new Credentials({did, privateKey})
```

### `createRequest` -> `createDisclosureRequest`
This is a simple name change to clarify the fact this creates a request as part of a selective disclosure flow.

### New method `createDisclosureResponse`
To better support two-way communication between all types of uPort clients, it is now possible to create a disclosure *response* as well as a request from `uport-credentials`.  This is the response part of the selective disclosure flow, and is equivalent to what gets returned by the mobile app when a disclosure is approved.

### `createVerificationRequest` -> `createVerificationSignatureRequest`
Another name change to clarify that this request asks for a *signature* from a user, on the provided `unsignedClaim`.  

### `receive`, `authenticate` -> `authenticateDisclosureResponse`
The `receive` method has been removed, and the equivalent `authenticate` method has been renamed to reflect that it is verifying the response to a selective disclosure request, *as well as* the fact that the original request came from the verifying identity (i.e. the current `Credentials` instance).  This makes the selective disclosure flow suitable for user authentication.

### New method `verifyDisclosure`
This is a new function to verify a JWT that is not necessarily part of a selective disclosure request (e.g. a JWT that is part of a public profile, or given from a third party).  It differs from `authenticateDisclsoureResponse` in that it does not verify an authentication challenge, so doesn't confirm that the request originated from this identity.  Instead it just verifies the data and signer of the claim, and returns the verified object.

### `attest` -> `createVerification`
We have renamed `attest` to better clarify that the return value of the attestation creation method is a JWT, and that it does no sending of the attestation/verification on its own.  In addition, we have adopted the language `verification` to refer to the most general sense of `attestation`, `claim`, and `credential`, as the language often can get confusing.

### New method `createTxRequest`
This is a request for a user to make an ethereum transaction.  It provides the signature and address of the contract inside a signed JWT, allowing the recipient to verify the identity requesting that they make the transaction, and have the parameters and contract address pre-filled.

### `lookup` -> **removed**
As the primary method for identity creation and management has changed, we no longer need to look up identities in a uport-specific contract.  Instead, `DID`s are resolved with the appropriate `did-resolver`, which handles any lookup/document retrieval necessary for a particular DID. 

### `credentials.push` -> **removed**
Push functionality is now handled by the new [`uport-transports`](https://github.com/uport-project/uport-transports) library.  Additionally, when using `uport-connect`, a `Connect` instance will make requests using push automatically if given permission from a mobile app.