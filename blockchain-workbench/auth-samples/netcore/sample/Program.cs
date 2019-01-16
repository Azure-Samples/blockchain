namespace sample
{
    using System;
    using System.Net.Http;
    using System.Threading.Tasks;
    using System.Net.Http.Headers;
    using Microsoft.IdentityModel.Clients.ActiveDirectory;

    class Program
    {
        public static readonly string AUTHORITY = "https://login.microsoftonline.com/<tenant_name>";
        public static readonly string WORKBENCH_API_URL = "<Workbench API URL>";
        public static readonly string RESOURCE = "<Workbench AppId>";
        public static readonly string CLIENT_APP_Id = "<service principal AppId>";
        public static readonly string CLIENT_SECRET = "<service principal secret>";

        static async Task Main(string[] args)
        {
            AuthenticationContext authenticationContext = new AuthenticationContext(AUTHORITY);
            ClientCredential clientCredential = new ClientCredential(CLIENT_APP_Id, CLIENT_SECRET);

            // Sample API Call
            try
            {
                // Getting the token, it is recommended to call AcquireTokenAsync before every Workbench API call
                // The library takes care of refreshing the token when it expires
                var result = await authenticationContext.AcquireTokenAsync(RESOURCE, clientCredential).ConfigureAwait(false);

                // Using token to call Workbench's API
                HttpClient client = new HttpClient();
                client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", result.AccessToken);

                // Get Users
                var response = await client.GetAsync($"{WORKBENCH_API_URL}/api/v1/users");
                var users = await response.Content.ReadAsStringAsync();

                Console.WriteLine(users);
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
            }
        }
    }
}
