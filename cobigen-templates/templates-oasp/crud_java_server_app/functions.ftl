<#-- ---------------------------------------- -->
<#-- GENERAL JAVA SPECIFIC FUNCTIONS & MACROS -->
<#-- ---------------------------------------- -->

<#function equalsJavaPrimitive simpleType>
<#return simpleType=="byte" || simpleType=="short" || simpleType=="int" || simpleType=="long" || simpleType=="float" || simpleType=="double" || simpleType=="boolean" || simpleType=="char">
</#function>

<#function equalsJavaPrimitiveIncludingArrays simpleType>
<#return equalsJavaPrimitive(simpleType) || simpleType=="byte[]" || simpleType=="short[]" || simpleType=="int[]" || simpleType=="long[]" || simpleType=="float[]" || simpleType=="double[]" || simpleType=="boolean[]" || simpleType=="char[]">
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

<#function getBoxedType simpleType>
<#if simpleType=="byte">
<#return "Byte">
<#elseif simpleType=="short">
<#return "Short">
<#elseif simpleType=="int">
<#return "Integer">
<#elseif simpleType=="long">
<#return "Long">
<#elseif simpleType=="float">
<#return "Float">
<#elseif simpleType=="double">
<#return "Double">
<#elseif simpleType=="boolean">
<#return "Boolean">
<#elseif simpleType=="char">
<#return "Char">
<#else>
<#return simpleType>
</#if>
</#function>

<#-- -------------------- -->
<#-- OASP SPECIFIC MACROS -->
<#-- -------------------- -->

<#--
	Generates all field declaration whereas Entity references will be converted to appropriate id references
-->
<#macro generateFieldDeclarations_withRespectTo_entityObjectToIdReferenceConversion isSearchCriteria=false>
<#list pojo.fields as field>
<#if field.type?contains("Entity")> <#-- add ID getter & setter for Entity references only for ID references -->
   <#if !field.type?starts_with("List<") && !field.type?starts_with("Set<")> <#-- do not generate field for multiple relation -->
          private ${field.type?replace("[^<>,]+Entity","Long","r")} ${resolveIdVariableName(field)};
   </#if>
<#elseif field.type?contains("Embeddable")>
  <#if isSearchCriteria>
    private ${field.type?replace("Embeddable","SearchCriteriaTo")} ${field.name};
  <#else>
    private ${field.type?replace("Embeddable","Eto")} ${field.name};
  </#if>
<#elseif isSearchCriteria && equalsJavaPrimitive(field.type)>
  private ${getBoxedType(field.type)} ${field.name};
<#else>
  private ${field.type} ${field.name};
</#if>
</#list>
</#macro>

<#--
  Generates all setter and getter for the fields whereas for Entity fields it will generate setter and getter for id references
-->
<#macro generateSetterAndGetter_withRespectTo_entityObjectToIdReferenceConversion implementsInterface=true, isInterface=false, isSearchCriteria=false>
<#list pojo.fields as field>
  <#if field.type?contains("Entity")> <#-- add ID getter & setter for Entity references only for ID references -->
     <#if !field.type?starts_with("List<") && !field.type?starts_with("Set<")> <#-- do not generate getters & setters for multiple relation -->
            <#assign idVar = resolveIdVariableName(field)>
            <#if implementsInterface>@Override</#if>
            public ${getSimpleEntityTypeAsLongReference(field)} ${resolveIdGetter(field)} <#if isInterface>;<#else>{
              return ${idVar};
            }</#if>

            <#if implementsInterface>@Override</#if>
            public void ${resolveIdSetter(field)}(${getSimpleEntityTypeAsLongReference(field)} ${idVar}) <#if isInterface>;<#else>{
              this.${idVar} = ${idVar};
            }
            </#if>
     </#if>
  <#elseif field.type?contains("Embeddable")>
    <#if isSearchCriteria>
      public ${field.type?replace("Embeddable","SearchCriteriaTo")} <#if field.type=='boolean'>is<#else>get</#if>${field.name?cap_first}() <#if isInterface>;<#else>{
        return ${field.name};
      }</#if>

      public void set${field.name?cap_first}(${field.type?replace("Embeddable","SearchCriteriaTo")} ${field.name}) <#if isInterface>;<#else>{
        this.${field.name} = ${field.name};
      }</#if>
    <#else>
      public ${field.type?replace("Embeddable","")} <#if field.type=='boolean'>is<#else>get</#if>${field.name?cap_first}() <#if isInterface>;<#else>{
        return ${field.name};
      }</#if>

      public void set${field.name?cap_first}(${field.type?replace("Embeddable","")} ${field.name}) <#if isInterface>;<#else>{
        this.${field.name} = ${field.name};
      }</#if>
    </#if>
  <#else>
    <#if implementsInterface>@Override</#if>
    public <#if isSearchCriteria>${getBoxedType(field.type)}<#else>${field.type}</#if> <#if field.type=='boolean'>is<#else>get</#if>${field.name?cap_first}() <#if isInterface>;<#else>{
      return ${field.name};
    }</#if>

    <#if implementsInterface>@Override</#if>
    public void set${field.name?cap_first}(<#if isSearchCriteria>${getBoxedType(field.type)}<#else>${field.type}</#if> ${field.name}) <#if isInterface>;<#else>{
      this.${field.name} = ${field.name};
    }</#if>
  </#if>
</#list>
</#macro>

<#-- ----------------------- -->
<#-- OASP SPECIFIC FUNCTIONS -->
<#-- ----------------------- -->

<#--
	Check whether the given 'canonicalType' is an OASP Entity, which is declared in the given 'component'
-->
<#function isEntityInComponent canonicalType component>
	<#assign regex =  ".+" + component + r"\.dataaccess\.api\.[A-Za-z0-9_]+Entity(<.*)?">
	<#return canonicalType?matches(regex)>
</#function>

<#--
	Determines the ID getter for a given 'field' dependent on whether the getter should access the ID via an object reference or a direct ID getter (default=false)
-->
<#function resolveIdGetter field byObjectReference=false>
	<#assign suffix = resolveIdVariableNameOrSetterGetterSuffix(field, byObjectReference, true)>
	<#return "get" + suffix + "()">
</#function>

<#--
	Determines the ID setter for a given 'field' dependent on whether the setter should access the ID via an object reference or a direct ID setter (default=false)
    In contrast to resolveIdGetter, this function does not generate the function parenthesis to enable parameter declaration.
-->
<#function resolveIdSetter field byObjectReference=false>
	<#assign suffix = resolveIdVariableNameOrSetterGetterSuffix(field, byObjectReference, true)>
	<#return "set" + suffix >
</#function>

<#--
	Determines the variable name for the id value of the 'field'
-->
<#function resolveIdVariableName field>
	<#return resolveIdVariableNameOrSetterGetterSuffix(field, false, false)>
</#function>

<#--
	Determines the ID setter/getter suffix for a given 'field' dependent on whether the setter/getter should access the ID via an object reference or a direct ID setter/getter (default=false)
-->
<#function resolveIdVariableNameOrSetterGetterSuffix field byObjectReference capitalize>
	<#assign fieldCapName=field.name>
	<#if capitalize>
		<#assign fieldCapName=fieldCapName?cap_first>
	</#if>
	<#assign suffix="">
	<#if field.type?contains("Entity")>
		<#if field.canonicalType?starts_with("java.util.List") || field.canonicalType?starts_with("java.util.Set")>
		  <#assign suffix="Ids">
		  <#-- Handle the standard case. Due to no knowledge about the interface, we have no other possibility than guessing -->
		  <#-- Therefore remove (hopefully) plural 's' from field's name to attach it on the suffix -->
		  <#if fieldCapName?ends_with("s")>
			 <#assign fieldCapName=fieldCapName?substring(0, fieldCapName?length-1)>
		  </#if>
		<#else>
		  <#assign suffix="Id">
		</#if>

		<#if byObjectReference && isEntityInComponent(field.canonicalType, variables.component)>
			<#assign suffix="().getId"><#-- direct references for Entities in same component, so get id of the object reference -->
		</#if>
	</#if>
	<#return fieldCapName + suffix>
</#function>

<#--
	Converts all occurrences of OASP Entities types in the given 'field' simple type (possibly generic) to Longs
-->
<#function getSimpleEntityTypeAsLongReference field>
	<#assign newSimpleType = field.type>
	<#if field.type?contains("Entity")>
		<#assign newSimpleType = field.type?replace("[^<>]+Entity","Long","r")>
	</#if>
	<#return newSimpleType>
</#function>
