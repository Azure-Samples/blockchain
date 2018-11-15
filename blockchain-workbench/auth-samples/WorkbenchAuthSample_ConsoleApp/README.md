# Auth Samples and Code Snippets for Workbench

### C# Sample

Getting the ADAL Nuget Package:
1. Go to Nuget Package Manager
2. Install the latest version of Microsoft.IdentityModel.Clients.ActiveDirectory
 
Code Sample:
```
AuthenticationContext authenticationContext = new AuthenticationContext("https://login.microsoftonline.com/{YOUR-TENANT-ID}");
ClientCredential clientCredential = new ClientCredential("{YOUR-CLIENT-APP-ID}", "{YOUR-CLIENT-SECRET}");
AuthenticationResult result = await authenticationContext.AcquireTokenAsync("{WORKBENCH-APP-ID}", clientCredential).ConfigureAwait(false);
var token = result.AccessToken;
```