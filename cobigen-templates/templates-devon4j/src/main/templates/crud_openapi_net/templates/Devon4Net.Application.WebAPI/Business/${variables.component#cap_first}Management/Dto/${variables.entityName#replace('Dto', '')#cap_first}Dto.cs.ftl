<#list model.properties as property>
<#if property.type != "string" && property.type != "number" && property.type != "boolean">
using Devon4Net.Application.WebAPI.Business.${property.name?cap_first}Management.Dto;
</#if>
</#list>

namespace Devon4Net.Application.WebAPI.Business.${variables.entityName?cap_first}Management.Dto
{
    /// <summary>
    /// ${variables.entityName?cap_first}Dto definition
    /// </summary>
    public class ${variables.entityName?cap_first}Dto
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
        <#if property?has_next>

        </#if>
        <#else>
        /// <summary>
        /// the ${property.name?cap_first}
        /// </summary>
        public <#if property.type == "number">long<#elseif property.type == "integer">int<#elseif property.isEntity>${property.type}Dto<#else>${property.type}</#if> <#if property.type?contains("Dto")>${property.name?cap_first}Dto<#else>${property.name?cap_first}</#if> { get; set; }
        </#if>
        <#if property?has_next>

        </#if>
    </#list>
    }
}