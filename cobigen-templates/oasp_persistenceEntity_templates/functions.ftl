<#function equalsJavaPrimitive simpleType>
<#return simpleType=="byte" || simpleType=="short" || simpleType=="int" || simpleType=="long" || simpleType=="float" || simpleType=="double" || simpleType=="boolean" || simpleType=="char"> 
</#function>

<#macro boxJavaPrimitive simpleType varName>
<#compress>
<#if simpleType=="byte">
((Byte)${varName})
<#elseif simpleType=="short">
((Short)${varName})
<#elseif simpleType=="int">
((Integer)${varName})
<#elseif simpleType=="long">
((Long)${varName})
<#elseif simpleType=="float">
((Float)${varName})
<#elseif simpleType=="double">
((Double)${varName})
<#elseif simpleType=="boolean">
((Boolean)${varName})
<#elseif simpleType=="char">
((Char)${varName})
</#if>
</#compress>
</#macro>