# swagger_client.CapabilitiesApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**can_create_contract**](CapabilitiesApi.md#can_create_contract) | **GET** /api/v1/capabilities/canCreateContract/{workflowId} | 
[**capabilities_get**](CapabilitiesApi.md#capabilities_get) | **GET** /api/v1/capabilities | 


# **can_create_contract**
> bool can_create_contract(workflow_id)



Checks if user can modify user role mappings

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.CapabilitiesApi()
workflow_id = 56 # int | The id of the application

try:
    # 
    api_response = api_instance.can_create_contract(workflow_id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling CapabilitiesApi->can_create_contract: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **workflow_id** | **int**| The id of the application | 

### Return type

**bool**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **capabilities_get**
> Capabilities capabilities_get()



Checks if user can upload application

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.CapabilitiesApi()

try:
    # 
    api_response = api_instance.capabilities_get()
    pprint(api_response)
except ApiException as e:
    print("Exception when calling CapabilitiesApi->capabilities_get: %s\n" % e)
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**Capabilities**](Capabilities.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

