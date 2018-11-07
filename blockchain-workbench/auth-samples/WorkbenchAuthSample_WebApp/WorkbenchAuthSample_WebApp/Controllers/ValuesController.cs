using Microsoft.IdentityModel.Clients.ActiveDirectory;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace WorkbenchAuthSample_WebApp.Controllers
{
    public class ValuesController : ApiController
    {
        // GET api/values
        public async System.Threading.Tasks.Task GetAsync()
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
        }
    }
}
