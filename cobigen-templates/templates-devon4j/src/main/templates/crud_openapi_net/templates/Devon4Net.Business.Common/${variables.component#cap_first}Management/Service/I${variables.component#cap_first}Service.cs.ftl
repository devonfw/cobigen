using Devon4Net.Infrastructure.ApplicationUser;
using System.Collections.Generic;
using System.Security.Claims;
using System.Threading.Tasks;
using Devon4Net.Business.Common.Dto;


namespace Devon4Net.Business.Common.${variables.component?cap_first}Management.Service
{
    public interface I${variables.component?cap_first}Service
    {
        Task<${variables.entityName?cap_first}Dto> Get${variables.entityName?cap_first}(long id);
        Task<${variables.entityName?cap_first}Dto> Save${variables.entityName?cap_first}(${variables.entityName?cap_first}Dto ${variables.entityName?uncap_first});
        Task<bool> Delete${variables.entityName?cap_first}(long id);
        Task<List<${variables.entityName?cap_first}Dto>> FindAll${variables.entityName?cap_first}s();
    }
}
        
        