# Auth Samples and Code Snippets for Workbench

### Python Sample

Getting the ADAL Package:
1. Go to Python Package Manager
2. Run command 'pip install adal'
 
Code Sample:
```
auth_context = AuthenticationContext("https://login.microsoftonline.com/{YOUR-TENANT-ID}")
token = auth_context.acquire_token_with_client_credentials("{WORKBENCH-APP-ID}", "{YOUR-CLIENT-APP-ID}", "{YOUR-CLIENT-SECRET}")
```