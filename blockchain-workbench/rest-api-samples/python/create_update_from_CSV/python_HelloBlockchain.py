#%%
from adal import AuthenticationContext
import requests
import json
import pandas as pd
import numpy as np

#%%
AUTHORITY = 'https://login.microsoftonline.com/*****.onmicrosoft.com'

# Click on this link to get the Swagger API reference
WORKBENCH_API_URL = 'https://*****-*****-api.azurewebsites.net'

# This is the application ID of the blockchain workbench web API
# Login to the directory of the tenant -> App registrations -> 'Azure Blockchain Workbench *****-***** ->
# copy the Application ID which is the RESOURCE
RESOURCE = '********-****-****-****-************'

#Service principal app id & secret/key:
CLIENT_APP_Id = '********-****-****-****-************'
CLIENT_SECRET = '***************/********************+******='

auth_context = AuthenticationContext(AUTHORITY)

#%%
# Read/process CSV into pandas df
def process_df(filename):
    df = pd.read_csv(filename)
    cols = list(df)
    for col in cols:  
        df[col] = df[col].astype(str)
    return df

#%%
def make_create_payload(df,index):
    # This function generates the payload json fed from the pandas df
    
    workflowFunctionId = 1

    try:
        payload = {
            "workflowFunctionId": workflowFunctionId,
            "workflowActionParameters": [
                {
                    "name": "message",
                    "value": df['value'][index]
                }
        ]
        }
        payload = json.dumps(payload)
        return payload
    except:
        print('error')

#%%
def make_update_payload(df,index):

    # This function generates the payload json fed from the pandas df

    workflowFunctionId = 3

    try:
        payload = {
            "workflowFunctionId": workflowFunctionId,
            "workflowActionParameters": [
                {
                    "name": "responseMessage",
                    "value": df['value'][index]
                }
        ]
        }
        payload = json.dumps(payload)
        return payload
    except:
        print('error')
    
#%%
#function to perform api post for contract create
def create_contract(workflowId, contractCodeId, connectionId, payload):
    if __name__ == '__main__':
        try:
            # Acquiring the token
            token = auth_context.acquire_token_with_client_credentials(
            RESOURCE, CLIENT_APP_Id, CLIENT_SECRET)
            #pprint(str(token))

            url = WORKBENCH_API_URL + '/api/v2/contracts'

            headers = {'Authorization': 'Bearer ' +
                   token['accessToken'], 'Content-Type': 'application/json'}

            params = {'workflowId': workflowId, 'contractCodeId': contractCodeId, 'connectionId': connectionId}

        # Making call to Workbench
            response = requests.post(url=url,data=payload,headers=headers,params=params)

            print('Status code: ' + str(response.status_code), '\n')
            print('Created contractId: ' + str(response.text), '\n', '\n')
            return response
        except Exception as error:
            print(error)
            return error

#%%
#function to perform api post for contract update
def update_contract(contractId, payload):
    if __name__ == '__main__':
        try:
            # Acquiring the token
            token = auth_context.acquire_token_with_client_credentials(
            RESOURCE, CLIENT_APP_Id, CLIENT_SECRET)
            #pprint(str(token))

            url = WORKBENCH_API_URL + '/api/v2/contracts/' + str(contractId) + '/actions'

            headers = {'Authorization': 'Bearer ' +
                   token['accessToken'], 'Content-Type': 'application/json'}

            # params = {}

        # Making call to Workbench
            response = requests.post(url=url,data=payload,headers=headers)

            print('Status code: ' + str(response.status_code), '\n')
            print('Response: ' + str(response.text), '\n', '\n')
            return response
        except Exception as error:
            print(error)
            return error

#%%
fnCreate = 'hello_blockchain_create_data.csv'
dfCreate = process_df(fnCreate)
print(dfCreate)

#%%
#This cell will run the payload generation, contract creation functions, 
# and store the contractIds into a list to call for updating

workflowId = 1
contractCodeId = 1
connectionId = 1

createdContracts = []

for index, row in dfCreate.iterrows():
    try:
        payload = make_create_payload(dfCreate,index)
        resp = create_contract(workflowId,contractCodeId,connectionId,payload)
        createdContracts.append(resp.text)
    except:
        print('contract creation failed')
        continue

#%%
fnUpdate = 'hello_blockchain_update_data.csv'
dfUpdate = process_df(fnUpdate)
print(dfUpdate)

#%%
#may need to pause before running this cell
contractIds = createdContracts.copy()

for index, row in dfUpdate.iterrows():
    try:
        payload = make_update_payload(dfUpdate,index)
        update_contract(contractIds[index],payload)
    except:
        print('contract creation failed')
        continue