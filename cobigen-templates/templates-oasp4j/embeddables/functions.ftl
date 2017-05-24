<#-- ---------------------------------------- -->
<#-- GENERAL JAVA SPECIFIC FUNCTIONS & MACROS -->
<#-- ---------------------------------------- -->

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
((Character)${varName})
</#if>
</#compress>
</#macro>

<#-- -------------------- -->
<#-- OASP SPECIFIC MACROS -->
<#-- -------------------- -->

<#--
	Generates all field declaration whereas Entity references will be converted to appropriate id references
-->
<#macro generateFieldDeclarations_withRespectTo_entityObjectToIdReferenceConversion boxPrimitives=false>
<#list pojo.fields as field>
<#if field.type?contains("Entity")> <#-- add ID getter & setter for Entity references only for ID references -->
   	private ${field.type?replace("[^<>,]+Entity","Long","r")} ${resolveIdVariableName(field)};
<#else>
	private <#if boxPrimitives>${JavaUtil.boxJavaPrimitives(field.type)}<#else>${field.type}</#if> ${field.name};
</#if>
</#list>
</#macro>

<#--
	Generates all setter and getter for the fields whereas for Entity fields it will generate setter and getter for id references
-->
<#macro generateSetterAndGetter_withRespectTo_entityObjectToIdReferenceConversion implementsInterface=true>
<#list pojo.fields as field>
<#if field.type?contains("Entity")> <#-- add ID getter & setter for Entity references only for ID references -->

	<#assign idVar = resolveIdVariableName(field)>
	<#if implementsInterface>
	@Override</#if>
	public ${getSimpleEntityTypeAsLongReference(field)} ${resolveIdGetter(field)} {
		return ${idVar};
	}

	<#if implementsInterface>
	@Override</#if>
	public void ${resolveIdSetter(field)}(${getSimpleEntityTypeAsLongReference(field)} ${idVar}) {
		this.${idVar} = ${idVar};
	}
<#else>
   	<#if implementsInterface>
	@Override</#if>
	public ${field.type} get${field.name?cap_first}() {
		return ${field.name};
	}

	<#if implementsInterface>
	@Override</#if>
	public void set${field.name?cap_first}(${field.type} ${field.name}) {
		this.${field.name} = ${field.name};
	}
</#if>
</#list>
</#macro>

<#-- ----------------------- -->
<#-- OASP SPECIFIC FUNCTIONS -->
<#-- ----------------------- -->

<#--
	edited by sholzer 20170523: Reimplemented all functions in Java class embeddables.EmbeddablesFunctions. Kept the functions here as delegates for better readability of the macros
-->

<#--
	Check whether the given 'canonicalType' is an OASP Entity, which is declared in the given 'component'
-->
<#function isEntityInComponent canonicalType component>
	<#return EmbeddablesFunctions.isEntityInComponent(canonicalType, component)>
</#function>

<#--
	Determines the ID getter for a given 'field' dependent on whether the getter should access the ID via an object reference or a direct ID getter (default=false)
-->
<#function resolveIdGetter field byObjectReference=false>
	<#return EmbeddablesFunctions.resolveIdGetter(field, byObjectReference, true, variables.component)>
</#function>

<#--
	Determines the ID setter for a given 'field' dependent on whether the setter should access the ID via an object reference or a direct ID setter (default=false)
    In contrast to resolveIdGetter, this function does not generate the function parenthesis to enable parameter declaration.
-->
<#function resolveIdSetter field byObjectReference=false>
	<#return EmbeddablesFunctions.resolveIdSetter(field, byObjectReference, true, variables.component)>
</#function>

<#--
	Determines the variable name for the id value of the 'field'
-->
<#function resolveIdVariableName field>
	<#return EmbeddablesFunctions.resolveIdVariableNameOrSetterGetterSuffix(field, false, false, variables.component)>
</#function>

<#--
	Determines the ID setter/getter suffix for a given 'field' dependent on whether the setter/getter should access the ID via an object reference or a direct ID setter/getter (default=false)
-->
<#function resolveIdVariableNameOrSetterGetterSuffix field byObjectReference capitalize>
	<#return EmbeddablesFunctions.resolveIdVariableNameOrSetterGetterSuffix(field, byObjectReference, capitalize, variables.component)>
</#function>

<#--
	Converts all occurrences of OASP Entities types in the given 'field' simple type (possibly generic) to Longs
-->
<#function getSimpleEntityTypeAsLongReference field>
	<#return EmbeddablesFunctions.getSimpleEntityTypeAsLongReference(field)>
</#function>












