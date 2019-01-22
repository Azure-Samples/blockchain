from adal import AuthenticationContext
import requests

AUTHORITY = 'https://login.microsoftonline.com/<tenant_name>'
WORKBENCH_API_URL = '<Workbench API URL>'
RESOURCE = '<Workbench AppId>'
CLIENT_APP_Id = '<service principal AppId>'
CLIENT_SECRET = '<service principal secret>'

auth_context = AuthenticationContext(AUTHORITY)

if __name__ == '__main__':
    try:
        # Acquiring the token
        token = auth_context.acquire_token_with_client_credentials(RESOURCE, CLIENT_APP_Id, CLIENT_SECRET)
        headers = {'Authorization': 'Bearer ' + token['accessToken']}

        # Making call to Workbench
        response = requests.get(WORKBENCH_API_URL + '/api/v1/users', headers=headers)
        print (response.text)
    except Exception as error:
        print (error)