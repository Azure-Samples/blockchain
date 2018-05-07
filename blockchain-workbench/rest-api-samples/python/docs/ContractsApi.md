# swagger_client.ContractsApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**contract_action_get**](ContractsApi.md#contract_action_get) | **GET** /api/v1/contracts/{contractId}/actions/{actionId} | 
[**contract_action_post**](ContractsApi.md#contract_action_post) | **POST** /api/v1/contracts/{contractId}/actions | 
[**contract_actions_get**](ContractsApi.md#contract_actions_get) | **GET** /api/v1/contracts/{contractId}/actions | 
[**contract_get**](ContractsApi.md#contract_get) | **GET** /api/v1/contracts/{contractId} | 
[**contract_post**](ContractsApi.md#contract_post) | **POST** /api/v1/contracts | 
[**contracts_get**](ContractsApi.md#contracts_get) | **GET** /api/v1/contracts | 


# **contract_action_get**
> WorkflowStateTransition contract_action_get(contract_id, action_id)



Gets the action matching the specified action ID. Users get the action if the user can take the action              given the current state of the specified smart contract instance and the user's associated application role or smart              contract instance role.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ContractsApi()
contract_id = 56 # int | The id of the contract
action_id = 56 # int | The id of the action

try:
    # 
    api_response = api_instance.contract_action_get(contract_id, action_id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ContractsApi->contract_action_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **contract_id** | **int**| The id of the contract | 
 **action_id** | **int**| The id of the action | 

### Return type

[**WorkflowStateTransition**](WorkflowStateTransition.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **contract_action_post**
> LedgerActionOutput contract_action_post(contract_id, action_information=action_information)



Executes an action for the specified smart contract instance and action ID. Users are only able to execute              the action given the current state of the specified smart contract instance and the user's associated application role              or smart contract instance role.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ContractsApi()
contract_id = 56 # int | The id of the workflow instance
action_information = swagger_client.WorkflowActionInput() # WorkflowActionInput | Parameters for a particular action (optional)

try:
    # 
    api_response = api_instance.contract_action_post(contract_id, action_information=action_information)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ContractsApi->contract_action_post: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **contract_id** | **int**| The id of the workflow instance | 
 **action_information** | [**WorkflowActionInput**](WorkflowActionInput.md)| Parameters for a particular action | [optional] 

### Return type

[**LedgerActionOutput**](LedgerActionOutput.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **contract_actions_get**
> WorkflowStateTransitionList contract_actions_get(contract_id, top=top, skip=skip)



Lists all actions, which can be taken by the given user and current state of the specified smart contract              instance. Users get all applicable actions if the user has an associated application role or is associated with a smart              contract instance role for the current state of the specified smart contract instance.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ContractsApi()
contract_id = 56 # int | The id of the contract
top = 56 # int | The maximum number of items to return (optional)
skip = 56 # int | The number of items to skip before returning (optional)

try:
    # 
    api_response = api_instance.contract_actions_get(contract_id, top=top, skip=skip)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ContractsApi->contract_actions_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **contract_id** | **int**| The id of the contract | 
 **top** | **int**| The maximum number of items to return | [optional] 
 **skip** | **int**| The number of items to skip before returning | [optional] 

### Return type

[**WorkflowStateTransitionList**](WorkflowStateTransitionList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **contract_get**
> Contract contract_get(contract_id)



Creates and deploys a new smart contract instance by adding the instance to the Workbench database and              sending a transaction to the blockchain. This method can only be performed by users who are specified within the              Initiators collection of the workflow within the Workbench application configuration.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ContractsApi()
contract_id = 56 # int | The id of the contract

try:
    # 
    api_response = api_instance.contract_get(contract_id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ContractsApi->contract_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **contract_id** | **int**| The id of the contract | 

### Return type

[**Contract**](Contract.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **contract_post**
> WorkflowActionInput contract_post(workflow_action_input=workflow_action_input, workflow_id=workflow_id, contract_code_id=contract_code_id, connection_id=connection_id)



Gets the smart contract instance matching a specific workflow instance ID. Users who are Workbench              administrators get the smart contract instance. Non-Workbench administrators get the smart contract instance              if they have at least one associated application role or is associated with the smart contract instance.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ContractsApi()
workflow_action_input = swagger_client.WorkflowActionInput() # WorkflowActionInput | The set of all contract action parameters. (optional)
workflow_id = 56 # int | The ID of the workflow. (optional)
contract_code_id = 56 # int | The ID of the ledger implementation. (optional)
connection_id = 56 # int | The ID of chain instance running on the ledger. (optional)

try:
    # 
    api_response = api_instance.contract_post(workflow_action_input=workflow_action_input, workflow_id=workflow_id, contract_code_id=contract_code_id, connection_id=connection_id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ContractsApi->contract_post: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **workflow_action_input** | [**WorkflowActionInput**](WorkflowActionInput.md)| The set of all contract action parameters. | [optional] 
 **workflow_id** | **int**| The ID of the workflow. | [optional] 
 **contract_code_id** | **int**| The ID of the ledger implementation. | [optional] 
 **connection_id** | **int**| The ID of chain instance running on the ledger. | [optional] 

### Return type

[**WorkflowActionInput**](WorkflowActionInput.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **contracts_get**
> ContractList contracts_get(top=top, skip=skip, workflow_id=workflow_id)



Lists the smart contract instances of the specified workflow. Users who are Workbench administrators get all              smart contract instances. Non-Workbench administrators get all smart contract instances for which they have at least              one associated application role or is associated with a smart contract instance role.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ContractsApi()
top = 56 # int | The maximum number of items to return (optional)
skip = 56 # int | The number of items to skip before returning (optional)
workflow_id = 56 # int | The ID of the associated workflow (optional)

try:
    # 
    api_response = api_instance.contracts_get(top=top, skip=skip, workflow_id=workflow_id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ContractsApi->contracts_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **top** | **int**| The maximum number of items to return | [optional] 
 **skip** | **int**| The number of items to skip before returning | [optional] 
 **workflow_id** | **int**| The ID of the associated workflow | [optional] 

### Return type

[**ContractList**](ContractList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

