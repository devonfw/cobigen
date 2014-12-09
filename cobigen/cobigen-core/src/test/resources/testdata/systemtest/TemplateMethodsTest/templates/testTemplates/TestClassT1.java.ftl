public TestClassT1 {
<#if isSubtypeOf("java.lang.String", "java.lang.Object")>
  true
<#else>
  false
</#if>
<#if isSubtypeOf("java.lang.String", "java.lang.String")>
  true
<#else>
  false
</#if>
<#if isSubtypeOf("java.lang.Object", "java.lang.String")>
  true
<#else>
  false
</#if>
}