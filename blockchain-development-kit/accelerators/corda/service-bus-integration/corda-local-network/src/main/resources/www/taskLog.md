# Task Log

## {{networkName}}

<div class="pre">
<div class="logline">ExecId&nbsp&nbsp&nbspTaskId&nbsp&nbsp&nbspTimestamp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbspMessage</div>
<div class="logline">-------- -------- ---------------------------- ------------------------------</div>
{{#history}}
<div class="logline"><span style="color:#{{executionColour}}">{{executionId}}</span> {{taskId}} {{timestamp}} {{message}}</div>
{{/history}}
</div>

<style>
/* copied from <pre> styling */
div.logline { 
    font-family: Monaco, "Bitstream Vera Sans Mono", "Lucida Console", Terminal, monospace;
    font-size: 13px;
    color: #333;
    font-weight: 400;
    line-height: 1.4;
}

div.pre {
    padding: 20px;
    overflow: auto;
    text-shadow: none;
    background: #fff;
    border: solid 1px #f2f2f2;
}
</style>

