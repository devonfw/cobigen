<#include "/functions.ftl">
// anchor:${model.componentName}:override:anchorend
== Operations of ${model.componentName}

**Component Service Path** 
....
<#list model.header.servers as server>
  ${server.URI}
</#list>
....

Component Data
[options="header"]
|===
|Name |Description
|<#if model.componentName??>${model.componentName?cap_first}<#else>-</#if>
|<#if model.description??>${model.description}<#else>-</#if>
|===

=== Requests

<#macro request path>


  <#compress>
    <#list path.operations as op>
      .${op.operationId}
      [cols='1s,3m']
      |===
      |<#if op.type??>${OaspUtil.getTypeWithAsciidocColour(op.type)}<#else>-</#if> |${getServer()}${path.pathURI}
      |Description |<#if op.description??>${op.description}<#else>-</#if>
    
      .2+<|Request 
      a|
        [options='header',cols='1,1,3,5']
        !===
        !Parameter !Type !Constraints !Param Description
        <#if op.parameters?size gt 0>
          <#list op.parameters as param>
            <#assign nrParam=0>
            <#if param.isBody?? && !param.isBody>
              <#assign nrParam=nrParam+1>
              !<#if param??>${OpenApiUtil.getParam(param)}<#else>-</#if>
              !<#if param.type??>${param.type}<#else>-</#if>
              !<#if param.constraints??>${OpenApiUtil.getConstraintList(param)}<#else>-</#if>
              !<#if param.description??>${param.description}<#else>-</#if>
            </#if>
            <#if nrParam==0>!-!-!-!-</#if>
          </#list>
        <#else>
          !-!-!-!-
        </#if>
        !===
        a|**Body**
        ....
        {
          <#assign nothingIn=true>
          <#list op.parameters as param>
            <#assign moreThanOne=false>
            <#if param.isBody>
              "${param.name}":"${param.type}"<#if moreThanOne>,</#if>
              <#assign moreThanOne=true>
              <#assign nothingIn=false>
            </#if>
          </#list>
          <#if nothingIn>-</#if>
        }
        ....
    
      |Response codes  a|
        [options='header',cols='1,5,5']
        !===
        !Code !Description !Body
        
        <#list op.responses as resp>
        !<#if resp.code??>${resp.code}<#else>-</#if>
        !<#if resp.description??>${resp.description}<#else>-</#if>
        a!
        <#if resp.entityRef??>
          <#assign entity=resp.entityRef>
          ....
          {
            <#list entity.properties as prop>
              "${prop.name}":"${prop.type}"
            </#list>
          }
          ....
        <#elseif resp.type??>${resp.type}<#else>-</#if>
        </#list>
        !===
      |===
    </#list>
  </#compress>
</#macro>

<#list model.component.paths as path>
  <@request path/>
</#list>