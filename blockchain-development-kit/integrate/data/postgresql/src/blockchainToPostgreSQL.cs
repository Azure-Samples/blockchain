using System;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Extensions.Http;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Npgsql;
using Newtonsoft.Json;

namespace EthereumLogicApp.Insert
{
    public static class blockchainToPostgreSql
    {
        [FunctionName("blockchainToPostgreSql")]
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


            string Server = config["POSTGRESQL_DB_SERVER"];
            string Database = config["POSTGRESQL_DB_DATABASE"];
            string UserID = config["POSTGRESQL_DB_USERID"];
            string Password = config["POSTGRESQL_DB_PWD"];
            string Port = config["POSTGRESQL_DB_PORT"];

            string connString =
                String.Format(
                    "Server={0}; User Id={1}; Database={2}; Port={3}; Password={4}; SSL Mode=Prefer; Trust Server Certificate=true",
                    Server,
                    UserID,
                    Database,
                    Port,
                    Password);

            using (var conn = new NpgsqlConnection(connString))
            {
                log.LogInformation("Trying to open connection");
                conn.Open();             
                log.LogInformation("Opened connection");

                NpgsqlCommand command = new NpgsqlCommand();
                command.Connection = conn; 

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
                
                log.LogInformation("wrote rows");

            }

             return name != null
                ? (ActionResult)new OkObjectResult($"Hello, {name}")
                : new BadRequestObjectResult("Please pass a name on the query string or in the request body");
        }
    }
}
