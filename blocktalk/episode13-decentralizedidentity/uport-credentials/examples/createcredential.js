const express = require('express')
const ngrok = require('ngrok')
const bodyParser = require('body-parser')

const uport = require('../lib/index.js')
const decodeJWT = require('did-jwt').decodeJWT
const transports = require('uport-transports').transport
const message = require('uport-transports').message.util

const htmlTemplate = (qrImageUri, mobileUrl) => `<div><img src="${qrImageUri}" /></div><div><a href="${mobileUrl}">Click here if on mobile</a></div>`
const Time30Days = () => Math.floor(new Date().getTime() / 1000) + 30 * 24 * 60 * 60
let endpoint = ''
const messageLogger = (message, title) => {
  const wrapTitle = title ? ` \n ${title} \n ${'-'.repeat(60)}` : ''
  const wrapMessage = `\n ${'-'.repeat(60)} ${wrapTitle} \n`
  console.log(wrapMessage)
  console.log(message)
}

const app = express();
app.use(bodyParser.json({ type: '*/*' }))

const credentials = new uport.Credentials({
  did: 'did:ethr:0xbc3ae59bc76f894822622cdef7a2018dbe353840',
  privateKey: '74894f8853f90e6e3d6dfdd343eb0eb70cca06e552ed8af80adadcc573b35da3'
})

/**
 *  First creates a disclosure request to get the DID (id) of a user. Also request push notification permission so
 *  a push can be sent as soon as a response from this request is received. The DID is used to create the attestation
 *  below. And a pushToken is used to push that attestation to a user.
 */
app.get('/', (req, res) => {
  credentials.createDisclosureRequest({
    notifications: true,
    callbackUrl: endpoint + '/callback'
  }).then(requestToken => {
    const uri = message.paramsToQueryString(message.messageToURI(requestToken), {callback_type: 'post'})
    const qr =  transports.ui.getImageDataURI(uri)
    res.send(htmlTemplate(qr, uri))
  })
})

/**
 *  This function is called as the callback from the request above. We the get the DID here and use it to create
 *  an attestation. We also use the push token and public encryption key share in the respone to create a push
 *  transport so that we send the attestion to the user.
 */
app.post('/callback', (req, res) => {
  const jwt = req.body.access_token
  credentials.authenticateDisclosureResponse(jwt).then(creds => {
    const did = creds.did
    const pushToken = creds.pushToken
    const pubEncKey = creds.boxPub
    const push = transports.push.send(creds.pushToken, pubEncKey)
    credentials.createVerification({
      sub: did,
      exp: Time30Days(),
      claim: {'My Title' : {'Position' : 'Engineer', 'Group' : 'Blockchain Engineering', 'YearsInRole' : '3'} }
      // Note, the above is a complex claim. Also supported are simple claims:
      // claim: {'Key' : 'Value'}
    }).then(att => {
      messageLogger(att, 'Encoded Attestation Sent to User (Signed JWT)')
      messageLogger(decodeJWT(att), 'Decoded Attestation Payload of Above')
      return push(att)
    }).then(res => {
      messageLogger('Push notification with attestation sent, will recieve on client in a moment')
      ngrok.disconnect()
    })
  })
})

const server = app.listen(8088, () => {
  ngrok.connect(8088).then(ngrokUrl => {
    endpoint = ngrokUrl
    console.log(`Attestation Creator Service running, open at ${endpoint}`)
  });
})
