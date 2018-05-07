# swagger_client.LedgersApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**ledgers_get**](LedgersApi.md#ledgers_get) | **GET** /api/v1/ledgers | 


# **ledgers_get**
> LedgerList ledgers_get(top=top, skip=skip)



Lists the supported blockchain types, such as Ethereum or Hyperledger Fabric.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.LedgersApi()
top = 56 # int | The maximum number of items to return (optional)
skip = 56 # int | The number of items to skip before returning (optional)

try:
    # 
    api_response = api_instance.ledgers_get(top=top, skip=skip)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling LedgersApi->ledgers_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **top** | **int**| The maximum number of items to return | [optional] 
 **skip** | **int**| The number of items to skip before returning | [optional] 

### Return type

[**LedgerList**](LedgerList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

