using System;
using Newtonsoft.Json;

namespace Workbench.Client.Models
{
    public class Environment
    {
        public Environment() { }

        [JsonProperty("BackendDefinition")]
        public BackendDefinition BackendDefinition { get; set; }

        [JsonProperty("ThemeDefinition")]
        public ThemeDefinition ThemeDefinition { get; set; }
    }

    public class BackendDefinition
    {
        [JsonProperty("ResourceId")]
        public string ResourceId { get; set; }

        [JsonProperty("ClientId")]
        public string ClientId { get; set; }

        [JsonProperty("SiteUrl")]
        public string SiteUrl { get; set; }

        [JsonProperty("TenantId")]
        public string TenantId { get; set; }
    }

    public class ThemeDefinition
    {
        [JsonProperty("Contract")]
        public ContractThemeDefinition ContractTheme { get; set; }

        [JsonProperty("Portal")]
        public ContractPortal Portal { get; set; }
    }

    [JsonObject("Contract")]
    public class ContractThemeDefinition
    {
        [JsonProperty("ContractHomePage")]
        public ContractHomePage ContractHomePage { get; set; }

        [JsonProperty("ContractSelectionMenuPage")]
        public ContractSelectionMenuPage ContractSelectionMenuPage { get; set; }

        [JsonProperty("ContractDetailsPage")]
        public ContractDetailsPage ContractDetailsPage { get; set; }

        [JsonProperty("ContractRole")]
        public ContractRole ContractRole { get; set; }

        [JsonProperty("Portal")]
        public Portal Portal { get; set; }
    }

    public class ContractHomePage
    {
        [JsonProperty("ContractInstanceListTitleText")]
        public string ContractInstanceListTitleText { get; set; }

        [JsonProperty("BackgroundColor")]
        public string BackgroundColor { get; set; }

        [JsonProperty("CreateContractText")]
        public string CreateContractText { get; set; }

        [JsonProperty("ForegroundColor")]
        public string ForegroundColor { get; set; }
    }

    public class ContractSelectionMenuPage
    {
        [JsonProperty("BackgroundColor")]
        public string BackgroundColor { get; set; }

        [JsonProperty("ForegroundColor")]
        public string ForegroundColor { get; set; }
    }

    public class ContractDetailsPage
    {
        [JsonProperty("ContractDetailBlockchainTransactionCard")]
        public ContractDetailCard ContractDetailBlockchainTransactionCard { get; set; }

        [JsonProperty("ContractDetailDocumentsCard")]
        public ContractDetailCard ContractDetailDocumentsCard { get; set; }

        [JsonProperty("ContractDetailActionsCard")]
        public ContractDetailCard ContractDetailActionsCard { get; set; }

        [JsonProperty("ContractDetailContactsCard")]
        public ContractDetailCard ContractDetailContactsCard { get; set; }

        [JsonProperty("ContractDetailProgressCard")]
        public ContractDetailProgressCard ContractDetailProgressCard { get; set; }

        [JsonProperty("ContractDetailStatusCard")]
        public ContractDetailCard ContractDetailStatusCard { get; set; }
    }

    public class ContractDetailCard
    {
        [JsonProperty("ForegroundColor")]
        public string ForegroundColor { get; set; }

        [JsonProperty("IsVisible")]
        public bool? IsVisible { get; set; }

        [JsonProperty("BackgroundColor")]
        public string BackgroundColor { get; set; }

        [JsonProperty("HeaderText")]
        public string HeaderText { get; set; }

        [JsonProperty("Visible")]
        public bool? Visible { get; set; }
    }

    public class ContractDetailProgressCard
    {
        [JsonProperty("ActionPendingForegroundColor")]
        public string ActionPendingForegroundColor { get; set; }

        [JsonProperty("ActionConfirmedForegroundColor")]
        public string ActionConfirmedForegroundColor { get; set; }

        [JsonProperty("ActionConfirmedBackgroundColor")]
        public string ActionConfirmedBackgroundColor { get; set; }

        [JsonProperty("ActionPendingBackgroundColor")]
        public string ActionPendingBackgroundColor { get; set; }

        [JsonProperty("ForegroundColor")]
        public string ForegroundColor { get; set; }

        [JsonProperty("BackgroundColor")]
        public string BackgroundColor { get; set; }

        [JsonProperty("HeaderText")]
        public string HeaderText { get; set; }

        [JsonProperty("Visible")]
        public bool Visible { get; set; }
    }

    public class ContractRole
    {
        [JsonProperty("Buyer")]
        public Appraiser Buyer { get; set; }

        [JsonProperty("Appraiser")]
        public Appraiser Appraiser { get; set; }

        [JsonProperty("Inspector")]
        public Appraiser Inspector { get; set; }

        [JsonProperty("Owner")]
        public Appraiser Owner { get; set; }
    }

    public class Appraiser
    {
        [JsonProperty("HeaderForegroundColor")]
        public string HeaderForegroundColor { get; set; }

        [JsonProperty("HeaderText")]
        public string HeaderText { get; set; }

        [JsonProperty("HeaderBackgroundColor")]
        public string HeaderBackgroundColor { get; set; }

        [JsonProperty("HeaderLogoURL")]
        public string HeaderLogoURL { get; set; }

        [JsonProperty("TextLanguage")]
        public string TextLanguage { get; set; }
    }

    public class Portal
    {
        [JsonProperty("FooterBackgroundColor")]
        public string FooterBackgroundColor { get; set; }

        [JsonProperty("HeaderForegroundColor")]
        public string HeaderForegroundColor { get; set; }

        [JsonProperty("ContractMenuImageURL")]
        public string ContractMenuImageURL { get; set; }

        [JsonProperty("ContractDisplayName")]
        public string ContractDisplayName { get; set; }

        [JsonProperty("FontName")]
        public string FontName { get; set; }

        [JsonProperty("FooterText")]
        public string FooterText { get; set; }

        [JsonProperty("FooterForegroundColor")]
        public string FooterForegroundColor { get; set; }

        [JsonProperty("HeaderBackgroundColor")]
        public string HeaderBackgroundColor { get; set; }

        [JsonProperty("HeaderText")]
        public string HeaderText { get; set; }

        [JsonProperty("PanelForegroundColor")]
        public string PanelForegroundColor { get; set; }

        [JsonProperty("HeaderLogoURL")]
        public string HeaderLogoURL { get; set; }

        [JsonProperty("PanelBackgroundColor")]
        public string PanelBackgroundColor { get; set; }

        [JsonProperty("TextDirection")]
        public string TextDirection { get; set; }

        [JsonProperty("TextLanguage")]
        public string TextLanguage { get; set; }
    }

    [JsonObject("Portal")]
    public class ContractPortal : Portal
    {
        [JsonProperty("DefaultPanelBackgroundColor")]
        public string DefaultPanelBackgroundColor { get; set; }

        [JsonProperty("ButtonForegroundColor")]
        public string ButtonForegroundColor { get; set; }

        [JsonProperty("ButtonBackgroundColor")]
        public string ButtonBackgroundColor { get; set; }

        [JsonProperty("CompleteAccentColor")]
        public string CompleteAccentColor { get; set; }

        [JsonProperty("DefaultPanelForegroundColor")]
        public string DefaultPanelForegroundColor { get; set; }

        [JsonProperty("NotCompleteAccentColor")]
        public string NotCompleteAccentColor { get; set; }
    }

    public class Convert
    {
        // Serialize/deserialize helpers

        public static Environment FromJson(string json) => JsonConvert.DeserializeObject<Environment>(json, Settings);
        public static string ToJson(Environment o) => JsonConvert.SerializeObject(o, Settings);

        // JsonConverter stuff

        static JsonSerializerSettings Settings = new JsonSerializerSettings
        {
            MetadataPropertyHandling = MetadataPropertyHandling.Ignore,
            DateParseHandling = DateParseHandling.None,
        };
    }
}