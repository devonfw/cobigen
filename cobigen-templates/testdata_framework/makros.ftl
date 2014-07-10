<#-- Copyright © Capgemini 2013. All rights reserved. -->
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
<#else>
	//TODO ${attr.name}(...); //set Default
</#if>
</#compress>
</#macro>