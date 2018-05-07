# swagger_client.GraphProxyApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**graph_proxy_users_get**](GraphProxyApi.md#graph_proxy_users_get) | **GET** /api/v1/graph-proxy/{version}/users | Get Users from AAD Graph


# **graph_proxy_users_get**
> ContentResult graph_proxy_users_get(version)

Get Users from AAD Graph

Proxies query parameters to AAD graph

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.GraphProxyApi()
version = 'version_example' # str | The version for the graph api endpoint

try:
    # Get Users from AAD Graph
    api_response = api_instance.graph_proxy_users_get(version)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling GraphProxyApi->graph_proxy_users_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **version** | **str**| The version for the graph api endpoint | 

### Return type

[**ContentResult**](ContentResult.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

