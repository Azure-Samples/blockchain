<script type="text/javascript">

$(document).ready(function(){
$("#123").click(function(){
    //$(this).hide();
    alert("Jquery is working");
  });
});


(function poll() {
console.log("running poll");
    setTimeout(function() {
        $.ajax({
            url: "/web/ajax/test",
            type: "GET",
            success: function(data) {
            console.log(data);
                console.log("polling");
            },
            //dataType: "json",
            complete: poll,
            timeout: 2000
        })
    }, 5000);
})();

</script>



# [{{networkName}}](/web/networks/{{networkName}})

{{#isRunning}}
This network is running. [Stop All Nodes](/web/networks/{{networkName}}/stop)
{{/isRunning}}
{{^isRunning}}
This network is not running. [Start All Nodes](/web/networks/{{networkName}}/start)
{{/isRunning}}

## Nodes

{{#nodesStatus}}
### {{name}}
socketTest <span class="{{socketTest}}">{{socketTest}}</span></br>
sshConnectionTest <span class="{{sshConnectionTest}}">{{sshConnectionTest}}</span></br>
[Full Status](/web/networks/{{networkName}}/nodes/{{name}}/status)
{{/nodesStatus}}

<!--<span id="123">click me</span>-->

