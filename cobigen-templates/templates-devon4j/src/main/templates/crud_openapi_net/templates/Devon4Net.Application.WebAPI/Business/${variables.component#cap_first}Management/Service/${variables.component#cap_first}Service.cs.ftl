using Devon4Net.Application.WebAPI.Business.${variables.entityName?cap_first}Management.Exceptions;
using Devon4Net.Application.WebAPI.Business.${variables.entityName?cap_first}Management.Dto;
using Devon4Net.Application.WebAPI.Business.${variables.entityName?cap_first}Management.Converter;
<#list model.properties as property>
<#if property.type != "string" && property.type != "number" && property.type != "boolean">
using Devon4Net.Application.WebAPI.Business.${property.name?cap_first}Management.Converter;
</#if>
</#list>
using Devon4Net.Application.WebAPI.Domain.Database;
using Devon4Net.Application.WebAPI.Domain.Entities;
using Devon4Net.Application.WebAPI.Domain.RepositoryInterfaces;
using Devon4Net.Domain.UnitOfWork.Service;
using Devon4Net.Domain.UnitOfWork.UnitOfWork;
using Devon4Net.Infrastructure.Common;
using System.Linq.Expressions;

namespace Devon4Net.Application.WebAPI.Business.${variables.component?cap_first}Management.Service
{
    /// <summary>
    /// ${variables.component?cap_first} service implementation
    /// </summary>
    public class ${variables.component?cap_first}Service : Service<CobigenContext>, I${variables.component?cap_first}Service
    {
        private readonly I${variables.entityName?cap_first}Repository _${variables.entityName?cap_first}Repository;

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="uoW"></param>
        public ${variables.component?cap_first}Service(IUnitOfWork<CobigenContext> uoW) : base(uoW)
        {
            _${variables.entityName?cap_first}Repository = uoW.Repository<I${variables.entityName?cap_first}Repository>();
        }

        /// <summary>
        /// Get${variables.entityName?cap_first}ById
        /// </summary>
        /// <param name="id"></param>
        /// <returns>${variables.entityName?cap_first}Dto</returns>
        public async Task<${variables.entityName?cap_first}Dto> Get${variables.entityName?cap_first}ById(long id)
        {
            Devon4NetLogger.Debug($"Get${variables.entityName?cap_first}ById method from service ${variables.entityName?cap_first}Service with value : {id}");
            var ${variables.entityName?uncap_first} = await _${variables.entityName}Repository.Get${variables.entityName?cap_first}ById(id).ConfigureAwait(false);

            if(${variables.entityName?uncap_first} == null)
            {
                throw new ${variables.entityName?cap_first}NotFoundException($"the ${variables.entityName?cap_first} with id:{id} does not exist");
            }

            return ${variables.entityName?cap_first}Converter.EntityToDto(${variables.entityName?uncap_first});
        }

        /// <summary>
        /// Create${variables.entityName?cap_first}
        /// </summary>
        /// <param name="${variables.entityName?uncap_first}Dto"></param>
        /// <returns>${variables.entityName?cap_first}Dto</returns>
        public async Task<${variables.entityName?cap_first}Dto> Create${variables.entityName?cap_first}(${variables.entityName?cap_first}Dto ${variables.entityName?uncap_first}Dto)
        {
            Devon4NetLogger.Debug("Set${variables.entityName?cap_first} method from service ${variables.entityName?cap_first}Service");
            var created${variables.entityName?cap_first} = await _${variables.entityName}Repository.Set${variables.entityName?cap_first}(<#list model.properties as property>${variables.entityName?uncap_first}Dto.${property.name?cap_first}<#if property.type != "string" && property.type != "number" && property.type != "boolean">.Select(x => ${property.name?cap_first}Converter.DtoToEntity(x)).ToArray()</#if><#if property?is_last><#else>,</#if></#list>).ConfigureAwait(false);

            return ${variables.entityName?cap_first}Converter.EntityToDto(created${variables.entityName?cap_first});
        }

        /// <summary>
        /// Delete${variables.entityName?cap_first}ById
        /// </summary>
        /// <param name="id"></param>
        /// <returns>deleted id</returns>
        public async Task<long> Delete${variables.entityName?cap_first}ById(long id)
        {
            Devon4NetLogger.Debug($"Delete${variables.entityName?cap_first}ById method from service ${variables.entityName?cap_first}Service with value : {id}");
            var ${variables.entityName?uncap_first} = await _${variables.entityName}Repository.Get${variables.entityName?cap_first}ById(id).ConfigureAwait(false);

            if (${variables.entityName?uncap_first} == null)
            {
                throw new ${variables.entityName?cap_first}NotFoundException($"The provided Id {id} does not exists");
            }

            return await _${variables.entityName}Repository.Delete${variables.entityName?cap_first}ById(id).ConfigureAwait(false);
        }

        /// <summary>
        /// FindAll${variables.entityName?cap_first}s
        /// </summary>
        /// <param name="predicate"></param>
        /// <returns>List of ${variables.entityName?cap_first}Dto</returns>
        public async Task<IEnumerable<${variables.entityName?cap_first}Dto>> FindAll${variables.entityName?cap_first}s(Expression<Func<${variables.entityName?cap_first}, bool>> predicate = null)
        {
            Devon4NetLogger.Debug("Get${variables.entityName?cap_first} method from service ${variables.entityName?cap_first}Service");
            var ${variables.entityName?uncap_first}s = await _${variables.entityName}Repository.Get${variables.entityName?cap_first}s().ConfigureAwait(false);
            return ${variables.entityName?uncap_first}s.Select(${variables.entityName?cap_first}Converter.EntityToDto);
        }
    }
}