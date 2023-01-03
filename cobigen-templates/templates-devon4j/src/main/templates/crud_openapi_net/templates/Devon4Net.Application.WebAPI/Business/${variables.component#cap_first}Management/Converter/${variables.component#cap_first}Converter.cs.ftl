using Devon4Net.Application.WebAPI.Business.${variables.entityName?cap_first}Management.Dto;
using Devon4Net.Application.WebAPI.Domain.Entities;
<#list model.properties as property>
<#if property.type != "string" && property.type != "number" && property.type != "boolean">
using Devon4Net.Application.WebAPI.Business.${property.name?cap_first}Management.Converter;
</#if>
</#list>

namespace Devon4Net.Application.WebAPI.Business.${variables.entityName?cap_first}Management.Converter
{
    /// <summary>
    /// ${variables.entityName?cap_first}Converter
    /// </summary>
    public static class ${variables.entityName?cap_first}Converter
    {
        /// <summary>
        /// EntityToDto ${variables.entityName?cap_first} transformation
        /// </summary>
        /// <param name="item"></param>
        /// <returns></returns>
        public static ${variables.entityName?cap_first}Dto EntityToDto(${variables.entityName?cap_first} item)
        {
            if(item == null) return new ${variables.entityName?cap_first}Dto();

            return new ${variables.entityName?cap_first}Dto
            {
                ${variables.entityName}Id = item.${variables.entityName}Id,
            <#list model.properties as property>
                <#if property.type != "string" && property.type != "number" && property.type != "boolean">
                ${property.name?cap_first} = item.${property.name?cap_first}.Select(x => ${property.name?cap_first}Converter.EntityToDto(x)).ToArray()<#if property?is_last><#else>,</#if>
                <#else>
                ${property.name?cap_first} = item.${property.name?cap_first}<#if property?is_last><#else>,</#if>
                </#if>
            </#list>
            };
        }

        /// <summary>
        /// DtoToEntity ${variables.entityName?cap_first} transformation
        /// </summary>
        /// <param name="item"></param>
        /// <returns></returns>
        public static ${variables.entityName?cap_first} DtoToEntity(${variables.entityName?cap_first}Dto item)
        {
            if (item == null) return new ${variables.entityName?cap_first}();

            return new ${variables.entityName?cap_first}
            {
                ${variables.entityName}Id = item.${variables.entityName}Id,
                <#list model.properties as property>
                <#if property.type != "string" && property.type != "number" && property.type != "boolean">
                ${property.name?cap_first} = item.${property.name?cap_first}.Select(x => ${property.name?cap_first}Converter.DtoToEntity(x)).ToArray()<#if property?is_last><#else>,</#if>
                <#else>
                ${property.name?cap_first} = item.${property.name?cap_first}<#if property?is_last><#else>,</#if>
                </#if>
                </#list>
            };
        }


    }
}