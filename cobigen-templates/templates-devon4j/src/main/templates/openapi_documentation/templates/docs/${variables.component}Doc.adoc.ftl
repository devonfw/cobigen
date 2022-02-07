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

<#macro request path>


  <#compress>
    <#list path.operations as op>
      <#if op.operationId??>.${op.operationId}</#if>
      [cols='1s,6m']
      |===
       <@compress single_line=true>
      |<#if op.type??>${DocumentationUtil.getTypeWithAsciidocColour(op.type)}<#else>-</#if> 
      |${getServer()}/<#if path.rootComponent??>${path.rootComponent}/<#if path.version??>${path.version}${path.pathURI}</#if></#if>
      </@compress>
      |Description |<#if op.description??>${op.description}<#else>-</#if>
    
      <#if hasRequestBody(op)>.2+<</#if>|Request 
      a|
        [options='header',cols='1,1,3,5']
        !===
        !Parameter !Type !Constraints !Param Description
        <#if op.parameters?size gt 0>
          <#list op.parameters as param>
            <#assign nrParam=0>
            <#if param.isBody?? && !param.isBody>
              <#assign nrParam=nrParam+1>
              !<#if param??>${OpenApiDocumentationUtil.getParam(param)}<#else>-</#if>
              !<#if param.type??>${param.type}<#else>-</#if>
              !<#if param.constraints??>${OpenApiDocumentationUtil.getConstraintList(param)}<#else>-</#if>
              !<#if param.description??>${param.description}<#else>-</#if>
            </#if>
            <#if nrParam==0>!-!-!-!-</#if>
          </#list>
        <#else>
          !-!-!-!-
        </#if>
        !===
        <#if hasRequestBody(op)>a|**Body**
        ....
        {
          <#assign nothingIn=true>
          <#list op.parameters as param>
            <#assign moreThanOne=false>
            <#if param.isBody?? && param.isBody>
              "${param.name}":"${param.type}"<#if moreThanOne>,</#if>
              <#assign moreThanOne=true>
              <#assign nothingIn=false>
            </#if>
          </#list>
          <#if nothingIn>-</#if>
        }
        ....
        </#if>
      <#if op.responses?size gt 0>
        |Response  a|
        <#if hasResponseBody(op)>
          [options='header',cols='1,4,7']
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
        <#else>
          [options='header',cols='1,4']
          !===
          !Code !Description
        
          <#list op.responses as resp>
            !<#if resp.code??>${resp.code}<#else>-</#if>
            !<#if resp.description??>${resp.description}<#else>-</#if>
          </#list>
          !===
        </#if>
      </#if>
      |===
    </#list>
  </#compress>
</#macro>

<#list model.component.paths as path>
  <@request path/>
</#list>

<#function hasResponseBody operation>
  <#list operation.responses as resp>
    <#if resp.entityRef??>
      <#return true>
    </#if>
  </#list>
  <#return false>
</#function>

<#function hasRequestBody operation>
  <#list operation.parameters as param>
    <#if param.isBody?? && param.isBody>
      <#return true>
    </#if>
  </#list>
  <#return false>
</#function>