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

<#macro insertFirstMethodDocletValueTestziel>
<#compress>
<#if pojo.methods[0]?has_content && pojo.methods[0].javaDoc?has_content && pojo.methods[0].javaDoc.Testziel?has_content><#assign cellWithNewLines=pojo.methods[0].javaDoc.Testziel?replace("\\r\\n|\\r","\n", "r")><#assign cellWithNewQuotes=cellWithNewLines?replace('\\"',"'","r")>"${cellWithNewQuotes}"</#if>
</#compress>
</#macro>

<#macro insertFirstMethodDocletValueBeschreibung>
<#compress>
<#if pojo.methods[0]?has_content && pojo.methods[0].javaDoc?has_content && pojo.methods[0].javaDoc.Beschreibung?has_content><#assign cellWithNewLines=pojo.methods[0].javaDoc.Beschreibung?replace("\\r\\n|\\r","\n", "r")><#assign cellWithNewQuotes=cellWithNewLines?replace('\\"',"'","r")>"${cellWithNewQuotes}"</#if>
</#compress>
</#macro>