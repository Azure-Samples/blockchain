using SQLite;

using Newtonsoft.Json;

using Workbench.Client.Models;

namespace Workbench.Forms.Models
{
	[Table("BlockchainEnvironment")]
	public class BlockchainEnvironment
	{
		public BlockchainEnvironment()
		{
		}

		[JsonIgnore, PrimaryKey, AutoIncrement]
		public System.Guid Id { get; set; }

		public string NickName { get; set; } = string.Empty;
		public string ThemeDefinitionUrl { get; set; }
		public string TenantId { get; set; }
		public string ResourceId { get; set; }
		public string ClientId { get; set; }
		public string ReturnUrl { get; set; }
		public string SiteUrl { get; set; }
		public string PreferredColor { get; set; }

		public bool IsSelected { get; set; }

	}
}