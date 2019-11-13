using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using System.Threading.Tasks;
using Devon4Net.WebAPI.Implementation.Business.Dto;
using Devon4Net.WebAPI.Implementation.Domain.Entities;

namespace Devon4Net.WebAPI.Implementation.Business.${variables.component?cap_first}Management.Service
{
    public interface I${variables.component?cap_first}Service
    {
        Task<${variables.entityName?cap_first}Dto> Get${variables.entityName?cap_first}ById(long id);
        Task<${variables.entityName?cap_first}Dto> Create${variables.entityName?cap_first}(${variables.entityName?cap_first}Dto ${variables.entityName?uncap_first});
        Task<long> Delete${variables.entityName?cap_first}ById(long id);
        Task<List<${variables.entityName?cap_first}Dto>> FindAll${variables.entityName?cap_first}s(Expression<Func<${variables.entityName?cap_first}, bool>> predicate = null);
    }
}
        
        