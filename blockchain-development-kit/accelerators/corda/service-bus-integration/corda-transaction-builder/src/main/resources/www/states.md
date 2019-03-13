<script type="text/javascript">

$(document).ready(function(){


// pick an existing uniqueidentifier
$("div.formfield  a").click(function(){ 
  //alert("hh");
  var id = $(this).data("id")
  //alert(id);
  
  //  :-( 
  var $parent =  $(this).parent().parent().parent();   
 
  $parent.find('input[name="id"]').val(id);        
});

});
</script>



# [{{networkName}}](/web/networks/{{networkName}}/nodes)

## {{stateName}} 

Explore data on node **{{nodeName}}**. 



[All](/web/networks/{{networkName}}/nodes/{{nodeName}}/apps/{{appName}}/states/{{stateName}}/all)


<form action="/web/networks/{{networkName}}/nodes/{{nodeName}}/apps/{{appName}}/states/{{stateName}}/query" method="GET">

<div class="formfield"><label for="id" >Linear Id</label>: <input name="id" class="">
  <ul>
   {{#idLookup}}
        <li><a data-id="{{first}}" href="#" class="button blueButton">#{{second}}</a></li>
        {{/idLookup}}
  </ul>      

<input type="submit" value="View"></input>
</div>

</form>

<style>

div.formfield ul {
    display: inline;
    list-style-type: none;
    margin: 0;
    padding: 0;
}
              
div.formfield li {
  display: inline;               
}      

form {
  backgound-color : #fafafa;
}

</style>
