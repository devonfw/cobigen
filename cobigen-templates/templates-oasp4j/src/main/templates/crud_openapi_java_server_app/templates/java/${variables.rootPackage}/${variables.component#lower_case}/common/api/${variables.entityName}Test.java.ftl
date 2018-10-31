<#include '/makros.ftl'>

<#list model.properties as property>
<@definePropertyNameAndType property true/>
----------------
property.type = ${property.type}
property.name = ${property.name}

propType = ${propType}
propName = ${propName}

property.sameComponent = ${property.sameComponent?c}
property.isEntity = ${property.isEntity?c}

----------------
</#list>