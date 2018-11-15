using System;

namespace Workbench.Forms.Models
{
    public class LoginResponse
    {
        public string AuthHeader { get; set; }
        public string AccessToken { get; set; }
        public string AccessTokenType { get; set; }
        public DateTimeOffset ExpiresOn { get; set; }
        public bool ExtendedLifetimeToken { get; set; }
        public string IdToken { get; set; }
        public string TenantId { get; set; }
        public UserProfile Profile { get; set; } = new UserProfile();
    }

    public class UserProfile
    {
        public string DisplayableId { get; set; }
        public string FamilyName { get; set; }
        public string GivenName { get; set; }
        public string UniqueId { get; set; }
    }
}