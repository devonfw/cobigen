using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using System.Threading.Tasks;
using Devon4Net.Domain.UnitOfWork.Repository;
using Devon4Net.WebAPI.Implementation.Domain.Entities;

namespace Devon4Net.WebAPI.Implementation.Domain.RepositoryInterfaces
{
    /// <summary>
    /// ${variables.entityName?cap_first}Repository interface
    /// </summary>
    public interface I${variables.entityName?cap_first}Repository : IRepository<${variables.entityName?cap_first}>
    {
        /// <summary>
        /// Get${variables.entityName?cap_first}
        /// </summary>
        /// <param name="predicate"></param>
        /// <returns>"List of ${variables.entityName?cap_first}"</returns>
        Task<IList<${variables.entityName?cap_first}>> Get${variables.entityName?cap_first}s(Expression<Func<${variables.entityName?cap_first}, bool>> predicate = null);

        /// <summary>
        /// Get${variables.entityName?cap_first}ById
        /// </summary>
        /// <param name="id"></param>
        /// <returns>"${variables.entityName?cap_first}"</returns>
        Task<${variables.entityName?cap_first}> Get${variables.entityName?cap_first}ById(long id);

        /// <summary>
        /// Set${variables.entityName?cap_first}
        /// </summary>
        /// <returns>"${variables.entityName?cap_first}"</returns>
        Task<${variables.entityName?cap_first}> Set${variables.entityName?cap_first}(<#list model.properties as property><#if property.isCollection><#if property.type == "number">long<#elseif property.type == "integer">int<#else>${property.type}</#if>[] ${property.name?uncap_first}<#if property?has_next>, </#if><#else><#if property.type == "number">long<#elseif property.type == "integer">int<#else>${property.type}</#if> ${property.name?uncap_first}<#if property?has_next>, </#if></#if></#list>);

        /// <summary>
        /// Delete${variables.entityName?cap_first}ById
        /// </summary>
        /// <param name="id"></param>
        /// <returns>"deleted id"</returns>
        Task<long> Delete${variables.entityName?cap_first}ById(long id);
    }
}