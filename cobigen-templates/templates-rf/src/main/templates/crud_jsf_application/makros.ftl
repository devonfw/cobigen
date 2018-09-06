<#macro insertIdParameter>
<#compress>
<#assign ids=doc["/doc/pojo/fields[isId='true']"]>
<#if ids[0]??>
	<#list ids as id>
		<#if (id_index>0)>, </#if>${id.type} ${id.name}
	</#list>
</#if>
</#compress>
</#macro>

<#macro insertIdParameterValues>
<#compress>
<#assign ids=doc["/doc/pojo/fields[isId='true']"]>
<#if ids[0]??>
	<#list ids as id>
		<#if (id_index>0)>, </#if>${id.name}
	</#list>
</#if>
</#compress>
</#macro>

<#macro insertIdParameterValuesAsStringList>
<#compress>
<#assign ids=doc["/doc/pojo/fields[isId='true']"]>
<#if ids[0]??>
	<#list ids as id>
		<#if (id_index>0)>+"', '"+</#if>${id.name}
	</#list>
</#if>
</#compress>
</#macro>

<#macro insertIdParameterAsJavaDoc>
<#assign ids=doc["/doc/pojo/fields[isId='true']"]>
<#if ids[0]??>
<#list ids as idAttr>
	 * @param ${idAttr.name}
     *            The ${idAttr.name} of the ${pojo.name}.
</#list>
</#if>
</#macro>

<#macro insertIdParameterAsListOfStrings>
<#compress>
<#assign ids=doc["/doc/pojo/fields[isId='true']"]>
<#if ids[0]??>
<#list ids as idAttr>
	<#if (idAttr_index>0)>, </#if>String.valueOf(${idAttr.name})
</#list>
</#if>
</#compress>
</#macro>

<#macro insertIdObjectType>
<#compress>
<#assign ids=doc["/doc/pojo/fields[isId='true']"]>
<#if ids[0]??>
<#assign idType=doc["/doc/pojo/fields[isId='true']"].type[0]>
<#if idType="int">
Integer
<#elseif idType="char">
Character
<#else>
${idType?cap_first}
</#if>
</#if>
</#compress>
</#macro>

<#macro defineAndRetrieveAllIds>
  <#list doc["/doc/pojo/fields[isId='true']"] as id>
    ${id.type} ${id.name} = ${pojo.name?uncap_first}.get${id.name?cap_first}();
  </#list>
</#macro>
