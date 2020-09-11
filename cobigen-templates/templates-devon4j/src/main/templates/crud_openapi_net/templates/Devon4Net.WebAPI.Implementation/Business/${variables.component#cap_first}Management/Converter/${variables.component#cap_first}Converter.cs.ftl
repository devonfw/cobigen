using System;
using Devon4Net.WebAPI.Implementation.Domain.Entities;
using Devon4Net.WebAPI.Implementation.Business.${variables.entityName?cap_first}Management.Dto;

namespace Devon4Net.WebAPI.Implementation.Business.${variables.entityName?cap_first}Management.Converter
{
    /// <summary>
    /// ${variables.entityName?cap_first}Converter
    /// </summary>
    public static class ${variables.entityName?cap_first}Converter
    {
        /// <summary>
        /// ModelToDto ${variables.entityName?cap_first} transformation
        /// </summary>
        /// <param name="item"></param>
        /// <returns></returns>
        public static ${variables.entityName?cap_first}ResponseDto ModelToDto(${variables.entityName?cap_first} item)
        {
            if(item == null) return new ${variables.entityName?cap_first}ResponseDto();

            return new ${variables.entityName?cap_first}ResponseDto
            {
                ${variables.entityName}Id = item.${variables.entityName}Id,
            <#list model.properties as property>
                ${property.name?cap_first} = item.${property.name?cap_first}<#if property?is_last><#else>,</#if>
            </#list>
            };
        }
    }
}