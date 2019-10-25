using System;

namespace Devon4Net.WebAPI.Implementation.Business.Dto
{
    public class ${variables.entityName?replace("Dto", "")?cap_first}Dto
    {
    <#list model.properties as property>
        <#if property.isCollection>
        public <#if property.type == "number">long<#elseif property.type == "integer">int<#elseif property.isEntity>${property.type}Dto<#else>${property.type}</#if>[] <#if property.type?contains("Dto")>${property.name?cap_first}Dto<#else>${property.name?cap_first}</#if> { get; set; }
        <#else>
        public <#if property.type == "number">long<#elseif property.type == "integer">int<#elseif property.isEntity>${property.type}Dto<#else>${property.type}</#if> <#if property.type?contains("Dto")>${property.name?cap_first}Dto<#else>${property.name?cap_first}</#if> { get; set; }
        </#if>
    </#list>
    }
}