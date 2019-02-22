using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using MySql.Data.MySqlClient;
using Newtonsoft.Json;

namespace EthereumLogicApp.Insert
{
    public static class blockchainToMySQL
    {
        [FunctionName("blockchainToMySQL")]
        public static async Task<IActionResult> Run(
            [HttpTrigger(AuthorizationLevel.Anonymous, "get", "post", Route = null)] HttpRequest req,
            ILogger log, ExecutionContext context)
        {
            log.LogInformation("C# HTTP trigger function processed a request.");

            string name = "Chris";

            string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
            dynamic data = JsonConvert.DeserializeObject(requestBody);

            string _previouscounterparty = Convert.ToString(data.PreviousCounterparty); 
            string _supplychainobserver = Convert.ToString(data.SupplyChainObserver);  
            string _counterparty = Convert.ToString(data.Counterparty);  
            string _supplychainowner = Convert.ToString(data.SupplyChainOwner);  
            string _initiatingcounteraparty = Convert.ToString(data.InitiatingCounterparty);  
            int _state = data.State; 

            log.LogInformation("Processed through request body.");

           var config = new ConfigurationBuilder()
            .SetBasePath(context.FunctionAppDirectory)
            .AddJsonFile("local.settings.json", optional: true, reloadOnChange: true)
            .AddEnvironmentVariables()
            .Build();    

           var builder = new MySqlConnectionStringBuilder
            {
                Server = config["MYSQL_DB_SERVER"],
                Database = config["MYSQL_DB_DATABASE"],
                UserID = config["MYSQL_DB_USERID"],
                Password = config["MYSQL_DB_PWD"],
                SslMode = MySqlSslMode.None,
            };

            using (var conn = new MySqlConnection(builder.ConnectionString))
            {
                log.LogInformation("Trying to open connection");
                await conn.OpenAsync();

                log.LogInformation("Opened connection");
                using (var command = conn.CreateCommand())
                {
                    //command.CommandText = "DROP TABLE IF EXISTS contractaction;";
                    //await command.ExecuteNonQueryAsync();
                    // Console.WriteLine("Finished dropping table (if existed)");

                    //command.CommandText = "CREATE TABLE contractaction (id serial PRIMARY KEY, previouscounterparty VARCHAR(50), supplychainobserver VARCHAR(50), counterparty VARCHAR(50), supplychainowner VARCHAR(50), initiatingcounteraparty VARCHAR(50), state INTEGER);";
                    //await command.ExecuteNonQueryAsync();
                    //Console.WriteLine("Finished creating table");

                    command.CommandText = @"INSERT INTO contractaction (previouscounterparty, supplychainobserver, counterparty, 
                    supplychainowner, initiatingcounteraparty, state) VALUES (@_previouscounterparty, @_supplychainobserver, 
                    @_counterparty, @_supplychainowner, @_initiatingcounteraparty, @_state);";

                    command.Parameters.AddWithValue("@_previouscounterparty", _previouscounterparty);
                    command.Parameters.AddWithValue("@_supplychainobserver", _supplychainobserver);
                    command.Parameters.AddWithValue("@_counterparty", _counterparty);
                    command.Parameters.AddWithValue("@_supplychainowner", _supplychainowner);
                    command.Parameters.AddWithValue("@_initiatingcounteraparty", _initiatingcounteraparty);
                    command.Parameters.AddWithValue("@_state", _state);

                    int rowCount = await command.ExecuteNonQueryAsync();
                }

            }
        
             return name != null
                ? (ActionResult)new OkObjectResult($"Hello, {name}")
                : new BadRequestObjectResult("Please pass a name on the query string or in the request body");
        }
    }
}
