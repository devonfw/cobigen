using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Devon4Net.Infrastructure.Log;
using Microsoft.AspNetCore.Mvc;
using Devon4Net.WebAPI.Implementation.Business.${variables.component?cap_first}Management.Service;
using Devon4Net.WebAPI.Implementation.Business.${variables.entityName?cap_first}Management.Dto;

namespace Devon4Net.WebAPI.Implementation.Business.${variables.component?cap_first}Management.Controller
{
    /// <summary>
    /// ${variables.component?cap_first} controller
    /// </summary>
    [ApiController]
    public class ${variables.component?cap_first}Controller : ControllerBase
    {
        private readonly I${variables.component?cap_first}Service _${variables.component?cap_first}Service;

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="${variables.component?uncap_first}Service"></param>
        public ${variables.component?cap_first}Controller(I${variables.component?cap_first}Service ${variables.component}Service)
        {
            _${variables.component?cap_first}Service = ${variables.component}Service;
        }

        /// <summary>
        ///  Get ${variables.entityName?cap_first}
        /// </summary>
        /// <param name="id"></param>
        /// <response code="200">Ok.</response>
        /// <response code="400">Bad request. Parser data error.</response>
        /// <response code="404">Forbidden. Authorization error.</response>
        /// <response code="500">Internal Server Error. The search process ended with error.</response>
        [HttpGet]
        [Route("/${variables.entityName?lower_case}/{id}/")]
        [ProducesResponseType(typeof(${variables.entityName?cap_first}Dto), 200)]
        [ProducesResponseType(400)]
        [ProducesResponseType(404)]
        [ProducesResponseType(500)]
        public async Task<IActionResult> Get${variables.entityName?cap_first}(long id)
        {
            Devon4NetLogger.Debug("Executing Get${variables.entityName?cap_first} from controller ${variables.component?cap_first}Controller");
            return Ok(await _${variables.component?cap_first}Service.Get${variables.entityName?cap_first}ById(id).ConfigureAwait(false));
        }

        /// <summary>
        ///  Create ${variables.entityName?cap_first}
        /// </summary>
        /// <param name="${variables.entityName?uncap_first}Dto"></param>
        /// <response code="200">Ok.</response>
        /// <response code="400">Bad request. Parser data error.</response>
        /// <response code="404">Forbidden. Authorization error.</response>
        /// <response code="500">Internal Server Error. The search process ended with error.</response>
        [HttpPost]
        [Route("/${variables.entityName?lower_case}/")]
        [ProducesResponseType(typeof(${variables.entityName?cap_first}Dto), 200)]
        [ProducesResponseType(400)]
        [ProducesResponseType(404)]
        [ProducesResponseType(500)]
        public async Task<IActionResult> Save${variables.entityName?cap_first}([FromBody]${variables.entityName?cap_first}Dto ${variables.entityName?uncap_first}Dto)
        {
            Devon4NetLogger.Debug("Executing Save${variables.entityName?cap_first} from controller ${variables.component?cap_first}Controller");
            return Ok(await _${variables.component?cap_first}Service.Create${variables.entityName?cap_first}(${variables.entityName?uncap_first}Dto).ConfigureAwait(false));
        }

        /// <summary>
        ///  Delete ${variables.entityName?cap_first}
        /// </summary>
        /// <param name="id"></param>
        /// <response code="200">Ok.</response>
        /// <response code="400">Bad request. Parser data error.</response>
        /// <response code="404">Forbidden. Authorization error.</response>
        /// <response code="500">Internal Server Error. The search process ended with error.</response>
        [HttpDelete]
        [Route("/${variables.entityName?lower_case}/{id}/")]
        [ProducesResponseType(typeof(long), 200)]
        [ProducesResponseType(400)]
        [ProducesResponseType(404)]
        [ProducesResponseType(500)]
        public async Task<IActionResult> Delete${variables.entityName?cap_first}(long id)
        {
            Devon4NetLogger.Debug("Executing Get${variables.entityName?cap_first} from controller ${variables.component?cap_first}Controller");
            return Ok(await _${variables.component?cap_first}Service.Delete${variables.entityName?cap_first}ById(id).ConfigureAwait(false));
        }

        /// <summary>
        ///  Get all ${variables.entityName?cap_first}
        /// </summary>
        /// <response code="200">Ok.</response>
        /// <response code="400">Bad request. Parser data error.</response>
        /// <response code="404">Forbidden. Authorization error.</response>
        /// <response code="500">Internal Server Error. The search process ended with error.</response>
        [HttpGet]
        [Route("/${variables.entityName?lower_case}/search/")]
        [ProducesResponseType(typeof(List<${variables.entityName?cap_first}Dto>), 200)]
        [ProducesResponseType(400)]
        [ProducesResponseType(404)]
        [ProducesResponseType(500)]
        public async Task<IActionResult> FindAll${variables.entityName?cap_first}s()
        {
            Devon4NetLogger.Debug("Executing FindAll${variables.entityName?cap_first} from controller ${variables.component?cap_first}Controller");    
            return Ok(await _${variables.component?cap_first}Service.FindAll${variables.entityName?cap_first}s().ConfigureAwait(false));
        }
    <#assign pathInUse = "/" + variables.entityName?lower_case + "/{id}/,/" + variables.entityName?lower_case + "/search/">
    <#list model.component.paths as path>
	    <#list path.operations as operation>
            <#list operation.parameters as parameter>
                <#if !DevonfwUtil.isCrudOperation(operation.operationId!null, variables.entityName?cap_first)>
                    <#if !pathInUse?contains(path.pathURI)>

        /// <summary>
        /// </summary>
        /// <param name="<#if parameter.isEntity>${parameter.type?uncap_first}Dto<#elseif parameter.type == "integer">int<#elseif parameter.type == "number">long</#if>"></param>
        /// <response code="200">Ok.</response>
        /// <response code="400">Bad request. Parser data error.</response>
        /// <response code="404">Forbidden. Authorization error.</response>
        /// <response code="500">Internal Server Error. The search process ended with error.</response>
        [Http${operation.type?cap_first}]
        [Route("${path.pathURI}")]
        [ProducesResponseType(200)]
        [ProducesResponseType(400)]
        [ProducesResponseType(404)]
        [ProducesResponseType(500)]
        public IActionResult ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)?cap_first} (<#if parameter.isEntity>${parameter.type}Dto<#elseif parameter.type == "integer">int<#elseif parameter.type == "number">long</#if> ${parameter.name}<#if parameter.isEntity>Dto</#if><#if parameter?has_next>, </#if>)
        {
          Devon4NetLogger.Debug("Executing ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)?cap_first} from controller ${variables.component?cap_first}Controller");
          throw new NotImplementedException();
        }
                </#if>
            </#if>
        </#list>
  	</#list>
 </#list>
    }
}
