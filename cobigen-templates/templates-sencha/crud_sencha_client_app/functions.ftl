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


<#-- ----------------------- -->
<#-- OASP SPECIFIC FUNCTIONS -->
<#-- ----------------------- -->

<#--
	Check whether the given 'canonicalType' is an OASP Entity, which is declared in the given 'component'
-->
<#function isEntityInComponent canonicalType component>
	<#assign regex =  ".+" + component + r"\.dataaccess\.api\.[A-Za-z0-9_]+Entity(<.*)?">
	<#return canonicalType?matches(regex)>
</#function>

<#--
	Determines the ID getter for a given 'field' dependent on whether the getter should access the ID via an object reference or a direct ID getter (default=false)
-->
<#function resolveIdGetter field byObjectReference=false>
	<#assign suffix = resolveIdVariableNameOrSetterGetterSuffix(field, byObjectReference, true)>
	<#return "get" + suffix + "()">
</#function>

<#--
	Determines the ID setter for a given 'field' dependent on whether the setter should access the ID via an object reference or a direct ID setter (default=false)
    In contrast to resolveIdGetter, this function does not generate the function parenthesis to enable parameter declaration.
-->
<#function resolveIdSetter field byObjectReference=false>
	<#assign suffix = resolveIdVariableNameOrSetterGetterSuffix(field, byObjectReference, true)>
	<#return "set" + suffix >
</#function>

<#--
	Determines the variable name for the id value of the 'field'
-->
<#function resolveIdVariableName field>
	<#return resolveIdVariableNameOrSetterGetterSuffix(field, false, false)>
</#function>

<#--
	Determines the ID setter/getter suffix for a given 'field' dependent on whether the setter/getter should access the ID via an object reference or a direct ID setter/getter (default=false)
-->
<#function resolveIdVariableNameOrSetterGetterSuffix field byObjectReference capitalize>
	<#assign fieldCapName=field.name>
	<#if capitalize>
		<#assign fieldCapName=fieldCapName?cap_first>
	</#if>
	<#assign suffix="">
	<#if field.type?contains("Entity")>
		<#if field.canonicalType?starts_with("java.util.List") || field.canonicalType?starts_with("java.util.Set")>
		  <#assign suffix="Ids">
		  <#-- Handle the standard case. Due to no knowledge about the interface, we have no other possibility than guessing -->
		  <#-- Therefore remove (hopefully) plural 's' from field's name to attach it on the suffix -->
		  <#if fieldCapName?ends_with("s")>
			 <#assign fieldCapName=fieldCapName?substring(0, fieldCapName?length-1)>
		  </#if>
		<#else>
		  <#assign suffix="Id">
		</#if>

		<#if byObjectReference && isEntityInComponent(field.canonicalType, variables.component)>
			<#assign suffix="().getId"><#-- direct references for Entities in same component, so get id of the object reference -->
		</#if>
	</#if>
	<#return fieldCapName + suffix>
</#function>

<#--
	Converts all occurrences of OASP Entities types in the given 'field' simple type (possibly generic) to Longs
-->
<#function getSimpleEntityTypeAsLongReference field>
	<#assign newSimpleType = field.type>
	<#if field.type?contains("Entity")>
		<#assign newSimpleType = field.type?replace("[^<>]+Entity","Long","r")>
	</#if>
	<#return newSimpleType>
</#function>
