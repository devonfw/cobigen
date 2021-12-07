<#function getReturnType operation isServiceResponse>
  <#assign result=[]>
  <#assign differentMediaTypes="">
  <#list operation.responses as response>
    <#list response.mediaTypes as mt>
      <#if !(differentMediaTypes?contains(mt))>
        <#assign result=result+[response]>
      </#if>
    </#list>
  </#list>
  <#if result?size gte 2>
    <#list operation.responses as response>
      <#if response.code=="200">
        <#return response.type>
      <#else>
        <#return "void">
      </#if>
    </#list>
  <#else>
    <#if result?size lt 1>
      <#return "void">
    <#else>
      <#if isServiceResponse>
        <#return OpenApiUtil.printJavaServiceResponseReturnType(result?first)>
      <#else>
        <#return result?first.type>
      </#if>
    </#if>
  </#if>
</#function>

<#function getDistinctMediaTypes operation>
  <#assign result=" ">
  <#assign amountOfTypes=0>
  <#list operation.responses as response>
    <#list response.mediaTypes as mt>
      <#assign springType = DevonfwUtil.getSpringMediaType(mt)>
      <#if !(result?contains(springType))>
        <#if !(result==" ")>
          <#assign result=result+",">
        </#if>
        <#assign result=result?trim+"MediaType."+springType>
      </#if>
    </#list>
    </#list>
  <#return result>
</#function>

<#function hasMediaTypeInResponses operation>
  <#list operation.responses as response>
    <#if response.mediaTypes?size gt 0>
      <#return true>
    </#if>
  </#list>
  <#return false>
</#function>

<#function hasResponseOfType response type>
    <#if type=="Entity">
      <#return response.isEntity>
    <#elseif type=="Paginated">
      <#return response.isPaginated>
    <#elseif type=="Array">
      <#return response.isArray>
    <#elseif type=="Void">
      <#return response.isVoid>
    <#else>
      <#return false>
    </#if>
</#function>
