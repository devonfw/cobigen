<#function equalsJavaPrimitive simpleType>
<#return simpleType=="byte" || simpleType=="short" || simpleType=="int" || simpleType=="long" || simpleType=="float" || simpleType=="double" || simpleType=="boolean" || simpleType=="char"> 
</#function>

<#function getComponentType type>
<#return type?replace("(\l.*\g)|(\\[.*\\])","","r")> 
</#function>