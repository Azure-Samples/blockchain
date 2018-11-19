'use strict';
var http = require('http');
var port = process.env.PORT || 1337;

http.createServer(function (req, res) {
    var AuthenticationContext = require('adal-node').AuthenticationContext;
    var authorityHostUrl = 'https://login.windows.net';
    var tenant = '{Tenant-Name}'; // AAD Tenant name.
    var authorityUrl = authorityHostUrl + '/' + tenant;
    var applicationId = '{Client-Application-ID}'; // Application Id of app registered under AAD.
    var clientSecret = '{Client-Secret}'; // Secret generated for app. Read this environment variable.
    var resource = '{Workbench-API-Application-ID}'; // Application Id that identifies the resource for which the token is valid.

    var context = new AuthenticationContext(authorityUrl);

    context.acquireTokenWithClientCredentials(resource, applicationId, clientSecret, function (err, tokenResponse) {
        if (err) {
            console.log('well that didn\'t work: ' + err.stack);
        } else {
            console.log(tokenResponse); // Use this tokenResponse to make API calls to workbench
        }
    });
    res.writeHead(200, { 'Content-Type': 'text/plain' });
    res.end('Hello World\n' + res.connection.localPort);
}).listen(port);
