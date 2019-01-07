using System.Threading.Tasks;

using Workbench.Forms.Models;

namespace Workbench.Forms.Interfaces
{
    public interface IAuthentication
    {
        Task<LoginResponse> LoginAsync(string authority, string resource, string clientId, string returnUri, bool isRefresh = false);
        void clearTokenFromCache(string authority);
    }
}