# Auth Samples and Code Snippets for Workbench

## Node.JS Sample

### Getting the ADAL Node Package:
1. Go to Node Package Manager
2. Install the latest version of 'adal-node'
 
### Code Sample:
```
var AuthenticationContext = require('adal-node').AuthenticationContext;
var authorityHostUrl = 'https://login.windows.net';
var tenant = '{YOUR-TENANT-NAME}'; // AAD Tenant name.
var authorityUrl = authorityHostUrl + '/' + tenant;
var applicationId = '{YOUR-CLIENT-APP-ID}'; // Application Id of app registered under AAD.
var clientSecret = '{YOUR-CLIENT-SECRET}'; // Secret generated for app. Read this environment variable.
var resource = '{WORKBENCH-APP-ID}'; // Application Id that identifies the resource for which the token is valid.
 
var context = new AuthenticationContext(authorityUrl);
 
context.acquireTokenWithClientCredentials(resource, applicationId, clientSecret, function (err, tokenResponse) {
    if (err) {
        console.log('well that didn\'t work: ' + err.stack);
    } else {
        console.log(tokenResponse);
    }
});
```