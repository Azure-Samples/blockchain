# API 
[Index](Index.md)

## Overview 

The API follows RESTish conventions in URL patterns but doesn't make full use
of the HTTP verbs (there is nothing to describe 'Start', 'Stop' and so)

All "command" style operations are simple POSTs and will either return 
a 200 or 500 status code. 

200 status codes always return a JSON object with a simple message, e.g.

```json
{ "message" : "network test created"} 
``` 

500 status codes also include a stack trace if available to help identify
the problem, e.g. 

```json
{ "message" : "failed to create test network",
  "stackTrace" :  "Exception in thread \"main\" java.lang.NullPointerException.... "} 
``` 

All "request" style operations are simple GETs and will return in appropriate MIME type 
(typically `application/json`).

To allow support of multiple networks, all requests start with a logical network name 
allocated by the client. By convention the name `default` is assumed. 

## Creating a network 

Build a new set of nodes. They will be created, but not started. Any existing nodes
are deleted (and any state lost)

```bash
curl -X PUT -H "Content-Type: application/json" http://localhost:1114/default/nodes --data \
 '[ "O=Alice,L=New York,C=US" , "O=Bob,L=Paris,C=FR" , "O=Notary,L=London,C=GB" ]'
``` 
  
## Upload a CorDapp 

This loads the app to all nodes, but will *not* restart: 

```bash
curl -X PUT -H "Content-Type: application/octet-stream" http://localhost:1114/default/nodes/app/refrigerated-transportation-0.1.jar \
   --data  @src\test\resources\cordapps\refrigerated-transportation-0.1.jar
```
 
## Start the network

A Corda process will be started for each node. This API call will return 
once the Corda processes are started but they wont be ready to accept request    

```bash
curl -X POST  http://localhost:1114/default/start
```

## Stop the network

The Corda nodes will be stopped, gracefully if possible   

```bash
curl -X POST  http://localhost:1114/default/stop
```

## Delete the network

The Corda nodes will be stopped and the network deleted. All state is lost   

```bash
curl -X POST  http://localhost:1114/default/delete
```

## List all the nodes  

Returns a list of all the nodes in the network as simple organisation name

```bash
curl http://localhost:1114/default/nodes
```

Would return something similar to 

```json
["Alice","Bob","Notary"]
```

## The config for a node   

Returns useful parts of config for a node 

```bash
curl http://localhost:1114/default/nodes/alice/config
```

Would return something similar to 

```json
{"legalName":"O=Alice,L=New York,C=US","port":10001,"sshPort":10004}
```

## The status of a node

Runs a number of status checks over the node and returns the result as a JSON object with 
an entry for the result of each check run.

```bash
curl http://localhost:1114/default/nodes/alice/status
```

Would return something similar to 
```json
{"socket test":"Passed","ssh connection test":"Passed"}
```

## Task history 

This returns the internal log of the commands (Tasks) run. There will be one or more tasks 
for each action. 

```bash
curl http://localhost:1114/default/tasks
```

Would return something similar to:

```json
[{"message":"Starting DeployCordaAppTask","taskId":"f7e0e786-d6ee-49b5-939a-ae79e26dc591","timestamp":1538995566244},
 {"message":"Deploying cordapp refrigerated-transportation-0.1.jar to alice_node","taskId":"f7e0e786-d6ee-49b5-939a-ae79e26dc591","timestamp":1538995566248},
 {"message":"Deploying cordapp refrigerated-transportation-0.1.jar to bob_node","taskId":"f7e0e786-d6ee-49b5-939a-ae79e26dc591","timestamp":1538995566250},
 ...
]
```

## Running Java Process

This returns the internal state of running processes as detected by the JVM. It may not be fully 
accurate (if processes have not shutdown cleanly) but it will usually give a reliable insight 
as to what is running 

```bash
curl  http://localhost:1114/default/processes
```

Would return something similar to:

```json
[{"name":"alice_node","alive":true},{"name":"bob_node","alive":true},{"name":"notary_node","alive":true}]
```

