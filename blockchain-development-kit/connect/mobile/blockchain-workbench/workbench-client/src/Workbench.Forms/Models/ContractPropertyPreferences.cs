using SQLite;

namespace Workbench.Forms.Models
{
    public class ContractPropertyPreferences
    {
        [PrimaryKey]
        public string ContractId { get; set; }
        public string DisplayedPropertyIds { get; set; }

        public string[] PropertyIds
        {
            get
            {
                return DisplayedPropertyIds.Split('|');
            }
        }
    }
}