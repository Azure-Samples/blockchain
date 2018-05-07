# swagger_client.CheckersApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**check_application_post**](CheckersApi.md#check_application_post) | **POST** /api/v1/checkers/checkApplication | Check validity of application configuration for Workbench
[**check_contract_code_post**](CheckersApi.md#check_contract_code_post) | **POST** /api/v1/checkers/checkContractCode | Check validity of application ledger implementation for Workbench


# **check_application_post**
> int check_application_post(file)

Check validity of application configuration for Workbench

Checks a configuration file

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.CheckersApi()
file = '/path/to/file.txt' # file | Upload File

try:
    # Check validity of application configuration for Workbench
    api_response = api_instance.check_application_post(file)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling CheckersApi->check_application_post: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | **file**| Upload File | 

### Return type

**int**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **check_contract_code_post**
> int check_contract_code_post(ledger_id=ledger_id)

Check validity of application ledger implementation for Workbench

Checks a ledger implementation file against configuration file

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.CheckersApi()
ledger_id = 56 # int | The input chain type id (optional)

try:
    # Check validity of application ledger implementation for Workbench
    api_response = api_instance.check_contract_code_post(ledger_id=ledger_id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling CheckersApi->check_contract_code_post: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **ledger_id** | **int**| The input chain type id | [optional] 

### Return type

**int**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

