# ApplicationsApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**applicationDelete**](ApplicationsApi.md#applicationDelete) | **DELETE** /api/v1/applications/{applicationID} | 
[**applicationDisable**](ApplicationsApi.md#applicationDisable) | **PATCH** /api/v1/applications/{applicationID}/disable | 
[**applicationEnable**](ApplicationsApi.md#applicationEnable) | **PATCH** /api/v1/applications/{applicationID}/enable | 
[**applicationGet**](ApplicationsApi.md#applicationGet) | **GET** /api/v1/applications/{applicationId} | 
[**applicationsGet**](ApplicationsApi.md#applicationsGet) | **GET** /api/v1/applications | 
[**applicationsPost**](ApplicationsApi.md#applicationsPost) | **POST** /api/v1/applications | 
[**contractCodeDelete**](ApplicationsApi.md#contractCodeDelete) | **DELETE** /api/v1/applications/contractCode/{contractCodeId} | 
[**contractCodeGet**](ApplicationsApi.md#contractCodeGet) | **GET** /api/v1/applications/contractCode/{contractCodeId} | 
[**contractCodePost**](ApplicationsApi.md#contractCodePost) | **POST** /api/v1/applications/{applicationId}/contractCode | 
[**contractCodesGet**](ApplicationsApi.md#contractCodesGet) | **GET** /api/v1/applications/{applicationID}/contractCode | 
[**roleAssignmentDelete**](ApplicationsApi.md#roleAssignmentDelete) | **DELETE** /api/v1/applications/{applicationId}/roleAssignments/{roleAssignmentId} | 
[**roleAssignmentGet**](ApplicationsApi.md#roleAssignmentGet) | **GET** /api/v1/applications/{applicationId}/roleAssignments/{roleAssignmentId} | 
[**roleAssignmentsGet**](ApplicationsApi.md#roleAssignmentsGet) | **GET** /api/v1/applications/{applicationId}/roleAssignments | 
[**roleAssignmentsPost**](ApplicationsApi.md#roleAssignmentsPost) | **POST** /api/v1/applications/{applicationId}/roleAssignments | 
[**workflowGet**](ApplicationsApi.md#workflowGet) | **GET** /api/v1/applications/workflows/{workflowId} | 
[**workflowsGet**](ApplicationsApi.md#workflowsGet) | **GET** /api/v1/applications/{applicationId}/workflows | 


<a name="applicationDelete"></a>
# **applicationDelete**
> applicationDelete(applicationID)



Deletes the specified blockchain application. This method can only be performed by users who are              Workbench administrators. NOTE: Currently not implemented.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ApplicationsApi;


ApplicationsApi apiInstance = new ApplicationsApi();
String applicationID = "applicationID_example"; // String | The id of the application.
try {
    apiInstance.applicationDelete(applicationID);
} catch (ApiException e) {
    System.err.println("Exception when calling ApplicationsApi#applicationDelete");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **applicationID** | **String**| The id of the application. |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="applicationDisable"></a>
# **applicationDisable**
> applicationDisable(applicationId)



Disables the specified blockchain application. This method can only be performed by users who are              Workbench administrators.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ApplicationsApi;


ApplicationsApi apiInstance = new ApplicationsApi();
Integer applicationId = 56; // Integer | The id of the application.
try {
    apiInstance.applicationDisable(applicationId);
} catch (ApiException e) {
    System.err.println("Exception when calling ApplicationsApi#applicationDisable");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **applicationId** | **Integer**| The id of the application. |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="applicationEnable"></a>
# **applicationEnable**
> applicationEnable(applicationId)



Enables the specified blockchain application. This method can only be performed by users who are              Workbench administrators.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ApplicationsApi;


ApplicationsApi apiInstance = new ApplicationsApi();
Integer applicationId = 56; // Integer | The id of the application.
try {
    apiInstance.applicationEnable(applicationId);
} catch (ApiException e) {
    System.err.println("Exception when calling ApplicationsApi#applicationEnable");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **applicationId** | **Integer**| The id of the application. |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="applicationGet"></a>
# **applicationGet**
> Application applicationGet(applicationId)



Gets the blockchain application matching a specific application ID. Users who are Workbench administrators get              the blockchain application. Non-Workbench administrators get the blockchain application if they have at least one associated              application role or is associated with a smart contract instance role.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ApplicationsApi;


ApplicationsApi apiInstance = new ApplicationsApi();
Integer applicationId = 56; // Integer | The id of the application.
try {
    Application result = apiInstance.applicationGet(applicationId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ApplicationsApi#applicationGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **applicationId** | **Integer**| The id of the application. |

### Return type

[**Application**](Application.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="applicationsGet"></a>
# **applicationsGet**
> ApplicationList applicationsGet(top, skip, enabled)



Lists all blockchain applications to which a user has access in Workbench. Users who are Workbench administrators get              all blockchain applications. Non-Workbench administrators get all blockchain applications for which they have at least one              associated application role or an associated smart contract instance role.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ApplicationsApi;


ApplicationsApi apiInstance = new ApplicationsApi();
Integer top = 56; // Integer | The maximum number of entries to return in the result set.
Integer skip = 56; // Integer | The number of entries to skip in the result set.
Boolean enabled = true; // Boolean | A Boolean for whether to filter the result set to only enabled applications.
try {
    ApplicationList result = apiInstance.applicationsGet(top, skip, enabled);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ApplicationsApi#applicationsGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **top** | **Integer**| The maximum number of entries to return in the result set. | [optional]
 **skip** | **Integer**| The number of entries to skip in the result set. | [optional]
 **enabled** | **Boolean**| A Boolean for whether to filter the result set to only enabled applications. | [optional]

### Return type

[**ApplicationList**](ApplicationList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="applicationsPost"></a>
# **applicationsPost**
> Integer applicationsPost(file)



Creates a new blockchain application. This method can only be performed by users who are              Workbench administrators.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ApplicationsApi;


ApplicationsApi apiInstance = new ApplicationsApi();
File file = new File("/path/to/file.txt"); // File | Upload File
try {
    Integer result = apiInstance.applicationsPost(file);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ApplicationsApi#applicationsPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **file** | **File**| Upload File |

### Return type

**Integer**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: Not defined

<a name="contractCodeDelete"></a>
# **contractCodeDelete**
> contractCodeDelete(contractCodeId)



Deletes the specified blockchain smart contract implementation of a specific blockchain application.              This method can only be performed by users who are Workbench administrators.              NOTE: not currently implemented

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ApplicationsApi;


ApplicationsApi apiInstance = new ApplicationsApi();
Integer contractCodeId = 56; // Integer | The id of the contract code
try {
    apiInstance.contractCodeDelete(contractCodeId);
} catch (ApiException e) {
    System.err.println("Exception when calling ApplicationsApi#contractCodeDelete");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **contractCodeId** | **Integer**| The id of the contract code |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="contractCodeGet"></a>
# **contractCodeGet**
> FileStreamResult contractCodeGet(contractCodeId)



Get the blockchain smart contract implementation matching a specific              ledger implementation ID. Users who are Workbench administrators get the specified smart contract implementation.              Non-Workbench administrators get the smart contract implementation if they have at least one associated application              role or is associated with a smart contract instance role.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ApplicationsApi;


ApplicationsApi apiInstance = new ApplicationsApi();
Integer contractCodeId = 56; // Integer | The id of the contract code
try {
    FileStreamResult result = apiInstance.contractCodeGet(contractCodeId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ApplicationsApi#contractCodeGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **contractCodeId** | **Integer**| The id of the contract code |

### Return type

[**FileStreamResult**](FileStreamResult.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="contractCodePost"></a>
# **contractCodePost**
> Integer contractCodePost(applicationId, file, ledgerId)



Uploads one or more smart contracts (ex. .sol or .zip), representing the implementation of the specified blockchain              application. This method can only be performed by users who are Workbench administrators.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ApplicationsApi;


ApplicationsApi apiInstance = new ApplicationsApi();
Integer applicationId = 56; // Integer | The id of the application
File file = new File("/path/to/file.txt"); // File | Upload File
Integer ledgerId = 56; // Integer | The index of the ledger
try {
    Integer result = apiInstance.contractCodePost(applicationId, file, ledgerId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ApplicationsApi#contractCodePost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **applicationId** | **Integer**| The id of the application |
 **file** | **File**| Upload File |
 **ledgerId** | **Integer**| The index of the ledger | [optional]

### Return type

**Integer**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: Not defined

<a name="contractCodesGet"></a>
# **contractCodesGet**
> ContractCodeList contractCodesGet(applicationId, ledgerId, top, skip)



List all blockchain smart contract implementations of the specified blockchain application.              Users who are Workbench administrators get all smart contract implementations. Non-Workbench administrators get all              smart contract implementations for which they have at least one associated application role or is associated with a              smart contract instance role.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ApplicationsApi;


ApplicationsApi apiInstance = new ApplicationsApi();
Integer applicationId = 56; // Integer | The id of the application
Integer ledgerId = 56; // Integer | The index of the chain type
Integer top = 56; // Integer | The maximum number of items to return
Integer skip = 56; // Integer | The number of items to skip before returning
try {
    ContractCodeList result = apiInstance.contractCodesGet(applicationId, ledgerId, top, skip);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ApplicationsApi#contractCodesGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **applicationId** | **Integer**| The id of the application |
 **ledgerId** | **Integer**| The index of the chain type | [optional]
 **top** | **Integer**| The maximum number of items to return | [optional]
 **skip** | **Integer**| The number of items to skip before returning | [optional]

### Return type

[**ContractCodeList**](ContractCodeList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="roleAssignmentDelete"></a>
# **roleAssignmentDelete**
> roleAssignmentDelete(applicationId, roleAssignmentId)



Deletes the specified role assignment. This method can only be performed by users who are              Workbench administrators.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ApplicationsApi;


ApplicationsApi apiInstance = new ApplicationsApi();
Integer applicationId = 56; // Integer | The id of the application
Integer roleAssignmentId = 56; // Integer | The id of the role assignment
try {
    apiInstance.roleAssignmentDelete(applicationId, roleAssignmentId);
} catch (ApiException e) {
    System.err.println("Exception when calling ApplicationsApi#roleAssignmentDelete");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **applicationId** | **Integer**| The id of the application |
 **roleAssignmentId** | **Integer**| The id of the role assignment |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="roleAssignmentGet"></a>
# **roleAssignmentGet**
> RoleAssignment roleAssignmentGet(applicationId, roleAssignmentId)



Get a role assignment of the specified blockchain application matching a specific user role assignment ID.              Users who are Workbench administrators get the role assignment. Non-Workbench administrators get the role assignment              if they are associated in the application.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ApplicationsApi;


ApplicationsApi apiInstance = new ApplicationsApi();
Integer applicationId = 56; // Integer | The id of the configuration
Integer roleAssignmentId = 56; // Integer | The id of the role assignment
try {
    RoleAssignment result = apiInstance.roleAssignmentGet(applicationId, roleAssignmentId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ApplicationsApi#roleAssignmentGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **applicationId** | **Integer**| The id of the configuration |
 **roleAssignmentId** | **Integer**| The id of the role assignment |

### Return type

[**RoleAssignment**](RoleAssignment.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="roleAssignmentsGet"></a>
# **roleAssignmentsGet**
> RoleAssignmentList roleAssignmentsGet(applicationId, applicationRoleId, top, skip)



List all role assignments of the specified blockchain application. Users who are Workbench administrators              get all role assignments. Non-Workbench administrators get all their role assignments. Roles are specified              in the Workbench application configuration and can be retrieved from GET /applications/{applicationID}.              Also, user information can be retrieved from GET /users/{userID}.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ApplicationsApi;


ApplicationsApi apiInstance = new ApplicationsApi();
Integer applicationId = 56; // Integer | The id of the configuration
Integer applicationRoleId = 56; // Integer | The id of the application role
Integer top = 56; // Integer | The maximum number of entries to return in the result set.
Integer skip = 56; // Integer | The number of entries to skip in the result set.
try {
    RoleAssignmentList result = apiInstance.roleAssignmentsGet(applicationId, applicationRoleId, top, skip);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ApplicationsApi#roleAssignmentsGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **applicationId** | **Integer**| The id of the configuration |
 **applicationRoleId** | **Integer**| The id of the application role | [optional]
 **top** | **Integer**| The maximum number of entries to return in the result set. | [optional]
 **skip** | **Integer**| The number of entries to skip in the result set. | [optional]

### Return type

[**RoleAssignmentList**](RoleAssignmentList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="roleAssignmentsPost"></a>
# **roleAssignmentsPost**
> Integer roleAssignmentsPost(applicationId, roleAssignment)



Creates a user-to-role mapping in the specified blockchain application. This method can only be performed by              users who are Workbench administrators.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ApplicationsApi;


ApplicationsApi apiInstance = new ApplicationsApi();
Integer applicationId = 56; // Integer | The id of the configuration.
RoleAssignmentInput roleAssignment = new RoleAssignmentInput(); // RoleAssignmentInput | New user-to-role mapping.
try {
    Integer result = apiInstance.roleAssignmentsPost(applicationId, roleAssignment);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ApplicationsApi#roleAssignmentsPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **applicationId** | **Integer**| The id of the configuration. |
 **roleAssignment** | [**RoleAssignmentInput**](RoleAssignmentInput.md)| New user-to-role mapping. | [optional]

### Return type

**Integer**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json-patch+json, application/json, text/json, application/_*+json
 - **Accept**: Not defined

<a name="workflowGet"></a>
# **workflowGet**
> Workflow workflowGet(workflowId)



Get a workflow matching a specific workflow ID.              Users who are Workbench administrators get the workflow. Non-Workbench administrators get the workflow if they              have at least one associated application role or is associated with a smart contract instance role.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ApplicationsApi;


ApplicationsApi apiInstance = new ApplicationsApi();
Integer workflowId = 56; // Integer | The id of the workflow
try {
    Workflow result = apiInstance.workflowGet(workflowId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ApplicationsApi#workflowGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **workflowId** | **Integer**| The id of the workflow |

### Return type

[**Workflow**](Workflow.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="workflowsGet"></a>
# **workflowsGet**
> WorkflowList workflowsGet(applicationId, top, skip)



List all workflows of the specified blockchain application. Users who are Workbench administrators get all              workflows. Non-Workbench administrators get all workflows for which they have at least one associated application role              or is associated with a smart contract instance role.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ApplicationsApi;


ApplicationsApi apiInstance = new ApplicationsApi();
Integer applicationId = 56; // Integer | The id of the application
Integer top = 56; // Integer | The maximum number of items to return
Integer skip = 56; // Integer | The number of items to skip before returning
try {
    WorkflowList result = apiInstance.workflowsGet(applicationId, top, skip);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ApplicationsApi#workflowsGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **applicationId** | **Integer**| The id of the application |
 **top** | **Integer**| The maximum number of items to return | [optional]
 **skip** | **Integer**| The number of items to skip before returning | [optional]

### Return type

[**WorkflowList**](WorkflowList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

