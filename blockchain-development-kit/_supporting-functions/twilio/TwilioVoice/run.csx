#r "Newtonsoft.Json"

using System.Net;
using System.Web;
using System.Net.Http.Headers;
using System.Net.Http;
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
    var values = HttpUtility.ParseQueryString(requestBody);
 
    string logicAppUrl = "https://prod-17.westus2.logic.azure.com:443/workflows/288574b6da704b38aa107be9651613d9/triggers/manual/paths/invoke?api-version=2016-10-01&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=_KCSbn6Pbj-m40qtu4T56oOpKCqPtNnNt1mF8lGAzgY";
    string id = values["Caller"];
    log.LogInformation("Caller - " + id);

    if(!listings.ContainsKey(id)) {
        var newListing = new Listing();

        listings.Add(id, newListing);

        var message = BuildGatherResponse("What would you like to sell?", "speech");
        return new ContentResult { Content = message, ContentType = "application/xml" };
    } else {
       var listing = listings[id];
       
       if(string.IsNullOrEmpty(listing.Description)) {
           listing.Description = values["SpeechResult"];

           var message = BuildGatherResponse(string.Format("Enter in how much you want to sell {0} for", listing.Description), "dtmf" );
           return new ContentResult { Content = message, ContentType = "application/xml" };
       } else {
           double val;
           if(double.TryParse(values["Digits"], out val)) {
               listing.ListingAmount = val;

               var buffer = Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(listing));
               var byteContent = new ByteArrayContent(buffer);
               byteContent.Headers.ContentType = new MediaTypeHeaderValue("application/json");
       
               var response = await httpClient.PostAsync(logicAppUrl, byteContent);

               var message = BuildHangupResponse(string.Format("{0} has been listed for {1} dollars.", listing.Description, listing.ListingAmount) );
               listings.Remove(id);

               return new ContentResult { Content = message, ContentType = "application/xml" };

           } else {
              var message = BuildGatherResponse(string.Format("Invalid amount. Enter in how much you want to sell {0} for", listing.Description), "dtmf" );
              return new ContentResult { Content = message, ContentType = "application/xml" };
           }        
       }
    }
}

public static string BuildGatherResponse(string message, string messageType) {

    return string.Format("<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
        "<Response>" +
        "<Gather input=\"{1}\" method=\"post\" timeout=\"3\">" +
        "<Say>{0}</Say>" +
        "</Gather>" +
        "</Response>", message, messageType);
    
}

public static string BuildHangupResponse(string message) {
    return string.Format("<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
        "<Response>" +
        "<Say>{0}</Say>" +
        "<Say>Goodbye</Say>" +
        "<Hangup/>" +
        "</Response>", message);
    
}