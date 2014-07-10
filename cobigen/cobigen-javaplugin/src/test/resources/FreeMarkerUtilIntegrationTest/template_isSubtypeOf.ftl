<#-- Copyright © Capgemini 2013. All rights reserved. -->
<#compress>
<#compress>
<#if .globals.utils.isSubtypeOf("java.lang.String", "java.lang.Object")>
true
<#else>
false
</#if>
</#compress>
<#compress>
<#if .globals.utils.isSubtypeOf("java.lang.String", "java.lang.String")>
true
<#else>
false
</#if>
</#compress>
<#compress>
<#if .globals.utils.isSubtypeOf("java.lang.Object", "java.lang.String")>
true
<#else>
false
</#if>
</#compress>
</#compress>