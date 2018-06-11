<#include "/functions.ftl">
// anchor:${model.componentName}:override:anchorend
== Requests of ${model.componentName}

**Component Service Path:** 
....
<#list model.header.servers as server>
  ${server.URI}
</#list>
....

Component Data
[options="header"]
|===
|Name |Description
|${model.componentName?cap_first}
|${model.description}
|===

<#macro request type>
<#compress>
  <#if hasPathsOfType(type)>
    === ${type} Requests

    [options="header"]
    |===
    |Service Path |Description |Response Type |Request Type |Path Parameter
      <#list getPathsOfType(type) as path>
        <#list path.operations as op>
          <#assign respList=false>
          <#if op.type=type>
            |<#if path.pathURI??>${getServer()}${path.pathURI}<#else>-</#if>
            |<#if op.description??>${op.description}<#else>-</#if>
            |<#if op.responses??><#if op.responses?size gt 1><<${op.operationId}-ResponseList,Response Type List>><#assign respList=true><#assign resps=op.responses><#assign operation=op><#else><@mediaTypes op.responses/></#if><#else>-</#if>
            |<#if op.parameters??><#list op.parameters as param>${getParameter(param,"query")} </#list><#else>-</#if>
            |<#if op.parameters??><#list op.parameters as param>${getParameter(param,"path")}; </#list><#else>-</#if>
          </#if>
        </#list>
      </#list>
    |===
    
    <#assign respListExists=true>    
    <#if respList>
      <#if respListExists>
        === Response types listings
      </#if>
      ==== Response types of ${operation.operationId}
      [[${op.operationId}-ResponseList]]
      [options="header"]
      |===
      |Response type |Description
      <#list resps as resp>
        | <@mediaTypes resp.mediaTypes/>
        | ${resp.description}
      </#list>
      |===
    </#if>
    <#else>
  </#if>
</#compress>
</#macro>

<#macro mediaTypes mediaTypes>
  <#if mediaTypes??><#list mediaTypes as mediaType>${mediaType}</#list><#else>void</#if>
</#macro>

<@request "get"/>

<@request "put"/>

<@request "post"/>

<@request "patch"/>

<@request "delete"/>