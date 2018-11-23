// anchor:${variables.component}:override:anchorend
== Requests of ${variables.component?cap_first}

**Component Service Path:** 
....
${JavaDocumentationUtil.getPath(pojo.annotations)}
....

<#-- Determine body format -->
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
|<#if pojo.javaDoc??>${JavaDocumentationUtil.getJavaDocWithoutLink(pojo.javaDoc.comment)}<#else>-</#if>
|===


<#list pojo.methods as method>
  <#assign type=JavaDocumentationUtil.getRequestType(method.annotations)>
  <#if !(type=='-')>
  
  
    <#compress>
      .${method.name}
      [cols='1s,6m']
      |===
      |${DocumentationUtil.getTypeWithAsciidocColour(type)} |${JavaDocumentationUtil.getOperationPath(pojo.annotations,method.annotations)}
      |Description |<#if method.javaDoc??>${JavaDocumentationUtil.getJavaDocWithoutLink(method.javaDoc.comment)}<#else>-</#if>
    
      <#if JavaDocumentationUtil.hasBody(classObject,method.name,false)>.2+<</#if>|Request 
      a|
      [options='header',cols='1,1,1,5']
        !===
        !Parameter !Type !Constraints !Param Description
        ${JavaDocumentationUtil.getParams(classObject,method.name,method.javaDoc)}
        !===
      </#compress>
      <#if JavaDocumentationUtil.hasBody(classObject,method.name,false)>
      
a|**Body** +
${JavaDocumentationUtil.getJSONRequestBody(classObject,method.name)}
      </#if>
      <#if JavaDocumentationUtil.hasBody(classObject,method.name,true)>
      <#compress>
      |Response  a|
        [options='header',cols='1,4,7']
        !===
        !Code !Description !Body
        
        !<#if JavaDocumentationUtil.hasBody(classObject,method.name,true)>200<#else>-</#if>
        !<#if method.javaDoc??><#if method.javaDoc.return??>${JavaDocumentationUtil.getJavaDocWithoutLink(method.javaDoc.return)}<#else>-</#if><#else>-</#if>
        a!
      </#compress>
      
${JavaDocumentationUtil.getJSONResponseBody(classObject,method.name)}
      </#if>
    <#compress>
        !===
      |===
    </#compress>
  </#if>
</#list>

<#function getPathEnding method>
  <#if JavaDocumentationUtil.isNoString(method)>
    <#if pojo.annotations.javax_ws_rs_Path?? && method.annotations.javax_ws_rs_Path??>
      <#return (pojo.annotations.javax_ws_rs_Path.value+method.annotations.javax_ws_rs_Path.value)>
    <#elseif pojo.annotations.org_springframework_web_bind_annotation_RequestMapping?? && method.annotations.org_springframework_web_bind_annotation_RequestMapping??>
      <#return (pojo.annotations.org_springframework_web_bind_annotation_RequestMapping.path+method.annotations.org_springframework_web_bind_annotation_RequestMapping.path)>
    </#if>
  <#else>
    <#if pojo.annotations.javax_ws_rs_Path??>
      <#return pojo.annotations.javax_ws_rs_Path.value>
    <#elseif pojo.annotations.org_springframework_web_bind_annotation_RequestMapping??>
      <#return pojo.annotations.org_springframework_web_bind_annotation_RequestMapping.path>
    </#if>
  </#if>
  <#return "">
</#function>