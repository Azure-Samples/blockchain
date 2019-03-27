---
title: "Server-side examples"
index: 2
category: "uport-credentials"
type: "tutorial"
source: "https://github.com/uport-project/uport-credentials/blob/develop/docs/guides/tutorial.md"
---

In this toutorial, we will demonstrate how to create and sign a custom credential on a server (called the Creator) and present it to a uPort identity. The user of the uPort app will add this credential to their list of credentials. Later, we'll show you how another service (called the Requestor) can request this credential and validate the corresponding JSON Web Token. This example is available in the [uport-credentials repo](github.com/uport-project/uport-credentials).

To get started, download the repo, run install and build, then find the code for this example in the examples folder:

``` bash
$ git clone https://github.com/uport-project/uport-credentials.git
$ cd uport-credentials
$ npm install
$ npm run build
$ cd examples
```

Here is the quick start, read the remainder of the guide for a more detailed walkthrough and explanation.

Run the Credential Creator Service and open the URL in the terminal console output. This will request your DID (identifier) with a QR code on the browser; once it receives a response, it will issue a credential to that DID and send it through a push notification. The output can be found in terminal console.

``` bash
$ node createcredential.js
```

Once you have the credential in your uPort client, you can use the Requestor Service by running and opening the URL in the terminal console output. It will ask that you share the credential you just received. Upon receiving the credential, it will verify it. The output can be found in terminal console.

``` bash
$ node requestcredential.js
```

## Create an Identity

This tutorial uses sample application identities (e.g., private keys) to issue and verify credentials on a server. For your own applications, you can create a new private key with this library, using the code snippet below:

```
$ node
> const { Credentials } = require('uport-credentials')
> Credentials.createIdentity()
{
  did: 'did:ethr:0x123...',
  privateKey: '3402abe3d...'
}
```

*Please note that in practice the signing key for the identity should remain private!*

## Create Verification Service

In the file `createcredential.js`, we have a simple node `express` server. In the setup phase, we will use the private key, and the DID we created above; there is already an example key pair available in the file, but this should be replaced with your own keypair for any real application. Following along in the code, the first step is to create a `Credentials` object which will encapsulate our newly created keypair.

```js
var credentials = new uport.Credentials({
  did: 'did:ethr:0xbc3ae59bc76f894822622cdef7a2018dbe353840',
  privateKey: '74894f8853f90e6e3d6dfdd343eb0eb70cca06e552ed8af80adadcc573b35da3'
})
```

This `Credentials` object has the ability to create messages containing or requesting verified data.  At the default route, we have a handler set up with `app.get('/')`, which simply calls the `createDisclosureRequest()` method on our new credentials object.  The disclosure request is a JSON web token (JWT), signed by our newly created identity, that requests some specific information from the user.  In particular, we request the ability to send push notifications to the user by including the `notifications: true` key-value pair.  When the request is sent to a user's mobile app, they will be asked to approve the disclosure of the requested attributes, in addition to their Decentralized Identifer, or `did`.

In order to get this request to the user's mobile app, we can use a number of "transports" provided by the `uport-transports` library.  We will not go into the details of all of the available transports in this tutorial, but there are helpers available for using QR codes, push notifications, mobile-specific URLs, custom messaging servers, and others.  In this case, we present the request to the user in the form of a QR code, using `transports.ui.getImageDataURI`, after converting the JWT into a request URI.  The QR code is injected into the  page that gets served to the user.

_**Note:** Some of the details of the transport layer can get complicated, as there are many cases to consider.  This is outside of the scope of this tutorial, which is focused on the practical side of requesting and exchanging credentials. For more details, visit the [repository](https://github.com/uport-project/uport-transports). Additionally, we provide another library, [`uport-connect`](https://github.com/uport-project/uport-connect), which comes with multiple different transports preconfigured._ 

When you scan this code with your mobile app, you should see an alert that you are about to add a credential.  Addtionally, the page contains a clickable link which will open the uPort mobile app from a mobile browser.

Note that in the above disclosure request, we set the `callbackUrl` field to `/callback`.  This is the route to which we should navigate when the response is received from the mobile app.  When we hit that route the `app.post('/callback')` handler will call `credentials.createVerification()` to sign a verification in line `57`.  
```javascript
credentials.createVerification({
  sub: did, // did of the current user
  exp: Time30Days(), // calculate the timestamp for 30 days from now
  claim: {'My Title' : {'KeyOne' : 'ValueOne', 'KeyTwo' : 'Value2', 'Last Key' : 'Last Value'} }
  // Note, the above is a complex claim. Also supported are simple claims:
  // claim: {'Key' : 'Value'}
})
```
The verification requires three fields: `sub`, which identifies the *subject* of the claim; `exp`, which is the unix epoch timestamp (in seconds) at which the claim should no longer be considered valid; and `claim`, which contains the data being signed. The claim can be any (serializable) javascript object (i.e. JSON), so feel free to edit the keys and values of the claim to anything you would like to issue to your users.  Also, notice that we set the `sub` field of the claim to the `did` of the user, which we have just received when they scanned the QR code with the uPort mobile app. 

The `createVerification()` function returns a promise that resolves to a JSON Web Token (JWT). This time, we will make use of another transport from the `uport-transports` library (specifically, `transports.push.send`) to send the JWT as a push notification, without requiring the user to scan another QR code.  This transport requires a public encryption key and push token of the user to which it is being sent, both of which are included in the initial disclosure response.

When the `/callback` page loads, a push notification should appear in the mobile app of the user that has just scanned the QR code, containing the credential that you have edited above!

Once any of your edits are complete, you can test out this flow by starting the server with:
```bash
$ node createcredential.js
```

Open your browser to the URL output in the console; you should see the QR code, which you may scan with the uPort app, initiating the disclosure request, and then the creation and delivery of a new verification. To see the format of the JWTs being passed around, look for output in the terminal console!

## Request Verification Service

The file `requestcredential.js` contains a simple node express server which will request the same credential that the Creator service gave out. The Requestor server will then validate that the identity who is providing the credential is the same identity that received the credential from the Creator service.

As with the Creator service we start by setting up the `Credentials` object using the private key and DID we created above (or using the example provided). We also set up `bodyParser` so that we can parse the JWT that we will get back from the user.

When we load the app at the default route, our handler `app.get('/')` will call `createDisclosureRequest()` much like before, but this time we will request a specific credential from the user.  In a disclsoure request, the  `verified` key to denotes a list of credentials that we are requesting -- as written, this is the `My Title` credential.  If you changed the primary key of the credential that you issued in the `createcredentials.js` service, be sure that you change the name of the key in the `verified` array.

Once again we use the `callbackUrl` field to specify where we should handle the response from the user, assuming they agree to share it.  This must be a publicly available endpoint so that the uPort client can post the response to it. Here as above, the example servers are wrapped with [ngrok]() to create a publicly available endpoint on demand from a server running on localhost.

To interact with this service, run:

```bash
$ node requestcredential.js
```

Then open your browser to the URL output in the console. You should see the QR code with the request, which you may scan with the uPort app. Look for output and responses in the terminal console again.

When the mobile app user approves the request to share their credential after scanning the code, the `/callback` route is called using `app.post('/callback')`. Here, we fetch the response JWT using `req.body.access_token`.

Once we have the JWT, we wish to validate it. We use the `authenticateDisclosureResponse()` function first. This validates the JWT by checking that the signature matches the public key of the issuer. This validation is done both for the overall JWT and for the JWTs that are sent in the larger payload. You may also want to verify additional data in the payload specific to your use case.

If everything checks out, you should see the following output in the console:

```js
Credential verified.
```
Congratulations, you have verified the credential!

To test everything out, try checking for a different attestation and make sure it fails. Also, try waiting until the request expires to make sure that the response fails &mdash; it should throw an error in this case.
