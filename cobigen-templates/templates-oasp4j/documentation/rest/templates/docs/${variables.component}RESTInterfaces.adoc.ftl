// anchor:${variables.component}:override:anchorend
== Requests of ${variables.component?cap_first}

**Component Service Path:** 
....
${JavaUtil.extractRootPath(classObject)}<#if pojo.annotations.javax_ws_rs_Path??>${variables.domain}/services/rest${pojo.annotations.javax_ws_rs_Path.value}<#else>This REST service has no path</#if>
....

<#if pojo.annotations.javax_ws_rs_Consumes??>
  <#assign inputType=pojo.annotations.javax_ws_rs_Consumes.value>
  <#if inputType?contains("JSON")>
    <#assign inputType="JSON">
    <#elseif inputType?contains("XML")>
    <#assign inputType="XML">
  </#if>
</#if>
<#if pojo.annotations.javax_ws_rs_Produces??>
  <#assign outputType=pojo.annotations.javax_ws_rs_Produces.value>
  <#if outputType?contains("JSON")>
    <#assign outputType="JSON">
    <#elseif outputType?contains("XML")>
    <#assign outputType="XML">
  </#if>
</#if>

Component Data 
[options="header"]
|===
|Name |Description
|${variables.component?cap_first}
|<#if pojo.javaDoc??>${JavaUtil.getJavaDocWithoutLink(pojo.javaDoc.comment)}<#else>-</#if>
|===

<#macro request type>
<#compress>
  <#assign annotation="javax.ws.rs.${type}">

  <#if JavaUtil.hasMethodWithAnnotation(classObject,annotation)>

    === ${type?lower_case} Requests

    [options="header"]
    |===
    |Service Path |Description |Response Type | Response Example | Request Type | Request Example |Path Parameter
    <#if pojo.methods?has_content>
     <#list pojo.methods as method>
        <#if JavaUtil.hasAnnotation(classObject,method.name,annotation)>
          |${JavaUtil.extractRootPath(classObject)}<#if method.annotations.javax_ws_rs_Path??>${variables.domain}/services/rest${pojo.annotations.javax_ws_rs_Path.value}${method.annotations.javax_ws_rs_Path.value}<#else>-</#if>
          |<#if method.javaDoc??>${JavaUtil.getJavaDocWithoutLink(method.javaDoc.comment)}<#else>-</#if>
          |${JavaUtil.getReturnType(classObject,method.name)}
          |<#if outputType??><#if (outputType=="JSON")>${JavaUtil.getJSONResponse(classObject,method.name)}<#elseif (outputType=="XML")>${JavaUtil.getXMLFormat(classObject,method.name)}<#else>-</#if><#else>-</#if>
          |${JavaUtil.getParams(classObject,method.name)}
          |<#if inputType??><#if (inputType=="JSON")>${JavaUtil.getJSONRequest(classObject,method.name)}<#elseif (inputType=="XML")>${JavaUtil.getXMLRequest(classObject,method.name)}<#else>-</#if><#else>-</#if>
          |<#if method.javaDoc??><#if method.javaDoc.param?contains(JavaUtil.getPathParam(classObject,method.name))>${JavaUtil.getJavaDocWithoutLink(method.javaDoc.param)}<#else>-</#if><#else>-</#if>
        </#if>
      </#list>
    </#if>
    |===
    <#else>
  </#if>
</#compress>  
</#macro>


<@request "GET"/>

<@request "PUT"/>

<@request "POST"/>

<@request "PATCH"/>

<@request "DELETE"/>
