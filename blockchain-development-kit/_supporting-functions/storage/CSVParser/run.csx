#r "Newtonsoft.Json"

using System.Net;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Primitives;
using Newtonsoft.Json;
using System.Dynamic;
using System.Collections;
using System.Text.RegularExpressions;

public static async Task<IActionResult> Run(HttpRequest req, ILogger log)
{
    log.LogInformation("C# HTTP trigger function processed a request.");

    string requestBody = await new StreamReader(req.Body).ReadToEndAsync();

    var regex = new Regex("(?<=^|,)(\"(?:[^\"]|\"\")*\"|[^,]*)");

    var entries = requestBody.Split(new string[] { "\r\n" }, StringSplitOptions.RemoveEmptyEntries);
    
    dynamic data = new ExpandoObject();
    data.Rows = new List<ExpandoObject>();

    var properties = entries[0].Split(new char[] { ',' });
    
    for (int row = 1; row < entries.Count(); row++)
    {
        var rowData = new ExpandoObject();
        var rowDictionary = (IDictionary<string, object>)rowData;

        int fieldIndex = 0;

        foreach (Match m in regex.Matches(entries[row]))
        {
            rowDictionary.Add(properties[fieldIndex], m.Value);
            fieldIndex++;
        }

        data.Rows.Add(rowData);
    }

    var serialized = JsonConvert.SerializeObject(data);
    return (ActionResult)new OkObjectResult(serialized);
}
