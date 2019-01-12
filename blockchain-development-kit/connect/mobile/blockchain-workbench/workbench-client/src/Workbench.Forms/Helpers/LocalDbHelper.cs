using System.Threading.Tasks;
using System.Collections.Generic;

using SQLite;

using Workbench.Client.Models;

using Workbench.Forms.Models;
using Workbench.Forms.Interfaces;


namespace Workbench.Forms.Helpers
{
    //This class is for saving environments to a database in case one wants to add the functionality to switch between multiple environments

	public class LocalDbHelper
	{
		public LocalDbHelper(string dbPath)
		{
			database = new SQLiteAsyncConnection(dbPath);
			database.CreateTableAsync<ContractPropertyPreferences>().Wait();
			database.CreateTableAsync<BlockchainEnvironment>().Wait();
		}

		#region Singleton implementation

		static SQLiteAsyncConnection database;
		static LocalDbHelper instance;

		public static LocalDbHelper Instance
		{
			get
			{
				if (instance == null)
				{
					instance = new LocalDbHelper(ServiceContainer.Resolve<IDatabase>().GetDbPath("ContractPreferences"));
				}
				return instance;
			}
		}

		#endregion

		#region BlockchainEnvironment Methods

		public async Task<int> SaveEnvironmentAsync(BlockchainEnvironment envToSave)
		{
			try
			{
				return await database.InsertOrReplaceAsync(envToSave);
			}
			catch (SQLiteException)
			{
				return -1;
			}
		}

		public async Task<List<BlockchainEnvironment>> GetAllSavedEnvironmentsAsync()
		{
			try
			{
				return await database.Table<BlockchainEnvironment>().ToListAsync();

			}
			catch (SQLiteException)
			{
				return null;
			}
		}

		public async Task PurgeEnvironments()
		{
			await database.DropTableAsync<BlockchainEnvironment>();
			await database.CreateTableAsync<BlockchainEnvironment>();
		}

		#endregion

		#region ContractInstance Mehtods
        
		public async Task<IEnumerable<Contract>> GetContractInstancesAsync(string contractId)
		{
			var pref = await database.Table<Contract>().Where(x => x.Id.ToString() == contractId).ToListAsync();

			if (pref != null)
				return pref;

			return null;
		}

		public async Task SaveContractInstancesAsync(IEnumerable<Contract> instancesToSave)
		{
			foreach (var instance in instancesToSave)
				await database.InsertOrReplaceAsync(instance);
		}

		#endregion

		#region ContractPropertyPreferences Methods

		public async Task<ContractPropertyPreferences> GetContractPropertyPreferencesAsync(string id)
		{
			if (string.IsNullOrWhiteSpace(id)) return null;

			var pref = await database.FindAsync<ContractPropertyPreferences>(x => x.ContractId == id);

			if (pref != null)
			{
				return pref;
			}

			return null;
		}

		public async Task<bool> SaveContractPropertyPreferencesAsync(ContractPropertyPreferences plan)
		{
			var duplicateCheck = await database.FindAsync<ContractPropertyPreferences>(x => x.ContractId == plan.ContractId);

			if (duplicateCheck != null)
				await database.UpdateAsync(plan);
			else
				await database.InsertAsync(plan);

			return true;
		}

		#endregion
	}
}