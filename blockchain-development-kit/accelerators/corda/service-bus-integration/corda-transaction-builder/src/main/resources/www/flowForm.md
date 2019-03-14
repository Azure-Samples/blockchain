<script type="text/javascript">

$(document).ready(function(){

var html = `
 <span class="pickName">
    <ul>
        {{#idLookup}}
        <li><a data-id="{{first}}" href="#" class="button blueButton">#{{second}}</a></li>
        {{/idLookup}}
    </ul>
 </span>&nbsp;
 <a href="#" class="new button redButton">new</a>&nbsp;
 <span class="inputName hidden"><label>Name</label><input class="name" type="text"/></span><a href="#" class="save button redButton">save</a>
`;


// add extra feaures based on type
$("input.UniqueIdentifier").after(html); 

//$("input.String").after( "<span>this is a string!!</span>" );

// create new uniqueidentifier
$("div.formfield > a.new").click(function(){ 
   var $parent =  $(this).parent();
   $.ajax({url: "/web/uniqueidentifier/create", success: function(result){
           $parent.find("input").val(result);         
        }});                    
});


// save the value
$("div.formfield > a.save").click(function(){ 
   var $parent =  $(this).parent();
   if ($parent.find(".inputName").hasClass("hidden")) {
       $parent.find(".inputName").removeClass("hidden");
   }
   else {
     // do some ajax
     var id = $parent.find("input").first().val();
     var name = $parent.find("input.name").first().val();
     
     var theUrl = "/web/uniqueidentifier/save?id=" + id + "&name=" + name;
    
     $.ajax({url: theUrl, success: function(result){
          //      alert(result);        
             }});
     // update view
     $parent.find(".inputName").addClass("hidden");
     $parent.find("li").last().after('<a data-id="' + id + '" href="#" class="button blueButton">' + name + '</a>');
   }
});


// pick an existing uniqueidentifier
$("div.formfield > span.pickName a").click(function(){ 
  var id = $(this).data("id")
  
  //  :-( 
  var $parent =  $(this).parent().parent().parent().parent();   
 
  $parent.find("input").val(id);        
});

});
</script>

<style>
.hidden {
  display:none;
}
span.pickName ul {
    display: inline;
    list-style-type: none;
    margin: 0;
    padding: 0;
}
              
span.pickName li {
  display: inline;               
}      


</style>


# {{networkName}}

You are talking to **{{nodeName}}**. Explore [{{nodeName}}](/web/networks/{{networkName}}/nodes/{{nodeName}}). Switch [node](/web/networks/{{networkName}}/nodes)

Run flow {{flowName}} 

{{#hasDescription}}
<div>{{description}}</div>
{{/hasDescription}}


<form action="run" method="POST">

{{#metadata}}
<div class="formfield"><label for="{{key}}" >{{key}}</label>: <input name="{{key}}" class="{{#value}}{{type}}{{/value}}"></div>
{{/metadata}}

<input type="submit" value="Run Flow"></input>

</form>

