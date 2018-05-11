# ConnectionsApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**blockGet**](ConnectionsApi.md#blockGet) | **GET** /api/v1/ledgers/connections/{connectionId}/blocks/{blockId} | 
[**blocksGet**](ConnectionsApi.md#blocksGet) | **GET** /api/v1/ledgers/connections/{connectionId}/blocks | 
[**connectionGet**](ConnectionsApi.md#connectionGet) | **GET** /api/v1/ledgers/connections/{connectionId} | 
[**connectionsGet**](ConnectionsApi.md#connectionsGet) | **GET** /api/v1/ledgers/connections | 
[**transactionGet**](ConnectionsApi.md#transactionGet) | **GET** /api/v1/ledgers/connections/{connectionId}/transactions/{transactionId} | 
[**transactionsGet**](ConnectionsApi.md#transactionsGet) | **GET** /api/v1/ledgers/connections/{connectionId}/transactions | 


<a name="blockGet"></a>
# **blockGet**
> Block blockGet(connectionId, blockId)



Gets the block matching a specific block ID.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ConnectionsApi;


ConnectionsApi apiInstance = new ConnectionsApi();
Integer connectionId = 56; // Integer | The connectionId of the block
Integer blockId = 56; // Integer | The id of the block
try {
    Block result = apiInstance.blockGet(connectionId, blockId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ConnectionsApi#blockGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connectionId** | **Integer**| The connectionId of the block |
 **blockId** | **Integer**| The id of the block |

### Return type

[**Block**](Block.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="blocksGet"></a>
# **blocksGet**
> BlockList blocksGet(connectionID, top, skip)



Lists the blocks for a connected blockchain network.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ConnectionsApi;


ConnectionsApi apiInstance = new ConnectionsApi();
Integer connectionID = 56; // Integer | The id of the connection
Integer top = 56; // Integer | The maximum number of items to return
Integer skip = 56; // Integer | The number of items to skip before returning
try {
    BlockList result = apiInstance.blocksGet(connectionID, top, skip);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ConnectionsApi#blocksGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connectionID** | **Integer**| The id of the connection |
 **top** | **Integer**| The maximum number of items to return | [optional]
 **skip** | **Integer**| The number of items to skip before returning | [optional]

### Return type

[**BlockList**](BlockList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="connectionGet"></a>
# **connectionGet**
> Connection connectionGet(connectionID)



Gets the connected blockchain network matching a specific chain instance ID.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ConnectionsApi;


ConnectionsApi apiInstance = new ConnectionsApi();
Integer connectionID = 56; // Integer | The id of the connection
try {
    Connection result = apiInstance.connectionGet(connectionID);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ConnectionsApi#connectionGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connectionID** | **Integer**| The id of the connection |

### Return type

[**Connection**](Connection.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="connectionsGet"></a>
# **connectionsGet**
> ConnectionList connectionsGet(top, skip)



Lists the connected blockchain networks.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ConnectionsApi;


ConnectionsApi apiInstance = new ConnectionsApi();
Integer top = 56; // Integer | The maximum number of items to return
Integer skip = 56; // Integer | The number of items to skip before returning
try {
    ConnectionList result = apiInstance.connectionsGet(top, skip);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ConnectionsApi#connectionsGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **top** | **Integer**| The maximum number of items to return | [optional]
 **skip** | **Integer**| The number of items to skip before returning | [optional]

### Return type

[**ConnectionList**](ConnectionList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="transactionGet"></a>
# **transactionGet**
> Transaction transactionGet(connectionId, transactionId)



Gets the transaction matching a specific transaction ID.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ConnectionsApi;


ConnectionsApi apiInstance = new ConnectionsApi();
Integer connectionId = 56; // Integer | The connectionId of the transaction
Integer transactionId = 56; // Integer | The id of the transaction
try {
    Transaction result = apiInstance.transactionGet(connectionId, transactionId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ConnectionsApi#transactionGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connectionId** | **Integer**| The connectionId of the transaction |
 **transactionId** | **Integer**| The id of the transaction |

### Return type

[**Transaction**](Transaction.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="transactionsGet"></a>
# **transactionsGet**
> List&lt;TransactionList&gt; transactionsGet(connectionId, top, skip)



Lists the transactions for a connected blockchain network.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ConnectionsApi;


ConnectionsApi apiInstance = new ConnectionsApi();
Integer connectionId = 56; // Integer | The id of the connection
Integer top = 56; // Integer | The maximum number of items to return
Integer skip = 56; // Integer | The number of items to skip before returning
try {
    List<TransactionList> result = apiInstance.transactionsGet(connectionId, top, skip);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ConnectionsApi#transactionsGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **connectionId** | **Integer**| The id of the connection |
 **top** | **Integer**| The maximum number of items to return | [optional]
 **skip** | **Integer**| The number of items to skip before returning | [optional]

### Return type

[**List&lt;TransactionList&gt;**](TransactionList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

