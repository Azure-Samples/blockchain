
# {{networkName}}

## Nodes

{{#nodes}}
* [{{organisation}}](/web/networks/{{networkName}}/nodes/{{organisation}}/status)
{{/nodes}}

## CorDapps

{{#cordapps}}
* <strong>{{name}}</strong> (deployed at {{deployedAt}} 
with length {{size}} bytes and MD5 hash of {{md5Hash}}). [Download](/web/networks/{{networkName}}/cordapps/{{name}}/download)
{{/cordapps}}


## Actions

* [Status](/web/networks/{{networkName}}/status)
* [Start All Nodes](/web/networks/{{networkName}}/start)
* [Stop All Nodes](/web/networks/{{networkName}}/stop)
* [Deploy a CorDapp](/web/networks/{{networkName}}/deploy)
* [Task History](/web/networks/{{networkName}}/tasks/history)

