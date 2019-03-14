# {{networkName}}

Successfully joined the network.

## Available CorDapps

The following apps were found and downloaded

{{#apps}}
* {{name}} - (MD5 hash is {{md5Hash}})
{{/apps}}

To run flows or queries for the apps, first select a node on the network

{{#nodes}}
* [{{.}}](/web/networks/{{networkName}}/nodes/{{.}})
{{/nodes}}


