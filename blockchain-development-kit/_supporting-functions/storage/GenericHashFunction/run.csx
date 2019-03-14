#r "Newtonsoft.Json"

using System.Net;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Primitives;
using Newtonsoft.Json;
using System.Security.Cryptography;
using System.Text;

public static async Task<IActionResult> Run(HttpRequest req, ILogger log)
{
    log.LogInformation("C# HTTP trigger function processed a request.");

    string requestBody = await new StreamReader(req.Body).ReadToEndAsync();

    if (string.IsNullOrWhiteSpace(requestBody))
        return new BadRequestObjectResult("File content or metadata failed to be hashed.");

    string contentHash = ComputeSha256Hash(requestBody);
        
    return (ActionResult)new OkObjectResult(contentHash);
}

public static string ComputeSha256Hash(string rawData)  
{  
    // Create a SHA256   
    using (SHA256 sha256Hash = SHA256.Create())  
    {  
        // ComputeHash - returns byte array  
        byte[] bytes = sha256Hash.ComputeHash(Encoding.UTF8.GetBytes(rawData));  

        // Convert byte array to a string   
        StringBuilder builder = new StringBuilder();  
        for (int i = 0; i < bytes.Length; i++)  
        {  
            builder.Append(bytes[i].ToString("x2"));  
        }  
        return builder.ToString();  
    }  
} 