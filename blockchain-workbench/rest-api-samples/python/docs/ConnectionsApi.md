# swagger_client.ConnectionsApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**block_get**](ConnectionsApi.md#block_get) | **GET** /api/v1/ledgers/connections/{connectionId}/blocks/{blockId} | 
[**blocks_get**](ConnectionsApi.md#blocks_get) | **GET** /api/v1/ledgers/connections/{connectionId}/blocks | 
[**connection_get**](ConnectionsApi.md#connection_get) | **GET** /api/v1/ledgers/connections/{connectionId} | 
[**connections_get**](ConnectionsApi.md#connections_get) | **GET** /api/v1/ledgers/connections | 
[**transaction_get**](ConnectionsApi.md#transaction_get) | **GET** /api/v1/ledgers/connections/{connectionId}/transactions/{transactionId} | 
[**transactions_get**](ConnectionsApi.md#transactions_get) | **GET** /api/v1/ledgers/connections/{connectionId}/transactions | 


# **block_get**
> Block block_get(connection_id, block_id)



Gets the block matching a specific block ID.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ConnectionsApi()
connection_id = 56 # int | The connectionId of the block
block_id = 56 # int | The id of the block

try:
    # 
    api_response = api_instance.block_get(connection_id, block_id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ConnectionsApi->block_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connection_id** | **int**| The connectionId of the block | 
 **block_id** | **int**| The id of the block | 

### Return type

[**Block**](Block.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **blocks_get**
> BlockList blocks_get(connection_id, top=top, skip=skip)



Lists the blocks for a connected blockchain network.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ConnectionsApi()
connection_id = 56 # int | The id of the connection
top = 56 # int | The maximum number of items to return (optional)
skip = 56 # int | The number of items to skip before returning (optional)

try:
    # 
    api_response = api_instance.blocks_get(connection_id, top=top, skip=skip)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ConnectionsApi->blocks_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connection_id** | **int**| The id of the connection | 
 **top** | **int**| The maximum number of items to return | [optional] 
 **skip** | **int**| The number of items to skip before returning | [optional] 

### Return type

[**BlockList**](BlockList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **connection_get**
> Connection connection_get(connection_id)



Gets the connected blockchain network matching a specific chain instance ID.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ConnectionsApi()
connection_id = 56 # int | The id of the connection

try:
    # 
    api_response = api_instance.connection_get(connection_id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ConnectionsApi->connection_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connection_id** | **int**| The id of the connection | 

### Return type

[**Connection**](Connection.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **connections_get**
> ConnectionList connections_get(top=top, skip=skip)



Lists the connected blockchain networks.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ConnectionsApi()
top = 56 # int | The maximum number of items to return (optional)
skip = 56 # int | The number of items to skip before returning (optional)

try:
    # 
    api_response = api_instance.connections_get(top=top, skip=skip)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ConnectionsApi->connections_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **top** | **int**| The maximum number of items to return | [optional] 
 **skip** | **int**| The number of items to skip before returning | [optional] 

### Return type

[**ConnectionList**](ConnectionList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **transaction_get**
> Transaction transaction_get(connection_id, transaction_id)



Gets the transaction matching a specific transaction ID.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ConnectionsApi()
connection_id = 56 # int | The connectionId of the transaction
transaction_id = 56 # int | The id of the transaction

try:
    # 
    api_response = api_instance.transaction_get(connection_id, transaction_id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ConnectionsApi->transaction_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connection_id** | **int**| The connectionId of the transaction | 
 **transaction_id** | **int**| The id of the transaction | 

### Return type

[**Transaction**](Transaction.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **transactions_get**
> list[TransactionList] transactions_get(connection_id, top=top, skip=skip)



Lists the transactions for a connected blockchain network.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ConnectionsApi()
connection_id = 56 # int | The id of the connection
top = 56 # int | The maximum number of items to return (optional)
skip = 56 # int | The number of items to skip before returning (optional)

try:
    # 
    api_response = api_instance.transactions_get(connection_id, top=top, skip=skip)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ConnectionsApi->transactions_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connection_id** | **int**| The id of the connection | 
 **top** | **int**| The maximum number of items to return | [optional] 
 **skip** | **int**| The number of items to skip before returning | [optional] 

### Return type

[**list[TransactionList]**](TransactionList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

