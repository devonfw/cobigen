using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using System.Threading.Tasks;
using Devon4Net.WebAPI.Implementation.Domain.Entities;
using Devon4Net.WebAPI.Implementation.Business.${variables.entityName?cap_first}Management.Dto;

namespace Devon4Net.WebAPI.Implementation.Business.${variables.component?cap_first}Management.Service
{
    /// <summary>
    /// I${variables.component?cap_first}Service
    /// </summary>
    public interface I${variables.component?cap_first}Service
    {
        /// <summary>
        /// Get${variables.entityName?cap_first}ById
        /// </summary>
        /// <param name="id"></param>
        /// <returns>${variables.entityName?cap_first}ResponseDto</returns>
        Task<${variables.entityName?cap_first}ResponseDto> Get${variables.entityName?cap_first}ById(long id);

        /// <summary>
        /// Create${variables.entityName?cap_first}
        /// </summary>
        /// <param name="${variables.entityName?uncap_first}Dto"></param>
        /// <returns>${variables.entityName?cap_first}ResponseDto</returns>
        Task<${variables.entityName?cap_first}ResponseDto> Create${variables.entityName?cap_first}(${variables.entityName?cap_first}Dto ${variables.entityName?uncap_first}Dto);

        /// <summary>
        /// Delete${variables.entityName?cap_first}ById
        /// </summary>
        /// <param name="id"></param>
        /// <returns>deleted id</returns>
        Task<long> Delete${variables.entityName?cap_first}ById(long id);

        /// <summary>
        /// FindAll${variables.entityName?cap_first}s
        /// </summary>
        /// <param name="predicate"></param>
        /// <returns>List of ${variables.entityName?cap_first}ResponseDto</returns>
        Task<IEnumerable<${variables.entityName?cap_first}ResponseDto>> FindAll${variables.entityName?cap_first}s(Expression<Func<${variables.entityName?cap_first}, bool>> predicate = null);
    }
}