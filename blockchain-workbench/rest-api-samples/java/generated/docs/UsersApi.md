# UsersApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**meGet**](UsersApi.md#meGet) | **GET** /api/v1/users/me | 
[**userDelete**](UsersApi.md#userDelete) | **DELETE** /api/v1/users/{userID} | 
[**userGet**](UsersApi.md#userGet) | **GET** /api/v1/users/{userID} | 
[**usersGet**](UsersApi.md#usersGet) | **GET** /api/v1/users | Get Users
[**usersPost**](UsersApi.md#usersPost) | **POST** /api/v1/users | 


<a name="meGet"></a>
# **meGet**
> Me meGet()



Returns the current user

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.UsersApi;


UsersApi apiInstance = new UsersApi();
try {
    Me result = apiInstance.meGet();
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UsersApi#meGet");
    e.printStackTrace();
}
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

<a name="userDelete"></a>
# **userDelete**
> userDelete(userID)



Deletes the specified user. This method can only be performed by users who are Workbench administrators.              NOTE: Not currently implemented.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.UsersApi;


UsersApi apiInstance = new UsersApi();
String userID = "userID_example"; // String | The id of the user
try {
    apiInstance.userDelete(userID);
} catch (ApiException e) {
    System.err.println("Exception when calling UsersApi#userDelete");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userID** | **String**| The id of the user |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="userGet"></a>
# **userGet**
> User userGet(userID)



Gets the user matching a specific user ID.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.UsersApi;


UsersApi apiInstance = new UsersApi();
Integer userID = 56; // Integer | The id of the user
try {
    User result = apiInstance.userGet(userID);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UsersApi#userGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userID** | **Integer**| The id of the user |

### Return type

[**User**](User.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="usersGet"></a>
# **usersGet**
> UserList usersGet(top, skip, externalId, userChainIdentifier)

Get Users

Lists all users within the connected blockchain consortium.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.UsersApi;


UsersApi apiInstance = new UsersApi();
Integer top = 56; // Integer | The maximum number of items to return
Integer skip = 56; // Integer | The number of items to skip before returning
String externalId = "externalId_example"; // String | The external ID of the user to query for
String userChainIdentifier = "userChainIdentifier_example"; // String | The on-chain address of the user to query for
try {
    UserList result = apiInstance.usersGet(top, skip, externalId, userChainIdentifier);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UsersApi#usersGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **top** | **Integer**| The maximum number of items to return | [optional]
 **skip** | **Integer**| The number of items to skip before returning | [optional]
 **externalId** | **String**| The external ID of the user to query for | [optional]
 **userChainIdentifier** | **String**| The on-chain address of the user to query for | [optional]

### Return type

[**UserList**](UserList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="usersPost"></a>
# **usersPost**
> Integer usersPost(userInput)



Adds a user to the blockchain consortium. This method can only be performed by users who are              Workbench administrators.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.UsersApi;


UsersApi apiInstance = new UsersApi();
UserInput userInput = new UserInput(); // UserInput | New user to add
try {
    Integer result = apiInstance.usersPost(userInput);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UsersApi#usersPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userInput** | [**UserInput**](UserInput.md)| New user to add | [optional]

### Return type

**Integer**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json-patch+json, application/json, text/json, application/_*+json
 - **Accept**: Not defined

