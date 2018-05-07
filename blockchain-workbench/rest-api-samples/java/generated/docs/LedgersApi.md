# LedgersApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**ledgersGet**](LedgersApi.md#ledgersGet) | **GET** /api/v1/ledgers | 


<a name="ledgersGet"></a>
# **ledgersGet**
> LedgerList ledgersGet(top, skip)



Lists the supported blockchain types, such as Ethereum or Hyperledger Fabric.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.LedgersApi;


LedgersApi apiInstance = new LedgersApi();
Integer top = 56; // Integer | The maximum number of items to return
Integer skip = 56; // Integer | The number of items to skip before returning
try {
    LedgerList result = apiInstance.ledgersGet(top, skip);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling LedgersApi#ledgersGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **top** | **Integer**| The maximum number of items to return | [optional]
 **skip** | **Integer**| The number of items to skip before returning | [optional]

### Return type

[**LedgerList**](LedgerList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

