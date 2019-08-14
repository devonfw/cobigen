using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Threading.Tasks;
using AutoMapper;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Cors;
using Microsoft.AspNetCore.Mvc;
using Devon4Net.Infrastructure.MVC.Controller;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using Devon4Net.Business.Common.${variables.component?cap_first}Management.Service;
using Devon4Net.Business.Common.Dto;

namespace Devon4Net.Business.Common.${variables.component?cap_first}Management.Controller
{
    [EnableCors("CorsPolicy")]
    public class ${variables.component?cap_first}Controller : Devon4NetController
    {
        I${variables.component?cap_first}Service _${variables.component?cap_first}Service;

        public ${variables.component?cap_first}Controller(IMapper mapper, ILogger<${variables.component?cap_first}Controller> logger, I${variables.component?cap_first}Service ${variables.component?cap_first}Service) : base (logger,mapper)
        {               
            _${variables.component?cap_first}Service = ${variables.component?cap_first}Service;
        }

        /// <summary>
        ///  Get ${variables.entityName?cap_first}
        /// </summary>
        /// <param name="id"></param>
        /// <response code="201">Ok.</response>
        /// <response code="400">Bad request. Parser data error.</response>
        /// <response code="401">Unathorized. Autentication fail</response>
        /// <response code="403">Forbidden. Authorization error.</response>
        /// <response code="500">Internal Server Error. The search process ended with error.</response>
        [HttpGet]
        [HttpOptions]
        [Route("/${variables.entityName?lower_case}/{id}/")]
        [AllowAnonymous]
        [EnableCors("CorsPolicy")]
        public async Task<IActionResult> Get${variables.entityName?cap_first}(long id){
            try
            {
                var result = await _${variables.component?cap_first}Service.Get${variables.entityName?cap_first}(id);
                var json = JsonConvert.SerializeObject(result);
                Console.WriteLine(json);
                return StatusCode((int)HttpStatusCode.OK, json);
            }
            catch (Exception ex)
            {
                Logger.LogDebug($"{ex.Message} : {ex.InnerException}");
                throw ex;
            }
        }


        /// <summary>
        ///  Save ${variables.entityName?cap_first}
        /// </summary>
        /// <param name="${variables.entityName}Dto"></param>
        /// <response code="201">Ok.</response>
        /// <response code="400">Bad request. Parser data error.</response>
        /// <response code="401">Unathorized. Autentication fail</response>
        /// <response code="403">Forbidden. Authorization error.</response>
        /// <response code="500">Internal Server Error. The search process ended with error.</response>
        [HttpPost]
        [HttpOptions]
        [Route("/${variables.entityName?lower_case}/")]
        [AllowAnonymous]
        [EnableCors("CorsPolicy")]
        public async Task<IActionResult> Save${variables.entityName?cap_first}([FromBody]${variables.entityName?cap_first}Dto ${variables.entityName?uncap_first}Dto){
            try
            {
                var result = await _${variables.component?cap_first}Service.Save${variables.entityName?cap_first}(${variables.entityName?uncap_first}Dto);
                var json = JsonConvert.SerializeObject(result);
                Console.WriteLine(json);
                return StatusCode((int)HttpStatusCode.OK, json);
            }
            catch (Exception ex)
            {
                Logger.LogDebug($"{ex.Message} : {ex.InnerException}");
                throw ex;
            }
        }
        
        /// <summary>
        ///  Delete ${variables.entityName?cap_first}
        /// </summary>
        /// <param name="id"></param>
        /// <response code="201">Ok.</response>
        /// <response code="400">Bad request. Parser data error.</response>
        /// <response code="401">Unathorized. Autentication fail</response>
        /// <response code="403">Forbidden. Authorization error.</response>
        /// <response code="500">Internal Server Error. The search process ended with error.</response>
        [HttpDelete]
        [Route("/${variables.entityName?lower_case}/{id}/")]
        [AllowAnonymous]
        [EnableCors("CorsPolicy")]
        public async Task<IActionResult> Delete${variables.entityName?cap_first}(long id){
            try
            {
                var result = await _${variables.component?cap_first}Service.Delete${variables.entityName?cap_first}(id);
                var json = JsonConvert.SerializeObject(result);
                Console.WriteLine(json);
                return StatusCode((int)HttpStatusCode.OK, json);
            }
            catch (Exception ex)
            {
                Logger.LogDebug($"{ex.Message} : {ex.InnerException}");
                throw ex;
            }
        }
        
        /// <summary>
        ///  Get all ${variables.entityName?cap_first}
        /// </summary>
        /// <param name="id"></param>
        /// <response code="201">Ok.</response>
        /// <response code="400">Bad request. Parser data error.</response>
        /// <response code="401">Unathorized. Autentication fail</response>
        /// <response code="403">Forbidden. Authorization error.</response>
        /// <response code="500">Internal Server Error. The search process ended with error.</response>
        [HttpGet]
        [HttpOptions]
        [Route("/${variables.entityName?lower_case}/search/")]
        [AllowAnonymous]
        [EnableCors("CorsPolicy")]
        public async Task<IActionResult> FindAll${variables.entityName?cap_first}s(){
            try
            {
                var result = await _${variables.component?cap_first}Service.FindAll${variables.entityName?cap_first}s();
                var json = JsonConvert.SerializeObject(result);
                Console.WriteLine(json);
                return StatusCode((int)HttpStatusCode.OK, json);
            }
            catch (Exception ex)
            {
                Logger.LogDebug($"{ex.Message} : {ex.InnerException}");
                throw ex;
            }
        }
    
    <#assign pathInUse = "/" + variables.entityName?lower_case + "/{id}/,/" + variables.entityName?lower_case + "/search/">
    <#list model.component.paths as path>
	    <#list path.operations as operation>
            <#list operation.parameters as parameter>
                <#if !DevonUtil.isCrudOperation(operation.operationId!null, variables.entityName?cap_first)>
                    <#if !pathInUse?contains(path.pathURI)>

        /// <summary>
        /// </summary>
        /// <param name="id"></param>
        /// <response code="201">Ok.</response>
        /// <response code="400">Bad request. Parser data error.</response>
        /// <response code="401">Unathorized. Autentication fail</response>
        /// <response code="403">Forbidden. Authorization error.</response>
        /// <response code="500">Internal Server Error. The search process ended with error.</response>
        [Http${operation.type?cap_first}]
        [HttpOptions]
        [Route("${path.pathURI}")]
        [AllowAnonymous]
        [EnableCors("CorsPolicy")]
        public async Task<IActionResult> ${OpenApiUtil.printServiceOperationName(operation, path.pathURI)} (<#if parameter.isEntity>${parameter.type}Dto<#elseif parameter.type == "integer">int<#elseif parameter.type == "number">long</#if> ${parameter.name}<#if parameter?has_next>, </#if>)
        {
            try
            {
                throw new NotImplementedException();
            }
            catch (Exception ex)
            {
                Logger.LogDebug($"{ex.Message} : {ex.InnerException}");
                throw ex;
            }
        }
                </#if>
            </#if>
        </#list>
  	</#list>
 </#list>
    }
}
    






















    