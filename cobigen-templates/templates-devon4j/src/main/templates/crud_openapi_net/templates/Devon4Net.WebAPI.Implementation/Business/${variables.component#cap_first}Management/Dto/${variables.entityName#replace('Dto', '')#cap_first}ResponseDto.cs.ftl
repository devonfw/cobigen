using System;

namespace Devon4Net.WebAPI.Implementation.Business.${variables.entityName?cap_first}Management.Dto
{
    /// <summary>
    /// ${variables.entityName?cap_first}ResponseDto definition
    /// </summary>
    public class ${variables.entityName?replace("Dto", "")?cap_first}ResponseDto
    {
        /// <summary>
        /// the ${variables.entityName?cap_first}Id
        /// </summary>
        public long ${variables.entityName?cap_first}Id { get; set; }
    <#list model.properties as property>
        <#if property.isCollection>
        
        /// <summary>
        /// the ${property.name?cap_first}
        /// </summary>
        public <#if property.type == "number">long<#elseif property.type == "integer">int<#elseif property.isEntity>${property.type}Dto<#else>${property.type}</#if>[] <#if property.type?contains("Dto")>${property.name?cap_first}Dto<#else>${property.name?cap_first}</#if> { get; set; }
        <#else>
        
        /// <summary>
        /// the ${property.name?cap_first}
        /// </summary>
        public <#if property.type == "number">long<#elseif property.type == "integer">int<#elseif property.isEntity>${property.type}Dto<#else>${property.type}</#if> <#if property.type?contains("Dto")>${property.name?cap_first}Dto<#else>${property.name?cap_first}</#if> { get; set; }
        </#if>
    </#list>
    }
}