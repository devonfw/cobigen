<#macro callNotNullPropertyWithDefaultValue attr>
<#compress>
<#if attr.type = "String">
	${attr.name}("DefaultString");
<#elseif attr.type = "int" || attr.type = "Integer">
	${attr.name}(1);
<#elseif attr.type?lower_case = "long">
	${attr.name}(1l);
<#elseif attr.type?lower_case = "float">
	${attr.name}(1f);
<#elseif getPackage(attr.canonicalType) = pojo.package>
	${attr.name}(new ${attr.name?cap_first}Builder().createNew());
<#else>
	//TODO ${attr.name}(...); //set Default ${getPackage(attr.canonicalType)}
</#if>
</#compress>
</#macro>

<#function getPackage fqn>
	<#assign lastIndexOfDot=fqn?last_index_of(".")+1 />
	<#assign package = fqn?substring(0, lastIndexOfDot) />
	<#if package = "">
		<#-- Heuristic, which might cause faulty generation results in some not often occuring cases -->
		<#assign package = pojo.package>
	</#if>
	<#return package>
</#function>