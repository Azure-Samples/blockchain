# swagger_client.ApplicationsApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**application_delete**](ApplicationsApi.md#application_delete) | **DELETE** /api/v1/applications/{applicationID} | 
[**application_disable**](ApplicationsApi.md#application_disable) | **PATCH** /api/v1/applications/{applicationID}/disable | 
[**application_enable**](ApplicationsApi.md#application_enable) | **PATCH** /api/v1/applications/{applicationID}/enable | 
[**application_get**](ApplicationsApi.md#application_get) | **GET** /api/v1/applications/{applicationId} | 
[**applications_get**](ApplicationsApi.md#applications_get) | **GET** /api/v1/applications | 
[**applications_post**](ApplicationsApi.md#applications_post) | **POST** /api/v1/applications | 
[**contract_code_delete**](ApplicationsApi.md#contract_code_delete) | **DELETE** /api/v1/applications/contractCode/{contractCodeId} | 
[**contract_code_get**](ApplicationsApi.md#contract_code_get) | **GET** /api/v1/applications/contractCode/{contractCodeId} | 
[**contract_code_post**](ApplicationsApi.md#contract_code_post) | **POST** /api/v1/applications/{applicationId}/contractCode | 
[**contract_codes_get**](ApplicationsApi.md#contract_codes_get) | **GET** /api/v1/applications/{applicationID}/contractCode | 
[**role_assignment_delete**](ApplicationsApi.md#role_assignment_delete) | **DELETE** /api/v1/applications/{applicationId}/roleAssignments/{roleAssignmentId} | 
[**role_assignment_get**](ApplicationsApi.md#role_assignment_get) | **GET** /api/v1/applications/{applicationId}/roleAssignments/{roleAssignmentId} | 
[**role_assignments_get**](ApplicationsApi.md#role_assignments_get) | **GET** /api/v1/applications/{applicationId}/roleAssignments | 
[**role_assignments_post**](ApplicationsApi.md#role_assignments_post) | **POST** /api/v1/applications/{applicationId}/roleAssignments | 
[**workflow_get**](ApplicationsApi.md#workflow_get) | **GET** /api/v1/applications/workflows/{workflowId} | 
[**workflows_get**](ApplicationsApi.md#workflows_get) | **GET** /api/v1/applications/{applicationId}/workflows | 


# **application_delete**
> application_delete(application_id)



Deletes the specified blockchain application. This method can only be performed by users who are              Workbench administrators. NOTE: Currently not implemented.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ApplicationsApi()
application_id = 'application_id_example' # str | The id of the application.

try:
    # 
    api_instance.application_delete(application_id)
except ApiException as e:
    print("Exception when calling ApplicationsApi->application_delete: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **application_id** | **str**| The id of the application. | 

### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **application_disable**
> application_disable(application_id)



Disables the specified blockchain application. This method can only be performed by users who are              Workbench administrators.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ApplicationsApi()
application_id = 56 # int | The id of the application.

try:
    # 
    api_instance.application_disable(application_id)
except ApiException as e:
    print("Exception when calling ApplicationsApi->application_disable: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **application_id** | **int**| The id of the application. | 

### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **application_enable**
> application_enable(application_id)



Enables the specified blockchain application. This method can only be performed by users who are              Workbench administrators.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ApplicationsApi()
application_id = 56 # int | The id of the application.

try:
    # 
    api_instance.application_enable(application_id)
except ApiException as e:
    print("Exception when calling ApplicationsApi->application_enable: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **application_id** | **int**| The id of the application. | 

### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **application_get**
> Application application_get(application_id)



Gets the blockchain application matching a specific application ID. Users who are Workbench administrators get              the blockchain application. Non-Workbench administrators get the blockchain application if they have at least one associated              application role or is associated with a smart contract instance role.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ApplicationsApi()
application_id = 56 # int | The id of the application.

try:
    # 
    api_response = api_instance.application_get(application_id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ApplicationsApi->application_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **application_id** | **int**| The id of the application. | 

### Return type

[**Application**](Application.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **applications_get**
> ApplicationList applications_get(top=top, skip=skip, enabled=enabled)



Lists all blockchain applications to which a user has access in Workbench. Users who are Workbench administrators get              all blockchain applications. Non-Workbench administrators get all blockchain applications for which they have at least one              associated application role or an associated smart contract instance role.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ApplicationsApi()
top = 56 # int | The maximum number of entries to return in the result set. (optional)
skip = 56 # int | The number of entries to skip in the result set. (optional)
enabled = true # bool | A Boolean for whether to filter the result set to only enabled applications. (optional)

try:
    # 
    api_response = api_instance.applications_get(top=top, skip=skip, enabled=enabled)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ApplicationsApi->applications_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **top** | **int**| The maximum number of entries to return in the result set. | [optional] 
 **skip** | **int**| The number of entries to skip in the result set. | [optional] 
 **enabled** | **bool**| A Boolean for whether to filter the result set to only enabled applications. | [optional] 

### Return type

[**ApplicationList**](ApplicationList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **applications_post**
> int applications_post(file)



Creates a new blockchain application. This method can only be performed by users who are              Workbench administrators.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ApplicationsApi()
file = '/path/to/file.txt' # file | Upload File

try:
    # 
    api_response = api_instance.applications_post(file)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ApplicationsApi->applications_post: %s\n" % e)
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

# **contract_code_delete**
> contract_code_delete(contract_code_id)



Deletes the specified blockchain smart contract implementation of a specific blockchain application.              This method can only be performed by users who are Workbench administrators.              NOTE: not currently implemented

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ApplicationsApi()
contract_code_id = 56 # int | The id of the contract code

try:
    # 
    api_instance.contract_code_delete(contract_code_id)
except ApiException as e:
    print("Exception when calling ApplicationsApi->contract_code_delete: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **contract_code_id** | **int**| The id of the contract code | 

### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **contract_code_get**
> FileStreamResult contract_code_get(contract_code_id)



Get the blockchain smart contract implementation matching a specific              ledger implementation ID. Users who are Workbench administrators get the specified smart contract implementation.              Non-Workbench administrators get the smart contract implementation if they have at least one associated application              role or is associated with a smart contract instance role.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ApplicationsApi()
contract_code_id = 56 # int | The id of the contract code

try:
    # 
    api_response = api_instance.contract_code_get(contract_code_id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ApplicationsApi->contract_code_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **contract_code_id** | **int**| The id of the contract code | 

### Return type

[**FileStreamResult**](FileStreamResult.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **contract_code_post**
> int contract_code_post(application_id, file, ledger_id=ledger_id)



Uploads one or more smart contracts (ex. .sol or .zip), representing the implementation of the specified blockchain              application. This method can only be performed by users who are Workbench administrators.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ApplicationsApi()
application_id = 56 # int | The id of the application
file = '/path/to/file.txt' # file | Upload File
ledger_id = 56 # int | The index of the ledger (optional)

try:
    # 
    api_response = api_instance.contract_code_post(application_id, file, ledger_id=ledger_id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ApplicationsApi->contract_code_post: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **application_id** | **int**| The id of the application | 
 **file** | **file**| Upload File | 
 **ledger_id** | **int**| The index of the ledger | [optional] 

### Return type

**int**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **contract_codes_get**
> ContractCodeList contract_codes_get(application_id, ledger_id=ledger_id, top=top, skip=skip)



List all blockchain smart contract implementations of the specified blockchain application.              Users who are Workbench administrators get all smart contract implementations. Non-Workbench administrators get all              smart contract implementations for which they have at least one associated application role or is associated with a              smart contract instance role.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ApplicationsApi()
application_id = 56 # int | The id of the application
ledger_id = 56 # int | The index of the chain type (optional)
top = 56 # int | The maximum number of items to return (optional)
skip = 56 # int | The number of items to skip before returning (optional)

try:
    # 
    api_response = api_instance.contract_codes_get(application_id, ledger_id=ledger_id, top=top, skip=skip)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ApplicationsApi->contract_codes_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **application_id** | **int**| The id of the application | 
 **ledger_id** | **int**| The index of the chain type | [optional] 
 **top** | **int**| The maximum number of items to return | [optional] 
 **skip** | **int**| The number of items to skip before returning | [optional] 

### Return type

[**ContractCodeList**](ContractCodeList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **role_assignment_delete**
> role_assignment_delete(application_id, role_assignment_id)



Deletes the specified role assignment. This method can only be performed by users who are              Workbench administrators.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ApplicationsApi()
application_id = 56 # int | The id of the application
role_assignment_id = 56 # int | The id of the role assignment

try:
    # 
    api_instance.role_assignment_delete(application_id, role_assignment_id)
except ApiException as e:
    print("Exception when calling ApplicationsApi->role_assignment_delete: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **application_id** | **int**| The id of the application | 
 **role_assignment_id** | **int**| The id of the role assignment | 

### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **role_assignment_get**
> RoleAssignment role_assignment_get(application_id, role_assignment_id)



Get a role assignment of the specified blockchain application matching a specific user role assignment ID.              Users who are Workbench administrators get the role assignment. Non-Workbench administrators get the role assignment              if they are associated in the application.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ApplicationsApi()
application_id = 56 # int | The id of the configuration
role_assignment_id = 56 # int | The id of the role assignment

try:
    # 
    api_response = api_instance.role_assignment_get(application_id, role_assignment_id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ApplicationsApi->role_assignment_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **application_id** | **int**| The id of the configuration | 
 **role_assignment_id** | **int**| The id of the role assignment | 

### Return type

[**RoleAssignment**](RoleAssignment.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **role_assignments_get**
> RoleAssignmentList role_assignments_get(application_id, application_role_id=application_role_id, top=top, skip=skip)



List all role assignments of the specified blockchain application. Users who are Workbench administrators              get all role assignments. Non-Workbench administrators get all their role assignments. Roles are specified              in the Workbench application configuration and can be retrieved from GET /applications/{applicationID}.              Also, user information can be retrieved from GET /users/{userID}.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ApplicationsApi()
application_id = 56 # int | The id of the configuration
application_role_id = 56 # int | The id of the application role (optional)
top = 56 # int | The maximum number of entries to return in the result set. (optional)
skip = 56 # int | The number of entries to skip in the result set. (optional)

try:
    # 
    api_response = api_instance.role_assignments_get(application_id, application_role_id=application_role_id, top=top, skip=skip)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ApplicationsApi->role_assignments_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **application_id** | **int**| The id of the configuration | 
 **application_role_id** | **int**| The id of the application role | [optional] 
 **top** | **int**| The maximum number of entries to return in the result set. | [optional] 
 **skip** | **int**| The number of entries to skip in the result set. | [optional] 

### Return type

[**RoleAssignmentList**](RoleAssignmentList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **role_assignments_post**
> int role_assignments_post(application_id, role_assignment=role_assignment)



Creates a user-to-role mapping in the specified blockchain application. This method can only be performed by              users who are Workbench administrators.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ApplicationsApi()
application_id = 56 # int | The id of the configuration.
role_assignment = swagger_client.RoleAssignmentInput() # RoleAssignmentInput | New user-to-role mapping. (optional)

try:
    # 
    api_response = api_instance.role_assignments_post(application_id, role_assignment=role_assignment)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ApplicationsApi->role_assignments_post: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **application_id** | **int**| The id of the configuration. | 
 **role_assignment** | [**RoleAssignmentInput**](RoleAssignmentInput.md)| New user-to-role mapping. | [optional] 

### Return type

**int**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json-patch+json, application/json, text/json, application/*+json
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **workflow_get**
> Workflow workflow_get(workflow_id)



Get a workflow matching a specific workflow ID.              Users who are Workbench administrators get the workflow. Non-Workbench administrators get the workflow if they              have at least one associated application role or is associated with a smart contract instance role.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ApplicationsApi()
workflow_id = 56 # int | The id of the workflow

try:
    # 
    api_response = api_instance.workflow_get(workflow_id)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ApplicationsApi->workflow_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **workflow_id** | **int**| The id of the workflow | 

### Return type

[**Workflow**](Workflow.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **workflows_get**
> WorkflowList workflows_get(application_id, top=top, skip=skip)



List all workflows of the specified blockchain application. Users who are Workbench administrators get all              workflows. Non-Workbench administrators get all workflows for which they have at least one associated application role              or is associated with a smart contract instance role.

### Example
```python
from __future__ import print_function
import time
import swagger_client
from swagger_client.rest import ApiException
from pprint import pprint

# create an instance of the API class
api_instance = swagger_client.ApplicationsApi()
application_id = 56 # int | The id of the application
top = 56 # int | The maximum number of items to return (optional)
skip = 56 # int | The number of items to skip before returning (optional)

try:
    # 
    api_response = api_instance.workflows_get(application_id, top=top, skip=skip)
    pprint(api_response)
except ApiException as e:
    print("Exception when calling ApplicationsApi->workflows_get: %s\n" % e)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **application_id** | **int**| The id of the application | 
 **top** | **int**| The maximum number of items to return | [optional] 
 **skip** | **int**| The number of items to skip before returning | [optional] 

### Return type

[**WorkflowList**](WorkflowList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

