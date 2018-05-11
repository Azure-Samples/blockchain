# GraphProxyApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**graphProxyUsersGet**](GraphProxyApi.md#graphProxyUsersGet) | **GET** /api/v1/graph-proxy/{version}/users | Get Users from AAD Graph


<a name="graphProxyUsersGet"></a>
# **graphProxyUsersGet**
> ContentResult graphProxyUsersGet(version)

Get Users from AAD Graph

Proxies query parameters to AAD graph

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.GraphProxyApi;


GraphProxyApi apiInstance = new GraphProxyApi();
String version = "version_example"; // String | The version for the graph api endpoint
try {
    ContentResult result = apiInstance.graphProxyUsersGet(version);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling GraphProxyApi#graphProxyUsersGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **version** | **String**| The version for the graph api endpoint |

### Return type

[**ContentResult**](ContentResult.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

