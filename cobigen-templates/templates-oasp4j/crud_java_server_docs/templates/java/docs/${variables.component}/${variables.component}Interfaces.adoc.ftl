<#compress>
:toc:

== Requests of ${variables.component?cap_first}

**Component Service Path:** 
....
<#if pojo.annotations.javax_ws_rs_Path??>${variables.domain}/services/rest${pojo.annotations.javax_ws_rs_Path.value}<#else>This should never appear: Bug</#if>
....

Component Data <#-- Table definitions in multiple rows to avoid long lines --> 
[options="header"]
|===
|Name |JavaDoc |Consumes |Produces
|${variables.component?cap_first}
|<#if pojo.javaDoc??>${pojo.javaDoc.comment}<#else>No javaDoc available</#if>
|This application accepts <#if pojo.annotations.javax_ws_rs_Consumes??>${pojo.annotations.javax_ws_rs_Consumes.value} as<#else>any</#if> Media Type
|This application produces <#if pojo.annotations.javax_ws_rs_Produces??>the media type: ${pojo.annotations.javax_ws_rs_Produces.value} <#else>anything</#if>
|===

<#macro request type>
  <#assign annotation="javax.ws.rs.${type}">

  <#if JavaUtil.hasMethodWithAnnotation(classObject,annotation)>

    === ${type} Requests

    [cols="10%,60%,30%", options="header"]
    |===
    |Function name |javaDoc |Service Path
    <#if pojo.methods?has_content>
     <#list pojo.methods as method>
        <#if JavaUtil.hasAnnotation(classObject,method.name,annotation)>
          |${method.name}
          |<#if method.javaDoc??>${method.javaDoc.comment} <#if method.javaDoc.param?? && method.javaDoc.param?contains("{") && method.javaDoc.param?contains("}")>@Param ${method.javaDoc.param}</#if><#else>No JavaDoc available</#if>
          |<#if method.annotations.javax_ws_rs_Path??>${variables.domain}/services/rest
          ${pojo.annotations.javax_ws_rs_Path.value}
          ${method.annotations.javax_ws_rs_Path.value}<#else>No @Path declaration found</#if>
        </#if>
      </#list>
    </#if>
    |===
    <#else> == ${annotation} 
  </#if>
</#macro>


<@request "GET"/>

<@request "PUT"/>

<@request "POST"/>

<@request "PATCH"/>

<@request "DELETE"/>

</#compress>