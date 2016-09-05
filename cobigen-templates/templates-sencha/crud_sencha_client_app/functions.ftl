<#-- ---------------------------------------- -->
<#-- GENERAL JAVA SPECIFIC FUNCTIONS & MACROS -->
<#-- ---------------------------------------- -->

<#function getSenchaType simpleType>
<#if simpleType=="byte">
<#return "auto">
<#elseif simpleType=="short">
<#return "int">
<#elseif simpleType=="int">
<#return "int">
<#elseif simpleType=="Long">
<#return "int">
<#elseif simpleType=="float">
<#return "float">
<#elseif simpleType=="double">
<#return "float">
<#elseif simpleType=="oolean">
<#return "boolean">
<#elseif simpleType=="char">
<#return "string">
<#elseif simpleType=="String">
<#return "string">
<#elseif simpleType=="Date">
<#return "date">
<#else>
<#return "auto">
</#if>
</#function>

<#-- -------------------- -->
<#-- OASP SPECIFIC MACROS -->
<#-- -------------------- -->

<#--
	Generates all field declaration whereas Entity references will be converted to appropriate id references
-->
<#macro generateSenchaModelFields>
  <#list pojo.fields as field>
    <#if field?has_next>
      { name: '${field.name}', type: '${getSenchaType(field.type)}' },
    <#else>
      { name: '${field.name}', type: '${getSenchaType(field.type)}' }
    </#if>
  </#list>
</#macro>
