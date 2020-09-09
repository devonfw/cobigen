using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using System.Threading.Tasks;
using Devon4Net.Domain.UnitOfWork.Repository;
using Devon4Net.Infrastructure.Log;
using Devon4Net.WebAPI.Implementation.Domain.Database;
using Devon4Net.WebAPI.Implementation.Domain.Entities;
using Devon4Net.WebAPI.Implementation.Domain.RepositoryInterfaces;
using Devon4Net.WebAPI.Implementation.Business.${variables.entityName?cap_first}Management.Exceptions;

namespace Devon4Net.WebAPI.Implementation.Data.Repositories
{
    /// <summary>
    /// ${variables.entityName?cap_first} Repository
    /// </summary>
    public class ${variables.entityName?cap_first}Repository : Repository<${variables.entityName?cap_first}>, I${variables.entityName?cap_first}Repository
    {
        /// <summary>
        /// ${variables.entityName?cap_first}Repository Constructor
        /// </summary>
        public ${variables.entityName?cap_first}Repository(CobigenContext context) : base(context)
        {
        }

        /// <summary>
        /// Get all ${variables.entityName?cap_first}
        /// </summary>
        /// <param name="predicate"></param>
        /// <returns>"List of ${variables.entityName?cap_first}"</returns>
        public async Task<IList<${variables.entityName?cap_first}>> Get${variables.entityName?cap_first}s(Expression<Func<${variables.entityName?cap_first}, bool>> predicate = null)
        {
            Devon4NetLogger.Debug("Get${variables.entityName?cap_first} method from ${variables.entityName?cap_first}Repository ${variables.entityName?cap_first}Service");
            return await Get().ConfigureAwait(false);
        }

        /// <summary>
        /// Get${variables.entityName?cap_first}ById
        /// </summary>
        /// <param name="id"></param>
        /// <returns>"${variables.entityName?cap_first}"</returns>
        public async Task<${variables.entityName?cap_first}> Get${variables.entityName?cap_first}ById(long id)
        {
            Devon4NetLogger.Information("Replace GetFirstOrDefault() to Get() with the needed expresion");
            return await GetFirstOrDefault(s=> s.${variables.entityName?cap_first}Id == id).ConfigureAwait(false);
        }

        /// <summary>
        /// Set${variables.entityName?cap_first}
        /// </summary>
        /// <returns>"${variables.entityName?cap_first}"</returns>
        public async Task<${variables.entityName?cap_first}> Set${variables.entityName?cap_first}(<#list model.properties as property><#if property.isCollection><#if property.type == "number">long<#elseif property.type == "integer">int<#else>${property.type}</#if>[] ${property.name?uncap_first}<#if property?has_next>, </#if><#else><#if property.type == "number">long<#elseif property.type == "integer">int<#else>${property.type}</#if> ${property.name?uncap_first}<#if property?has_next>, </#if></#if></#list>)
        {
            Devon4NetLogger.Debug($"Set${variables.entityName?cap_first} method from repository ${variables.entityName?cap_first}Service");
            Devon4NetLogger.Information("Replace input parameter Create(new ${variables.entityName?cap_first}()) with the object that is needed to be created");
            return await Create(new ${variables.entityName?cap_first}{<#list model.properties as property>${property.name?cap_first} = ${property.name?uncap_first}<#if property?has_next>, </#if></#list>}).ConfigureAwait(false);
        }

        /// <summary>
        /// Delete${variables.entityName?cap_first}ById
        /// </summary>
        /// <param name="id"></param>
        /// <returns>"deleted id"</returns>
        public async Task<long> Delete${variables.entityName?cap_first}ById(long id)
        {
            Devon4NetLogger.Information("Add Expresion to delete");
            var deleted = await Delete(s => s.${variables.entityName?cap_first}Id == id).ConfigureAwait(false);

            if (deleted)
            {
                return id;
            }

            throw  new ${variables.entityName?cap_first}NotDeletedException($"The ${variables.entityName?cap_first} entity {id} has not been deleted.");
        }
    }
}
