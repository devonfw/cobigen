// anchor:${variables.component}:override:anchorend
== Requests of ${variables.component?cap_first}

**Component Service Path:** 
....
${JavaUtil.extractRootPath(classObject)}<#if pojo.annotations.javax_ws_rs_Path??>${variables.domain}/services/rest${pojo.annotations.javax_ws_rs_Path.value}<#else>This REST service has no path</#if>
....

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
    |Service Path |Description |Response Type |Request Type |Path Parameter
    <#if pojo.methods?has_content>
     <#list pojo.methods as method>
        <#if JavaUtil.hasAnnotation(classObject,method.name,annotation)>
          |${JavaUtil.extractRootPath(classObject)}<#if method.annotations.javax_ws_rs_Path??>${variables.domain}/services/rest${pojo.annotations.javax_ws_rs_Path.value}${method.annotations.javax_ws_rs_Path.value}<#else>-</#if>
          |<#if method.javaDoc??>${JavaUtil.getJavaDocWithoutLink(method.javaDoc.comment)}<#else>-</#if>
          |${JavaUtil.getReturnType(classObject,method.name)}
          |${JavaUtil.getParams(classObject,method.name)}
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
