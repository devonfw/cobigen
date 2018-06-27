<#function getServer>
  <#list model.header.servers as server>
    <#return server.URI>
  </#list>
</#function>

<#function getParameter param where>
  <#if where="header">
    <#if param.inHeader>
      <#if param.mediaType??>
        <#assign type=param.mediaType>
      <#else><#assign type="-">
      </#if>
    <#else><#assign type="-">
    </#if>
  </#if>

  <#if where="query">
  <#if param.inQuery>
      <#if param.mediaType??>
        <#assign type=param.mediaType>
      <#else><#assign type="-">
      </#if>
    <#else><#assign type="-">
    </#if>
  </#if>
  
  <#if where="path">
    <#if param.inPath>
      <#if param.mediaType??>
        <#assign type=param.mediaType>
      <#else><#assign type="-">
      </#if>
    <#else><#assign type="-">
    </#if>
  </#if>
  
  <#return type>
</#function>

<#function hasPathsOfType type>
  <#list model.component.paths as path>
    <#list path.operations as op>
      <#if op.type=type>
        <#return true>
      </#if>
    </#list>
  </#list>
  <#return false>
</#function>

<#function getPathsOfType type>
  <#assign pathList=[]>
  <#list model.component.paths as path>
    <#list path.operations as op>
      <#if op.type=type>
        <#assign pathList=pathList+[path]>
      </#if>
    </#list>
  </#list>
  <#return pathList>
</#function>