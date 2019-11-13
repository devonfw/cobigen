using System;
using System.Collections.Generic;
using Devon4Net.WebAPI.Implementation.Domain.Entities;

namespace Devon4Net.WebAPI.Implementation.Domain.Entities
{
    public class ${variables.entityName}
    {
        public ${variables.entityName}(){

        }

        public long ${variables.entityName?cap_first}Id;
    <#list model.properties as property>
        <#if property.isCollection>
        public <#if property.type == "number">long<#elseif property.type == "integer">int<#else>${property.type?replace("Dto", "")}</#if>[] ${property.name?cap_first} { get; set; }
        <#else>
        public <#if property.type == "number">long<#elseif property.type == "integer">int<#else>${property.type?replace("Dto", "")}</#if> ${property.name?cap_first} { get; set; }
        </#if>
    </#list>
    }
}