# ContractsApi

All URIs are relative to *https://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**contractActionGet**](ContractsApi.md#contractActionGet) | **GET** /api/v1/contracts/{contractId}/actions/{actionId} | 
[**contractActionPost**](ContractsApi.md#contractActionPost) | **POST** /api/v1/contracts/{contractId}/actions | 
[**contractActionsGet**](ContractsApi.md#contractActionsGet) | **GET** /api/v1/contracts/{contractId}/actions | 
[**contractGet**](ContractsApi.md#contractGet) | **GET** /api/v1/contracts/{contractId} | 
[**contractPost**](ContractsApi.md#contractPost) | **POST** /api/v1/contracts | 
[**contractsGet**](ContractsApi.md#contractsGet) | **GET** /api/v1/contracts | 


<a name="contractActionGet"></a>
# **contractActionGet**
> WorkflowStateTransition contractActionGet(contractId, actionId)



Gets the action matching the specified action ID. Users get the action if the user can take the action              given the current state of the specified smart contract instance and the user&#39;s associated application role or smart              contract instance role.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ContractsApi;


ContractsApi apiInstance = new ContractsApi();
Integer contractId = 56; // Integer | The id of the contract
Integer actionId = 56; // Integer | The id of the action
try {
    WorkflowStateTransition result = apiInstance.contractActionGet(contractId, actionId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ContractsApi#contractActionGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **contractId** | **Integer**| The id of the contract |
 **actionId** | **Integer**| The id of the action |

### Return type

[**WorkflowStateTransition**](WorkflowStateTransition.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="contractActionPost"></a>
# **contractActionPost**
> LedgerActionOutput contractActionPost(contractId, actionInformation)



Executes an action for the specified smart contract instance and action ID. Users are only able to execute              the action given the current state of the specified smart contract instance and the user&#39;s associated application role              or smart contract instance role.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ContractsApi;


ContractsApi apiInstance = new ContractsApi();
Integer contractId = 56; // Integer | The id of the workflow instance
WorkflowActionInput actionInformation = new WorkflowActionInput(); // WorkflowActionInput | Parameters for a particular action
try {
    LedgerActionOutput result = apiInstance.contractActionPost(contractId, actionInformation);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ContractsApi#contractActionPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **contractId** | **Integer**| The id of the workflow instance |
 **actionInformation** | [**WorkflowActionInput**](WorkflowActionInput.md)| Parameters for a particular action | [optional]

### Return type

[**LedgerActionOutput**](LedgerActionOutput.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

<a name="contractActionsGet"></a>
# **contractActionsGet**
> WorkflowStateTransitionList contractActionsGet(contractId, top, skip)



Lists all actions, which can be taken by the given user and current state of the specified smart contract              instance. Users get all applicable actions if the user has an associated application role or is associated with a smart              contract instance role for the current state of the specified smart contract instance.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ContractsApi;


ContractsApi apiInstance = new ContractsApi();
Integer contractId = 56; // Integer | The id of the contract
Integer top = 56; // Integer | The maximum number of items to return
Integer skip = 56; // Integer | The number of items to skip before returning
try {
    WorkflowStateTransitionList result = apiInstance.contractActionsGet(contractId, top, skip);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ContractsApi#contractActionsGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **contractId** | **Integer**| The id of the contract |
 **top** | **Integer**| The maximum number of items to return | [optional]
 **skip** | **Integer**| The number of items to skip before returning | [optional]

### Return type

[**WorkflowStateTransitionList**](WorkflowStateTransitionList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="contractGet"></a>
# **contractGet**
> Contract contractGet(contractId)



Creates and deploys a new smart contract instance by adding the instance to the Workbench database and              sending a transaction to the blockchain. This method can only be performed by users who are specified within the              Initiators collection of the workflow within the Workbench application configuration.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ContractsApi;


ContractsApi apiInstance = new ContractsApi();
Integer contractId = 56; // Integer | The id of the contract
try {
    Contract result = apiInstance.contractGet(contractId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ContractsApi#contractGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **contractId** | **Integer**| The id of the contract |

### Return type

[**Contract**](Contract.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="contractPost"></a>
# **contractPost**
> WorkflowActionInput contractPost(workflowActionInput, workflowId, contractCodeId, connectionId)



Gets the smart contract instance matching a specific workflow instance ID. Users who are Workbench              administrators get the smart contract instance. Non-Workbench administrators get the smart contract instance              if they have at least one associated application role or is associated with the smart contract instance.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ContractsApi;


ContractsApi apiInstance = new ContractsApi();
WorkflowActionInput workflowActionInput = new WorkflowActionInput(); // WorkflowActionInput | The set of all contract action parameters.
Integer workflowId = 56; // Integer | The ID of the workflow.
Integer contractCodeId = 56; // Integer | The ID of the ledger implementation.
Integer connectionId = 56; // Integer | The ID of chain instance running on the ledger.
try {
    WorkflowActionInput result = apiInstance.contractPost(workflowActionInput, workflowId, contractCodeId, connectionId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ContractsApi#contractPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **workflowActionInput** | [**WorkflowActionInput**](WorkflowActionInput.md)| The set of all contract action parameters. | [optional]
 **workflowId** | **Integer**| The ID of the workflow. | [optional]
 **contractCodeId** | **Integer**| The ID of the ledger implementation. | [optional]
 **connectionId** | **Integer**| The ID of chain instance running on the ledger. | [optional]

### Return type

[**WorkflowActionInput**](WorkflowActionInput.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

<a name="contractsGet"></a>
# **contractsGet**
> ContractList contractsGet(top, skip, workflowId)



Lists the smart contract instances of the specified workflow. Users who are Workbench administrators get all              smart contract instances. Non-Workbench administrators get all smart contract instances for which they have at least              one associated application role or is associated with a smart contract instance role.

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.ContractsApi;


ContractsApi apiInstance = new ContractsApi();
Integer top = 56; // Integer | The maximum number of items to return
Integer skip = 56; // Integer | The number of items to skip before returning
Integer workflowId = 56; // Integer | The ID of the associated workflow
try {
    ContractList result = apiInstance.contractsGet(top, skip, workflowId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling ContractsApi#contractsGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **top** | **Integer**| The maximum number of items to return | [optional]
 **skip** | **Integer**| The number of items to skip before returning | [optional]
 **workflowId** | **Integer**| The ID of the associated workflow | [optional]

### Return type

[**ContractList**](ContractList.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

