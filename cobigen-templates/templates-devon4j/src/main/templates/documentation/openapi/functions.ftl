<#compress>

<#function getServer>
  <#list model.header.servers as server>
    <#return server.URI>
  </#list>
</#function>

</#compress>