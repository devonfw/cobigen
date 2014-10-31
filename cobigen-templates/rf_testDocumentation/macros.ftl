<#-- Copyright � Capgemini 2013. All rights reserved. -->
<#macro insertMethodsJavaDoc>
<#compress>
<#list doc.pojo.methods as method><#if method.annotations.org_junit_Test?has_content && method.javaDoc?has_content><#assign cellWithNewLines=method.javaDoc?replace("\\r\\n|\\r","\n", "r")><#assign cellWithNewQuotes=cellWithNewLines?replace('\\"',"'","r")>"${cellWithNewQuotes}";</#if></#list>
</#compress>
</#macro>

<#macro insertCategory>
<#compress>
<#assign categories=pojo.annotations.org_junit_experimental_categories_Category.value />
<#list categories as category>${category?replace(".class","")} </#list>
</#compress>
</#macro>