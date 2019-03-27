const express = require('express');
const bodyParser = require('body-parser')
const ngrok = require('ngrok');

const uport = require('../lib/index.js');
const decodeJWT = require('did-jwt').decodeJWT
const transports = require('uport-transports').transport
const message = require('uport-transports').message.util

const htmlTemplate = (qrImageUri, mobileUrl) => `<div><img src="${qrImageUri}" /></div><div><a href="${mobileUrl}">Click here if on mobile</a></div>`
let endpoint = ''
const messageLogger = (message, title) => {
  const wrapTitle = title ? ` \n ${title} \n ${'-'.repeat(60)}` : ''
  const wrapMessage = `\n ${'-'.repeat(60)} ${wrapTitle} \n`
  console.log(wrapMessage)
  console.log(message)
}

const credentials = new uport.Credentials({
  did: 'did:ethr:0xbc3ae59bc76f894822622cdef7a2018dbe353840',
  privateKey: '74894f8853f90e6e3d6dfdd343eb0eb70cca06e552ed8af80adadcc573b35da3'
})

const app = express();
app.use(bodyParser.json({ type: '*/*' }))

/**
 *  First create a disclosure request, this time request the attestion/credentials already created
 *  by the Creator Service. The credential is identified by it's title/key, 'My Title' was used by
 *  the Creator Service. The callback is specified as the callback path of this server, seen below.
 *  This function will receive the response to this resquest from the uPort Client.
 */
app.get('/', (req, res) => {
  credentials.createDisclosureRequest({
    verified: ['My Title'],
    callbackUrl: endpoint + '/callback',
  }).then(requestToken => {
    const uri = message.paramsToQueryString(message.messageToURI(requestToken), {callback_type: 'post'})
    messageLogger(requestToken, 'Encoded Request URI to send to uPort Client (Signed JWT Wrapped with URI)')
    messageLogger(decodeJWT(requestToken), 'Decoded Request Token')
    const qr =  transports.ui.getImageDataURI(uri)
    res.send(htmlTemplate(qr, uri))
  })
})

/**
 *  This function/path will receive the response from the uPort client to the request created above.
 *  Response is a signed JWT. authenticateDisclosureResponse can be used to verify the signature
 *  of the response payload and the signatures of credentials inclued in the response. You may want
 *  to verify additional parts of the data specific to your use case.
 */
app.post('/callback', (req, res) => {
  const jwt = req.body.access_token
  messageLogger(jwt, 'Response Token (Signed JWT)')
  messageLogger(decodeJWT(jwt), 'Raw Decoded Response Token')
  credentials.authenticateDisclosureResponse(jwt).then(creds => {
    messageLogger(creds, 'uPort Parsed and Verified Response')
    messageLogger(creds.verified[0], 'Credential Requested')
    messageLogger('Credential verified')
  }).catch( err => {
    messageLogger('Verification failed')
  })
})

const server = app.listen(8088, () => {
  ngrok.connect(8088).then(ngrokUrl => {
    endpoint = ngrokUrl
    console.log(`Requestor Service running, open at ${endpoint}`)
  })
})
