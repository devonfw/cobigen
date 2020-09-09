using System;
using System.Collections.Generic;

namespace Devon4Net.WebAPI.Implementation.Domain.Entities
{
    /// <summary>
    /// Entity class for ${variables.entityName}
    /// </summary>
    public class ${variables.entityName}
    {
        /// <summary>
        /// Id
        /// </summary>
        public long ${variables.entityName?cap_first}Id { get; set; }
    <#list model.properties as property>
        <#if property.isCollection>

        /// <summary>
        /// ${property.name}
        /// </summary>
        public <#if property.type == "number">long<#elseif property.type == "integer">int<#else>${property.type?replace("Dto", "")}</#if>[] ${property.name?cap_first} { get; set; }
        <#else>

        /// <summary>
        /// ${property.name}
        /// </summary>
        public <#if property.type == "number">long<#elseif property.type == "integer">int<#else>${property.type?replace("Dto", "")}</#if> ${property.name?cap_first} { get; set; }
        </#if>
    </#list>
    }
}