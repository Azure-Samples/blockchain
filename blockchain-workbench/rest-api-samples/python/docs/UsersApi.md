# swagger_client.UsersApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**me_get**](UsersApi.md#me_get) | **GET** /api/v1/users/me | 
[**user_delete**](UsersApi.md#user_delete) | **DELETE** /api/v1/users/{userID} | 
[**user_get**](UsersApi.md#user_get) | **GET** /api/v1/users/{userID} | 
[**users_get**](UsersApi.md#users_get) | **GET** /api/v1/users | Get Users
[**users_post**](UsersApi.md#users_post) | **POST** /api/v1/users | 


# **me_get**
> Me me_get()



Returns the current user

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.UsersApi()

try:
    # 
    api_response = api_instance.me_get()
    pprint(api_response)
except ApiException as e:
    print("Exception when calling UsersApi->me_get: %s\n" % e)
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**Me**](Me.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **user_delete**
> user_delete(user_id)



Deletes the specified user. This method can only be performed by users who are Workbench administrators.              NOTE: Not currently implemented.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.UsersApi()
user_id = 'user_id_example' # str | The id of the user

try:
    # 
    api_instance.user_delete(user_id)
except ApiException as e:
    print("Exception when calling UsersApi->user_delete: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **user_id** | **str**| The id of the user | 

### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **user_get**
> User user_get(user_id)



Gets the user matching a specific user ID.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.UsersApi()
user_id = 56 # int | The id of the user

try:
    # 
    api_response = api_instance.user_get(user_id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling UsersApi->user_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **user_id** | **int**| The id of the user | 

### Return type

[**User**](User.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **users_get**
> UserList users_get(top=top, skip=skip, external_id=external_id, user_chain_identifier=user_chain_identifier)

Get Users

Lists all users within the connected blockchain consortium.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.UsersApi()
top = 56 # int | The maximum number of items to return (optional)
skip = 56 # int | The number of items to skip before returning (optional)
external_id = 'external_id_example' # str | The external ID of the user to query for (optional)
user_chain_identifier = 'user_chain_identifier_example' # str | The on-chain address of the user to query for (optional)

try:
    # Get Users
    api_response = api_instance.users_get(top=top, skip=skip, external_id=external_id, user_chain_identifier=user_chain_identifier)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling UsersApi->users_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **top** | **int**| The maximum number of items to return | [optional] 
 **skip** | **int**| The number of items to skip before returning | [optional] 
 **external_id** | **str**| The external ID of the user to query for | [optional] 
 **user_chain_identifier** | **str**| The on-chain address of the user to query for | [optional] 

### Return type

[**UserList**](UserList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **users_post**
> int users_post(user_input=user_input)



Adds a user to the blockchain consortium. This method can only be performed by users who are              Workbench administrators.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.UsersApi()
user_input = swagger_client.UserInput() # UserInput | New user to add (optional)

try:
    # 
    api_response = api_instance.users_post(user_input=user_input)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling UsersApi->users_post: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **user_input** | [**UserInput**](UserInput.md)| New user to add | [optional] 

### Return type

**int**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json-patch+json, application/json, text/json, application/*+json
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

