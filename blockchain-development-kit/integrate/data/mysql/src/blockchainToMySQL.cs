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

            string requestBody = await new StreamReader(req.Body).ReadToEndAsync();
            dynamic data = JsonConvert.DeserializeObject(requestBody);

            string _previouscounterparty = Convert.ToString(data.PreviousCounterparty); 
            string _supplychainobserver = Convert.ToString(data.SupplyChainObserver);  
            string _counterparty = Convert.ToString(data.Counterparty);  
            string _supplychainowner = Convert.ToString(data.SupplyChainOwner);  
            string _initiatingcounteraparty = Convert.ToString(data.InitiatingCounterparty);  
            int _state = data.State; 

            log.LogInformation("Processed request body.");

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

            int rowCount;

            using (var conn = new MySqlConnection(builder.ConnectionString))
            {
                await conn.OpenAsync();
                log.LogInformation("Opened connection");

                using (var command = conn.CreateCommand())
                {
                    command.CommandText = @"INSERT INTO contractaction (previouscounterparty, supplychainobserver, counterparty, 
                    supplychainowner, initiatingcounteraparty, state) VALUES (@_previouscounterparty, @_supplychainobserver, 
                    @_counterparty, @_supplychainowner, @_initiatingcounteraparty, @_state);";

                    command.Parameters.AddWithValue("@_previouscounterparty", _previouscounterparty);
                    command.Parameters.AddWithValue("@_supplychainobserver", _supplychainobserver);
                    command.Parameters.AddWithValue("@_counterparty", _counterparty);
                    command.Parameters.AddWithValue("@_supplychainowner", _supplychainowner);
                    command.Parameters.AddWithValue("@_initiatingcounteraparty", _initiatingcounteraparty);
                    command.Parameters.AddWithValue("@_state", _state);

                    rowCount = await command.ExecuteNonQueryAsync();
                }

            }
        
             return rowCount > 0
                ? (ActionResult)new OkObjectResult($"Updated, {rowCount} database rows")
                : new BadRequestObjectResult("No database updates");
        }
    }
}
