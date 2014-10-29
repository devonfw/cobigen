<#compress>
<#compress>
<#if isSubtypeOf("java.lang.String", "java.lang.Object")>
true
<#else>
false
</#if>
</#compress>
<#compress>
<#if isSubtypeOf("java.lang.String", "java.lang.String")>
true
<#else>
false
</#if>
</#compress>
<#compress>
<#if isSubtypeOf("java.lang.Object", "java.lang.String")>
true
<#else>
false
</#if>
</#compress>
<#compress>
<#if isSubtypeOf(variables.javaLang+".Object", variables.javaLang+".String")>
true
<#else>
false
</#if>
</#compress>
<#compress>
<#if isSubtypeOf(variables.objectFqn, variables.stringFqn)>
true
<#else>
false
</#if>
</#compress>
</#compress>