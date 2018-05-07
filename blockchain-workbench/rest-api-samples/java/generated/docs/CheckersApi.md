# CheckersApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**checkApplicationPost**](CheckersApi.md#checkApplicationPost) | **POST** /api/v1/checkers/checkApplication | Check validity of application configuration for Workbench
[**checkContractCodePost**](CheckersApi.md#checkContractCodePost) | **POST** /api/v1/checkers/checkContractCode | Check validity of application ledger implementation for Workbench


<a name="checkApplicationPost"></a>
# **checkApplicationPost**
> Integer checkApplicationPost(file)

Check validity of application configuration for Workbench

Checks a configuration file

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CheckersApi;


CheckersApi apiInstance = new CheckersApi();
File file = new File("/path/to/file.txt"); // File | Upload File
try {
    Integer result = apiInstance.checkApplicationPost(file);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CheckersApi#checkApplicationPost");
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

<a name="checkContractCodePost"></a>
# **checkContractCodePost**
> Integer checkContractCodePost(ledgerId)

Check validity of application ledger implementation for Workbench

Checks a ledger implementation file against configuration file

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.CheckersApi;


CheckersApi apiInstance = new CheckersApi();
Integer ledgerId = 56; // Integer | The input chain type id
try {
    Integer result = apiInstance.checkContractCodePost(ledgerId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling CheckersApi#checkContractCodePost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **ledgerId** | **Integer**| The input chain type id | [optional]

### Return type

**Integer**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

