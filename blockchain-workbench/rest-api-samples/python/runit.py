import json
import os
import sys
import adal
import swagger_client
from pprint import pprint
from swagger_client.api_client import ApiClient

def main():
	authority_url = 'https://login.windows.net/[Your Tenant]'
	client_id = '[Your client_id]'
	client_secret = '[Your secret]'
	context = adal.AuthenticationContext(authority_url,api_version=None)
	token = context.acquire_token_with_client_credentials(client_id,client_id,client_secret)
	api_client = ApiClient();
	#api_client.configuration.host = "[Set this if custom host and port is needed]"
	bearer_token = "Bearer "+token['accessToken']
	api_client.set_default_header('Authorization',bearer_token)
	api_user = swagger_client.UsersApi(api_client)
	me = api_user.me_get()	
	pprint(me)

main()
