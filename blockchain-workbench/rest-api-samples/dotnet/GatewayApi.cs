using System;
using System.Text;
using System.Net.Http;
using System.Diagnostics;
using System.Threading.Tasks;
using System.Net.Http.Headers;
using System.Collections.Generic;

using Newtonsoft.Json;

using Polly;
using Workbench.Client.Models;
using System.IO;

namespace Workbench.Client
{
	public sealed class GatewayApi
	{

		static int CLIENT_API_TIMEOUT = 15;
		static int POLLY_RETRY_COUNT = 5;
        const int TOP_QUERY_PARAM = 50;

		#region Singleton Implementation

        static GatewayApi instance = null;
		static readonly object instancelock = new object();

		public static GatewayApi Instance
		{
			get
			{
				if (instance == null)
				{
					lock (instancelock)
					{
						instance = new GatewayApi();
					}

				}

				return instance;
			}
		}
    
		#endregion

		static int numberOfFailedTries;
		public static string SiteUrl = string.Empty;
		public static string BaseUrl
		{
			get {
				if(SiteUrl.EndsWith("/"))
				    return $"{SiteUrl}api/v1/"; 
				else
					return $"{SiteUrl}/api/v1/";
			}
		}

		// Static HTTPClient
        bool IsLoggedIn => (!string.IsNullOrWhiteSpace(AuthToken)
                                         && (!AccessTokenExpiration.HasValue || (AccessTokenExpiration.HasValue && AccessTokenExpiration.Value >= DateTimeOffset.UtcNow)));

        public DateTimeOffset? AccessTokenExpiration { get; set; }
		static string AuthToken { get; set; }
		static HttpClient _httpClient;
        static HttpClient HttpClient
        {
            get
            {
                if (_httpClient == null)
                {
					_httpClient = new HttpClient ();
					_httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", AuthToken);
					_httpClient.Timeout = TimeSpan.FromSeconds(CLIENT_API_TIMEOUT);
                }
				return _httpClient;
            }
        }

		public event EventHandler ExpiredAccessToken;
		public event EventHandler<RequestFailedEventArgs> MaxTimeoutExceeded;
		public class RequestFailedEventArgs : EventArgs
		{

			public string Title { get; set; }
			public string Message { get; set; }
		}
		public event EventHandler<Exception> ExceptionThrown;
        
		public void SetAuthToken(string token)
		{
			AuthToken = token;         
            _httpClient = null;
		}

		#region CHECKERS 
		public async Task<string> CheckApplicationAsync(FileStream appFile, string appFileName)
        {
            var url = $"{BaseUrl}checkers/checkApplication";

			var formContent = new MultipartFormDataContent();
			formContent.Add(new StreamContent(appFile), "appFile", appFileName);
         
			var (success, error) = await postFormAsync(url, formContent);

			formContent = null;
            if (success)
                return string.Empty;
            return error;
        }
        
		public async Task<string> CheckContractCodeAsync(FileStream appFile, FileStream contractFile, string appFileName, string contractFileName, int ledgerID)
        {
			var url = $"{BaseUrl}checkers/checkContractCode?ledgerId={ledgerID}";

			var formContent = new MultipartFormDataContent();
			formContent.Add(new StreamContent(appFile), "appFile", appFileName);
			formContent.Add(new StreamContent(contractFile), "contractFile", contractFileName);
                  
			var (success, error) = await postFormAsync(url,formContent);

			formContent = null;

            if (success)
                return string.Empty;
            return error;
        }
#endregion

		#region APPLICATIONS
		public async Task<IEnumerable<Application>> GetApplicationsAsync(bool Enabled = false, int top = TOP_QUERY_PARAM, int skip = 0)
        {
            if (Enabled)
            {
                var result = await getDataObjectFromAPI<ApplicationReturnType>($"{BaseUrl}applications?enabled=true&top={top}&skip={skip}");
                return result?.Applications;
            }
            else
            {
                var result = await getDataObjectFromAPI<ApplicationReturnType>($"{BaseUrl}applications?top={top}&skip={skip}");
                return result?.Applications;
            }
                    
        }

		public async Task<string> PostApplicationAsync(FileStream appFile, string appFileName)
        {
            var url = $"{BaseUrl}applications";

            var formContent = new MultipartFormDataContent();
            formContent.Add(new StreamContent(appFile), "appFile", appFileName);

            var (success, error) = await postFormAsync(url, formContent);

            formContent = null;
            if (success)
                return string.Empty;
            return error;
        }

		public async Task<string> DeleteApplicationByIdAsync(string applicationID)
        {
			var(success, error) = await deleteDataObjectFromAPI($"{BaseUrl}applications/{applicationID}");
			         
            if (success)
                return string.Empty;
            return error;
        }

		public async Task<Application> GetApplicationByIdAsync(string applicationID)
        {
			return await getDataObjectFromAPI<Application>($"{BaseUrl}applications/{applicationID}");
        }


        public async Task<string> EnableOrDisableApplicationByIdAsync(string applicationID, bool enable)
        {
			if (enable)
			{
				var (success, error) = await patchDataObjectToAPI($"{BaseUrl}applications/{applicationID}/enable");
				
				if (success)
                    return string.Empty;
					
                return error;
			}
			else
			{
				var(success,error) = await patchDataObjectToAPI($"{BaseUrl}applications/{applicationID}/disable");
				if (success)
				if (success)
                    return string.Empty;
				
                return error;
			}
        }

        public async Task<IEnumerable<RoleAssignment>> GetUserRoleAssignmentsAsync(string applicationID, int top = TOP_QUERY_PARAM, int skip = 0)
        {
            var result = await getDataObjectFromAPI<RoleAssignmentReturnType>($"{BaseUrl}applications/{applicationID}/roleAssignments?top={top}&skip={skip}");
			return result?.RoleAssignments;
		}

		public async Task<RoleAssignment> GetUserRoleAssignmentsByIdAsync(string applicationID, string roleAssignmentID)
        {
			return await getDataObjectFromAPI<RoleAssignment>($"{BaseUrl}applications/{applicationID}/roleAssignments/{roleAssignmentID}");
        }
        
		public async Task<string> PostUserRoleAssignmentsByIdAsync(string applicationID, int _userId, int _applicationRoleId)
		{
			var roleAssignmentInput = new { userId = _userId, applicationRoleId = _applicationRoleId };

			var url = $"{BaseUrl}applications/{applicationID}/roleAssignments";
			var (success, error) = await postDataObjectAsync(roleAssignmentInput, url);

            if (success)
                return string.Empty;
            return error;
		}

       
		public async Task<string> DeleteUserRoleAssignmentsByIdAsync(string applicationID, string roleAssignmentID)
        {
			var (success, error) = await deleteDataObjectFromAPI($"{BaseUrl}applications/{applicationID}/roleAssignments/{roleAssignmentID}");

            if (success)
                return string.Empty;
            return error;
        }


		public async Task<string> UpdateUserRoleAssignmentsByIdAsync(string ApplicationID, string RoleAssignmentID, int UserId, int ApplicationRoleId)
        {
            var roleAssignmentInput = new { userId = UserId, applicationRoleId = ApplicationRoleId };
            
			var url = $"{BaseUrl}applications/{ApplicationID}/roleAssignments/{RoleAssignmentID}";
            var (success, error) = await putDataObjectAsync(roleAssignmentInput, url);

            if (success)
                return string.Empty;
            return error;
        }

		public async Task<IEnumerable<Workflow>> GetWorkflowsByApplicationIdAsync(string applicationID, int top = TOP_QUERY_PARAM, int skip = 0)
        {
            var result = await getDataObjectFromAPI<WorkflowReturnType>($"{BaseUrl}applications/{applicationID}/workflows?top={top}&skip={skip}");
			return result?.Workflows;
        }

		public async Task<Workflow> GetWorkflowByIdAsync(string workflowID, string applicationID = null)
        {
			return await getDataObjectFromAPI<Workflow>($"{BaseUrl}applications/workflows/{workflowID}");
        }

		public async Task<IEnumerable<ContractCodes>> GetContractCodesByApplicationAsync(string applicationID, int ledgerID, int top = TOP_QUERY_PARAM, int skip = 0)
		{
            var result = await getDataObjectFromAPI<ContractCodesReturnType>($"{BaseUrl}applications/{applicationID}/contractCode?ledgerId={ledgerID}&top={top}&skip={skip}");
			return result?.ContractCodes;
        }
        
		public async Task<string> PostContractCodeByApplicationAsync(FileStream contractFile, string contractFileName, string applicationID, int ledgerID)
        {
			var url = $"{BaseUrl}applications/{applicationID}/contractCode?ledgerId={ledgerID}";

            var formContent = new MultipartFormDataContent();
			formContent.Add(new StreamContent(contractFile), "contractFile", contractFileName);

            var (success, error) = await postFormAsync(url, formContent);

            formContent = null;
            if (success)
                return string.Empty;
            return error;
        }

		public async Task<string> DeleteContractCodesByApplicationAsync(string contractCodeID)
        {
			var (success, error) = await deleteDataObjectFromAPI($"{BaseUrl}applications/contractCode/{contractCodeID}");

            if (success)
                return string.Empty;
            return error;
        }

		//TODO: IMPLEMENT GETCONTRACTCODESBYID AS A FILESTREAM
		//public async Task<ContractCodes> GetContractCodesByIdAsync(string contractCodeID)
  //      {
		//}

#endregion

#region CONNECTIONS

		public async Task<IEnumerable<Connection>> GetConnectionsAsync(int top = TOP_QUERY_PARAM, int skip = 0)
        {
            var result = await getDataObjectFromAPI<ConnectionsReturnType>($"{BaseUrl}ledgers/connections?top={top}&skip={skip}");
			return result?.Connections;
        }
        
		public async Task<Connection> GetConnectionByIdAsync(string connectionID)
		{
			return await getDataObjectFromAPI<Connection>($"{BaseUrl}ledgers/connections/{connectionID}");
		}

		public async Task<IEnumerable<Block>> GetBlocksByConnectionIdAsync(string connectionID, int top = TOP_QUERY_PARAM, int skip = 0)
        {
            var result = await getDataObjectFromAPI<BlockReturnType>($"{BaseUrl}ledgers/connections/{connectionID}/blocks?top={top}&skip={skip}");
            return result?.Blocks;
        }

		public async Task<Block> GetBlockByIdAsync(string connectionID, string blockID)
		{
			return await getDataObjectFromAPI<Block>($"{BaseUrl}ledgers/connections/{connectionID}/blocks/{blockID}");
		}

		public async Task<IEnumerable<Transaction>> GetTransactionsByChainInstanceIdAsync(string connectionID, int top = TOP_QUERY_PARAM, int skip = 0)
        {
            var result = await getDataObjectFromAPI<TransactionReturnType>($"{BaseUrl}ledgers/connections/{connectionID}/transactions?top={top}&skip={skip}");
            return result?.Transactions;
        }
        
		public async Task<Transaction> GetTransactionByIdAsync(string connectionID, string transactionID)
        {
			return await getDataObjectFromAPI<Transaction>($"{BaseUrl}ledgers/connections/{connectionID}/transactions/{transactionID}");
        }
        
		public async Task<IEnumerable<Ledger>> GetLedgersAsync(int top = TOP_QUERY_PARAM, int skip = 0)
        {
            var result = await getDataObjectFromAPI<LedgersReturnType>($"{BaseUrl}ledgers?top={top}&skip={skip}");
			return result?.Ledgers;
        }
       
      
#endregion

#region GRAPH PROXY & HEALTH
		public async Task GraphProxy(string version)
        {
			await getDataObjectFromAPI<string>($"{BaseUrl}graphProxy/{version}/users");
        }

        public async Task<string> GetApiHealth()
        {
			return await getDataObjectFromAPI<string>($"{BaseUrl}health");
        }
#endregion

#region USERS & CAPABILITIES
		public async Task<IEnumerable<User>> GetAllUsersAsync(int top = TOP_QUERY_PARAM, int skip = 0,string sortBy = "FirstName")
        {
            var result = await getDataObjectFromAPI<UsersReturnType>($"{BaseUrl}users?sortBy={sortBy}&top={top}&skip={skip}");
            return result?.Users;
        }
      
        public async Task<string> PostUserAsync(string ExternalID, string FirstName, string LastName, string EmailAddress)
        {
			var newUser = new { externalID = ExternalID, firstName = FirstName, lastName = LastName, emailAddress = EmailAddress };         
            
			var url = $"{BaseUrl}users";
            
			var (success, error) = await postDataObjectAsync(newUser, url);

            if (success)
                return string.Empty;
            return error;
        }
        
        public async Task<User> GetUserByIdAsync(string userID)
        {
			return await getDataObjectFromAPI<User>($"{BaseUrl}users/{userID}");
        }

		public async Task<string> DeleteUserById(string userID)
        {
			var (success, error) = await deleteDataObjectFromAPI($"{BaseUrl}users/{userID}");

            if (success)
                return string.Empty;
            return error;
        }

		public async Task<CurrentUser> GetCurrentUserDetails()
        {
            return await getDataObjectFromAPI<CurrentUser>($"{BaseUrl}users/me");
        }

		public async Task<bool> CanCurrentUserCreateContractsForWorkflow(string workflowId)
		{
			return await getDataObjectFromAPI<bool>($"{BaseUrl}/capabilities/canCreateContract/{workflowId}");
		}

		public async Task<Capabilities> GetCapabilitiesForUserAsync()
        {
            return await getDataObjectFromAPI<Capabilities>($"{BaseUrl}/capabilities");
        }


      
#endregion
        
#region WORKFLOWINSTANCES (CONTRACTS)
		public async Task<Contract> GetContractByIdAsync(string contractId)
        {
			return await getDataObjectFromAPI<Contract>($"{BaseUrl}contracts/{contractId}");         
        }

        public async Task<IEnumerable<Contract>> GetWorkflowInstancesAsync(string workflowInstanceID, int top = TOP_QUERY_PARAM, int skip = 0, string sortBy = "Timestamp")
        {
            var result = await getDataObjectFromAPI<WorkflowInstancesReturnType>($"{BaseUrl}contracts?workflowid={workflowInstanceID}&top={top}&skip={skip}&sortBy={sortBy}");
			return result?.Contracts;
        }

		public async Task<string> CreateNewContractAsync(ActionInformation action, string workflowID, string contractCodeID, string connectionID)
        {
			var url = $"{BaseUrl}contracts?workflowId={workflowID}&contractCodeId={contractCodeID}&connectionId={connectionID}";
            var (success, error) = await postDataObjectAsync(action, url);

            if (success)
                return string.Empty;
            return error;
        }
        
		public async Task<IEnumerable<WorkflowFunction>> GetWorkflowActionsAsync(string workflowInstanceID, int top = TOP_QUERY_PARAM, int skip = 0)
        {
            var result = await getDataObjectFromAPI<Actions>($"{BaseUrl}contracts/{workflowInstanceID}/actions?top={top}&skip={skip}");
			return result?.WorkflowFunctions;
        }

        public async Task<WorkflowFunction> GetWorkflowActionByActionIdAsync(string workflowInstanceID, string actionID)
        {
			return await getDataObjectFromAPI<WorkflowFunction>($"{BaseUrl}contracts/{workflowInstanceID}/actions/{actionID}");
        }
        
		public async Task<string> PostWorkflowActionAsync(ActionInformation action, string contractID)
        {
			var url = $"{BaseUrl}contracts/{contractID}/actions";
            var (success, error) = await postDataObjectAsync(action, url);

            if (success)
                return string.Empty;
            return error;
        }

      
#endregion

        /// <summary>
        /// Generic Post Method 
        /// </summary>
        /// <returns>If Succesful - returns True status code and an empty string, If Unsuccesful - returns false status code and message of exception</returns>
        /// <param name="objectToPost">The Object to POST in the body</param>
        /// <param name="url">the URL</param>
        /// <typeparam name="T">The Class Type</typeparam>

		async Task<(bool, string)> postDataObjectAsync<T>(T objectToPost, string url)
        {
            if (!IsLoggedIn)
            {
                ExpiredAccessToken.Invoke(Instance, EventArgs.Empty);
            }
            try
            {
                var json = JsonConvert.SerializeObject(objectToPost);
                var content = new StringContent(json, Encoding.UTF8, "application/json");

                var msg = await Policy
                    .Handle<TaskCanceledException>()
                    .WaitAndRetryAsync
                    (
						retryCount: POLLY_RETRY_COUNT,
                        sleepDurationProvider: retryAttempt => TimeSpan.FromSeconds(Math.Pow(2, retryAttempt))
                    )
                    .ExecuteAsync(async () => await HttpClient.PostAsync(url, content).ConfigureAwait(false));
                           
                if (msg.IsSuccessStatusCode)
                {
                    numberOfFailedTries = 0;
                    return (true, string.Empty);
                }

                return (false, await msg.Content.ReadAsStringAsync());
            }
            catch (JsonException je)
            {
				ExceptionThrown?.Invoke(Instance,je);
                Debug.WriteLine($"Error posting object {nameof(T)} - Unknown: {je.Message}");
                return (false, je.Message);
            }
            catch (HttpRequestException we)
            {
				ExceptionThrown?.Invoke(Instance, we);
                Debug.WriteLine($"Error posting object {nameof(T)} - Unknown: {we.Message}");
                if (we.Message.Contains("401"))
                {
                    numberOfFailedTries++;
                }

                return (false, we.Message);
            }
        }

		async Task<(bool, string)> postFormAsync(string url, MultipartFormDataContent formDataContent)
        {
            if (!IsLoggedIn)
            {
                ExpiredAccessToken.Invoke(Instance, EventArgs.Empty);
            }
            try
            {            
                var msg = await Policy
                    .Handle<TaskCanceledException>()
                    .WaitAndRetryAsync
                    (
                        retryCount: POLLY_RETRY_COUNT,
                        sleepDurationProvider: retryAttempt => TimeSpan.FromSeconds(Math.Pow(2, retryAttempt))
                    )
                    .ExecuteAsync(async () => await HttpClient.PostAsync(url, formDataContent).ConfigureAwait(false));
                            
                if (msg.IsSuccessStatusCode)
                {
                    numberOfFailedTries = 0;
                    return (true, string.Empty);
                }

                return (false, await msg.Content.ReadAsStringAsync());
            }
            catch (HttpRequestException we)
            {
                ExceptionThrown?.Invoke(Instance, we);
                Debug.WriteLine($"Error posting form");
                if (we.Message.Contains("401"))
                {
                    numberOfFailedTries++;
                }

                return (false, we.Message);
            }
        }

		/// <summary>
        /// Generic Get Method 
        /// </summary>
        /// <param name="url">the URL</param>
        /// <typeparam name="T">The Class Type to GET and deserialize</typeparam>

		async Task<T> getDataObjectFromAPI<T>(string url)
		{
            if (!IsLoggedIn)
            {
                ExpiredAccessToken.Invoke(Instance, EventArgs.Empty);
            }
			try
			{
				var json = await Policy
					.Handle<TaskCanceledException>()
					.WaitAndRetryAsync
					(
						retryCount: POLLY_RETRY_COUNT,
						sleepDurationProvider: retryAttempt => TimeSpan.FromSeconds(Math.Pow(2, retryAttempt))
					)
					.ExecuteAsync(async () => await HttpClient.GetStringAsync(url));

				var objectToReturn = JsonConvert.DeserializeObject<T>(json);

				numberOfFailedTries = 0;
				return objectToReturn;
			}
			catch (JsonException je)
			{
				ExceptionThrown?.Invoke(Instance, je);
				Debug.WriteLine($"Error getting object {nameof(T)} - Json: {je.Message}");
			}
			catch (HttpRequestException we)
			{
				ExceptionThrown?.Invoke(Instance, we);
				var requestFailedArgs = new RequestFailedEventArgs();

				if (we.Message.Contains("401"))
				{
					requestFailedArgs.Title = "Unauthorized";
					requestFailedArgs.Message = "Your access token has expired. Please login again.";
					numberOfFailedTries++;

					if (numberOfFailedTries == 3)
					{
						numberOfFailedTries = 0;
						Instance.ExpiredAccessToken?.Invoke(Instance, EventArgs.Empty);
					}
				}
				else
				{
					if (we.Message.Contains("502"))
						requestFailedArgs.Title = "Bad Gateway";
					else if (we.Message.Contains("500"))
						requestFailedArgs.Title = "Internal Server Error";
					else
						requestFailedArgs.Title = "Unknown Error";

					requestFailedArgs.Message = "There was an error in the running service. Please try again.";

					Instance.MaxTimeoutExceeded?.Invoke(Instance, requestFailedArgs);
				}

				Debug.WriteLine($"Error getting object {nameof(T)} - Http: {we.Message}");
			}
			catch (TaskCanceledException te)
			{
				ExceptionThrown?.Invoke(Instance, te);
				Instance.MaxTimeoutExceeded?.Invoke(Instance, new RequestFailedEventArgs
				{
					Title = "Timed out",
					Message = $"The backend timed out after {POLLY_RETRY_COUNT} tries"
				});
			}
			catch (Exception e)
			{
				ExceptionThrown?.Invoke(Instance, e);
				Debug.WriteLine($"Error getting object {nameof(T)} - Unknown: {e.Message}");
			}

			return default(T);
		}

		/// <summary>
        /// Generic Delete Method 
        /// </summary>
        /// <param name="url">the URL</param>

		async Task<(bool,string)> deleteDataObjectFromAPI(string url)
        {
            if (!IsLoggedIn)
            {
                ExpiredAccessToken.Invoke(Instance, EventArgs.Empty);
            }
            try
            {
                var msg = await Policy
                    .Handle<TaskCanceledException>()
                    .WaitAndRetryAsync
                    (
                        retryCount: POLLY_RETRY_COUNT,
                        sleepDurationProvider: retryAttempt => TimeSpan.FromSeconds(Math.Pow(2, retryAttempt))
                    )
                    .ExecuteAsync(async () => await HttpClient.DeleteAsync(url));

				if (msg.IsSuccessStatusCode)
                {
                    numberOfFailedTries = 0;
                    return (true, string.Empty);
                }

                return (false, await msg.Content.ReadAsStringAsync());
            }
			catch (Exception e)
            {
                ExceptionThrown?.Invoke(Instance, e);
                Debug.WriteLine($"Error deleting object");
                if (e.Message.Contains("401"))
                {
                    numberOfFailedTries++;
                }

                return (false, e.Message);
            }
        }

		/// <summary>
		/// Generic Patch Method 
		/// </summary>
		/// <param name="url">the URL</param>

		async Task<(bool, string)> patchDataObjectToAPI(string url)
		{
            if (!IsLoggedIn)
            {
                ExpiredAccessToken.Invoke(Instance, EventArgs.Empty);
            }
			try
			{
				var msg = await Policy
					.Handle<TaskCanceledException>()
					.WaitAndRetryAsync
					(
						retryCount: POLLY_RETRY_COUNT,
						sleepDurationProvider: retryAttempt => TimeSpan.FromSeconds(Math.Pow(2, retryAttempt))
					)
					.ExecuteAsync(async () => await HttpClient.SendAsync(new HttpRequestMessage(new HttpMethod("PATCH"), url)));
               
				if (msg.IsSuccessStatusCode)
				{
					numberOfFailedTries = 0;
					return (true, string.Empty);
				}

				return (false, await msg.Content.ReadAsStringAsync());
			}
			catch (Exception e)
			{
				ExceptionThrown?.Invoke(Instance, e);
				Debug.WriteLine($"Error patching object");
				if (e.Message.Contains("401"))
				{
					numberOfFailedTries++;
				}

				return (false, e.Message);
			}
		}

        /// <summary>               
        /// Generic Put Method 
        /// </summary>
        /// <returns>If Succesful - returns True status code and an empty string, If Unsuccesful - returns false status code and message of exception</returns>
        /// <param name="objectToPut">The Object to POST in the body</param>
        /// <param name="url">the URL</param>
        /// <typeparam name="T">The Class Type</typeparam>

		async Task<(bool, string)> putDataObjectAsync<T>(T objectToPut, string url)
        {
            if (!IsLoggedIn)
            {
                ExpiredAccessToken.Invoke(Instance, EventArgs.Empty);
            }
            try
            {
                var json = JsonConvert.SerializeObject(objectToPut);
                var content = new StringContent(json, Encoding.UTF8, "application/json");

                var msg = await Policy
                    .Handle<TaskCanceledException>()
                    .WaitAndRetryAsync
                    (
                        retryCount: POLLY_RETRY_COUNT,
                        sleepDurationProvider: retryAttempt => TimeSpan.FromSeconds(Math.Pow(2, retryAttempt))
                    )
                    .ExecuteAsync(async () => await HttpClient.PutAsync(url, content).ConfigureAwait(false));

                if (msg.IsSuccessStatusCode)
                {
                    numberOfFailedTries = 0;
                    return (true, string.Empty);
                }

                return (false, await msg.Content.ReadAsStringAsync());
            }
            catch (JsonException je)
            {
                ExceptionThrown?.Invoke(Instance, je);
                Debug.WriteLine($"Error posting object {nameof(T)} - Unknown: {je.Message}");
                return (false, je.Message);
            }
            catch (HttpRequestException we)
            {
                ExceptionThrown?.Invoke(Instance, we);
                Debug.WriteLine($"Error posting object {nameof(T)} - Unknown: {we.Message}");
                if (we.Message.Contains("401"))
                {
                    numberOfFailedTries++;
                }

                return (false, we.Message);
            }
        }
	}
}
