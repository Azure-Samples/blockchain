#r "Newtonsoft.Json"

using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Web;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Primitives;
using Newtonsoft.Json;
using System.Text;

static Dictionary<string, Listing> listings = new Dictionary<string, Listing>();
static HttpClient httpClient = new HttpClient();

public class Listing {
    public string Description { get; set;}
    public double ListingAmount { get; set;}
}

public static async Task<IActionResult> Run(HttpRequest req, ILogger log)
{
    log.LogInformation("C# HTTP trigger function processed a request.");

    string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
    log.LogInformation(requestBody);

    var values = HttpUtility.ParseQueryString(requestBody);
 
    string logicAppUrl = "https://prod-16.westus2.logic.azure.com:443/workflows/32a5f3742eed41c48d488120ec53b3b9/triggers/manual/paths/invoke?api-version=2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=l4-aaZ2jyztJwdbN7lbXiworFNEciXXXDgSQEcUh_Bg";
    string id =  values["From"];
    log.LogInformation("Msg from - " + id);
    string input = values["Body"];

    if(!listings.ContainsKey(id)) {
        var newListing = new Listing();

        listings.Add(id, newListing);

        var message = BuildResponse("What would you like to sell?");
        return new ContentResult { Content = message, ContentType = "application/xml" };
    } else {
       var listing = listings[id];
       
       if(string.IsNullOrEmpty(listing.Description)) {
           listing.Description = input;

           var message = BuildResponse(string.Format("How much do you want to sell {0} for?", listing.Description));
           return new ContentResult { Content = message, ContentType = "application/xml" };
       } else {
           double val;
           if(double.TryParse(input, out val)) {
               listing.ListingAmount = val;

               var message = BuildResponse(string.Format("{0} has been listed for ${1}.", listing.Description, listing.ListingAmount) );
               log.LogInformation(JsonConvert.SerializeObject(listing));
               
               var buffer = Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(listing));
               var byteContent = new ByteArrayContent(buffer);
               byteContent.Headers.ContentType = new MediaTypeHeaderValue("application/json");
       
               var response = await httpClient.PostAsync(logicAppUrl, byteContent);

               listings.Remove(id);
               return new ContentResult { Content = message, ContentType = "application/xml" };

           } else {
              var message = BuildResponse(string.Format("Invalid amount. How much do you want to sell {0} for?", listing.Description) );
              return new ContentResult { Content = message, ContentType = "application/xml" };
           }        
       }
    }
}

public static string BuildResponse(string message) {

    return string.Format("<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
        "<Response>" +
        "<Message>{0}</Message>" +
        "</Response>", message);
    
}
