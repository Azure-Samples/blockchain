using Microsoft.IdentityModel.Clients.ActiveDirectory;
using System;
using System.Net.Http;
using System.Threading.Tasks;
using System.Net.Http.Headers;

namespace WorkbenchAuthSample_ConsoleApp
{
    class Program
    {
        static async Task Main(string[] args)
        {
            AuthenticationResult result = null;
            AuthenticationContext authenticationContext = new AuthenticationContext("https://login.microsoftonline.com/{Tenant-ID}");
            ClientCredential clientCredential = new ClientCredential("{Client-Application-ID}", "Client-Secret}");
            try
            {
                result = await authenticationContext.AcquireTokenAsync("{Workbench-API-Application-ID}", clientCredential).ConfigureAwait(false);
            }
            catch (Exception e)
            {
                Console.WriteLine("{0}", e);
            }
            var token = result.AccessToken; // Use this token to make API calls to workbench

            // Sample API Call
            HttpClient client = new HttpClient();
            HttpResponseMessage response = null;
            try
            {
                client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", token);
                response = await client.GetAsync("{workbench-api-url}/api/v1/users");
            }
            catch (Exception e)
            {
                Console.WriteLine("{0}", e);
            }

            Console.WriteLine("{0}", response.Content);
        }
    }
}
