# CapabilitiesApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**canCreateContract**](CapabilitiesApi.md#canCreateContract) | **GET** /api/v1/capabilities/canCreateContract/{workflowId} | 
[**capabilitiesGet**](CapabilitiesApi.md#capabilitiesGet) | **GET** /api/v1/capabilities | 


<a name="canCreateContract"></a>
# **canCreateContract**
> Boolean canCreateContract(workflowId)



Checks if user can modify user role mappings

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CapabilitiesApi;


CapabilitiesApi apiInstance = new CapabilitiesApi();
Integer workflowId = 56; // Integer | The id of the application
try {
    Boolean result = apiInstance.canCreateContract(workflowId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CapabilitiesApi#canCreateContract");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **workflowId** | **Integer**| The id of the application |

### Return type

**Boolean**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="capabilitiesGet"></a>
# **capabilitiesGet**
> Capabilities capabilitiesGet()



Checks if user can upload application

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CapabilitiesApi;


CapabilitiesApi apiInstance = new CapabilitiesApi();
try {
    Capabilities result = apiInstance.capabilitiesGet();
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CapabilitiesApi#capabilitiesGet");
    e.printStackTrace();
}
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

