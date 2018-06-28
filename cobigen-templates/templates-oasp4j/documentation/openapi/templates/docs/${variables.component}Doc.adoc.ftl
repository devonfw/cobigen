<#include "/functions.ftl">
// anchor:${model.componentName}:override:anchorend
== Requests of ${model.componentName}

**Component Service Path:** 
....
<#list model.header.servers as server>
  ${server.URI}
</#list>
....
<#-- <#list model.component.paths as path>
  ${OpenApiUtil.print(("Path at: "+path.pathURI))}
  <#list path.operations as op>
    ${OpenApiUtil.print(("  Has operation: "+op.operationId))}
    <#list op.parameters as param>
      <#if param.mediaType??><#assign mediaType=param.mediaType><#else><#assign mediaType="void"></#if>
      <#if param.type??><#assign type=param.type><#else><#assign type="no type"></#if>
      ${OpenApiUtil.print(("    Has parameter: "+param.inQuery?string("?","")+param.inPath?string("{","")+type+param.inPath?string("}","")+param.inHeader?string(" in header","")))}
    </#list>
    <#list op.responses as resp>
      <#if resp.code??><#assign code=resp.code><#else><#assign code="no code"></#if>
      <#if resp.type??><#assign type=resp.type><#else><#assign type="no type"></#if>
      <#if resp.mediaType??><#assign mediaType=resp.mediaType><#else><#assign mediaType="no mediaType"></#if>
      <#if resp.description??><#assign description=resp.description><#else><#assign description="no description"></#if>
      ${OpenApiUtil.print(("    Has response: "+code+", "+type+", "+mediaType+", "+description+", which is "+resp.isEntity?string("an Entity","")+resp.isArray?string("an Array","")+resp.isPaginated?string("paginated","")+resp.isVoid?string("void","")))}
    </#list>
  </#list>
</#list> -->
Component Data
[options="header"]
|===
|Name |Description
|<#if model.componentName??>${model.componentName?cap_first}<#else>-</#if>
|<#if model.description??>${model.description}<#else>-</#if>
|===

<#macro request type>
<#compress>
  <#if hasPathsOfType(type)>
    === ${type} Requests

    [options="header"]
    |===
    |Service Path |Description |Response Type | Response Example | Request Parameter Types | Request Example |Path Parameter
      <#list getPathsOfType(type) as path>
        <#list path.operations as op>
          <#if (op.parameters?size gt 0)>
            <#assign responseType="">
            <#if op.type=type>
              |<#if path.pathURI??>${getServer()}${path.pathURI}<#else>-</#if>
              |<#if op.description??>${op.description}<#else>-</#if>
              |<#if op.responses??><#list op.responses as resp><#if resp.type??>${resp.type} <#else>void<#assign responseType="void"> </#if></#list><#else>-</#if>
              |<#if op.responses??><#if op.responses?size gt 0><#if (responseType!="void")>{<#list op.responses as response><#if response.type??></#if>${OpenApiUtil.getJSONResponse(response)}</#list>}<#else>-</#if><#else>-</#if><#else>-</#if>
              |<#if op.parameters??><#list op.parameters as param>${OpenApiUtil.getParameter(param)} </#list><#else>-</#if>
              |<#if op.parameters??><#if op.parameters?size gt 0>{<#assign moreThanOne=false><#list op.parameters as param><#if moreThanOne>,</#if>${OpenApiUtil.getJSONRequest(param)}<#assign moreThanOne=true></#list>}<#else>void</#if><#else>void</#if>
              |<#assign nrPathParam=0><#if op.parameters??><#if op.parameters?size==1>Yes<#else><#list op.parameters as param><#if param.inPath>${param.name}</#if></#list></#if><#else>-</#if>
            </#if>
          <#else>
            |-|-|-|-|-|-|-
          </#if>
        </#list>
      </#list>
    |===
    <#else>
  </#if>
</#compress>
</#macro>

<#macro mediaTypes resp>
    <#if response.mediaTypes??><#if response.mediaTypes?size gt 0><#list response.mediaTypes as mediaType>${mediaType}</#list><#else>void</#if><#else>void</#if>
</#macro>

<@request "get"/>

<@request "put"/>

<@request "post"/>

<@request "patch"/>

<@request "delete"/>