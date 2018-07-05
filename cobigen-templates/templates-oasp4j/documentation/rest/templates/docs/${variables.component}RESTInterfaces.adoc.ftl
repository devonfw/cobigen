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


<#list pojo.methods as method>
  <#assign type=JavaUtil.getRequestType(method.annotations)>
  <#if !(type=='-')>
  
  
    <#compress>
      .${method.name}
      [cols='1s,6m']
      |===
      |${OaspUtil.getTypeWithAsciidocColour(type)} |${JavaUtil.extractRootPath(classObject)}service/rest/${pojo.annotations.javax_ws_rs_Path.value}${method.annotations.javax_ws_rs_Path.value}
      |Description |<#if method.javaDoc??>${JavaUtil.getJavaDocWithoutLink(method.javaDoc.comment)}<#else>-</#if>
    
      .2+<|Request 
      a|
      [options='header',cols='1,1,1,5']
        !===
        !Parameter !Type !Constraints !Param Description
        ${JavaUtil.getParams(classObject,method.name,method.javaDoc)}
        !===
        a|**Body** +
      </#compress>
      
${JavaUtil.getJSONRequestBody(classObject,method.name)}
      <#compress>
    
      |Response  a|
        [options='header',cols='1,4,7']
        !===
        !Code !Description !Body
        
        !<#if JavaUtil.hasBody(classObject,method.name,true)>200<#else>-</#if>
        !<#if method.javaDoc??><#if method.javaDoc.return??>${JavaUtil.getJavaDocWithoutLink(method.javaDoc.return)}<#else>-</#if><#else>-</#if>
        a!
      </#compress>
      
${JavaUtil.getJSONResponseBody(classObject,method.name)}
      <#compress>
        !===
      |===
    </#compress>
  </#if>
</#list>