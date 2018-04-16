<#assign test=true>
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

GET requests:

[options="header"]
|===
|Function name |javaDoc |Service Path
<#list pojo.methods as request>
  <#if request.annotations.javax_ws_rs_GET??>
  <#assign test=false>
|${request.name} 
|<#if request.javaDoc??>${request.javaDoc.comment} <#if request.javaDoc.param?contains("{") && request.javaDoc.param?contains("}")>@Param ${request.javaDoc.param}</#if><#else>No JavaDoc available</#if> 
|<#if request.annotations.javax_ws_rs_Path??>${variables.domain}/services/rest${pojo.annotations.javax_ws_rs_Path.value}${request.annotations.javax_ws_rs_Path.value}<#else>This should never show: Bug</#if>
  </#if>
</#list>
<#if test>
3+|No requests of this type
</#if>
<#assign test=true>
|===

POST requests:

[options="header"]
|===
|Function name |javaDoc |Service Path
<#list pojo.methods as request>
  <#if request.annotations.javax_ws_rs_POST??>
  <#assign test=false>
|${request.name} 
|<#if request.javaDoc??>${request.javaDoc.comment} <#if request.javaDoc.param?contains("{") && request.javaDoc.param?contains("}")>@Param ${request.javaDoc.param}</#if><#else>No JavaDoc available</#if> 
|<#if request.annotations.javax_ws_rs_Path??>${variables.domain}/services/rest${pojo.annotations.javax_ws_rs_Path.value}${request.annotations.javax_ws_rs_Path.value}<#else>This should never show: Bug</#if>
  </#if>
</#list>
<#if test>
3+|No requests of this type
</#if>
<#assign test=true>
|===

PUT requests:

[options="header"]
|===
|Function name |javaDoc |Service Path
<#list pojo.methods as request>
  <#if request.annotations.javax_ws_rs_PUT??>
  <#assign test=false>
|${request.name} 
|<#if request.javaDoc??>${request.javaDoc.comment} <#if request.javaDoc.param?contains("{") && request.javaDoc.param?contains("}")>@Param ${request.javaDoc.param}</#if><#else>No JavaDoc available</#if> 
|<#if request.annotations.javax_ws_rs_Path??>${variables.domain}/services/rest${pojo.annotations.javax_ws_rs_Path.value}${request.annotations.javax_ws_rs_Path.value}<#else>This should never show: Bug</#if>
  </#if>
</#list>
<#if test>
3+|No requests of this type
</#if>
<#assign test=true>
|===

DELETE requests:

[options="header"]
|===
|Function name |javaDoc |Service Path
<#list pojo.methods as request>
  <#if request.annotations.javax_ws_rs_DELETE??>
  <#assign test=false>
|${request.name} 
|<#if request.javaDoc??>${request.javaDoc.comment} <#if request.javaDoc.param?contains("{") && request.javaDoc.param?contains("}")>@Param ${request.javaDoc.param}</#if><#else>No JavaDoc available</#if> 
|<#if request.annotations.javax_ws_rs_Path??>${variables.domain}/services/rest${pojo.annotations.javax_ws_rs_Path.value}${request.annotations.javax_ws_rs_Path.value}<#else>This should never show: Bug</#if>
  </#if>
</#list>
<#if test>
3+|No requests of this type
</#if>
<#assign test=true>
|===

PATCH requests:

[options="header"]
|===
|Function name |javaDoc |Service Path
<#list pojo.methods as request>
  <#if request.annotations.javax_ws_rs_PATCH??>
  <#assign test=false>
|${request.name} 
|<#if request.javaDoc??>${request.javaDoc.comment} <#if request.javaDoc.param?contains("{") && request.javaDoc.param?contains("}")>@Param ${request.javaDoc.param}</#if><#else>No JavaDoc available</#if> 
|<#if request.annotations.javax_ws_rs_Path??>${variables.domain}/services/rest${pojo.annotations.javax_ws_rs_Path.value}${request.annotations.javax_ws_rs_Path.value}<#else>This should never show: Bug</#if>
  </#if>
</#list>
<#if test>
3+|No requests of this type
</#if>
<#assign test=true>
|===