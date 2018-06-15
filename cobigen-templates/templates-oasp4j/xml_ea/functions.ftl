<#-- ---------------------------------------- -->
<#-- GENERAL JAVA SPECIFIC FUNCTIONS & MACROS -->
<#-- ---------------------------------------- -->
<#function getType simpleType>
    <#if simpleType=="byte">
        <#return "number">
    <#elseif simpleType=="short">
        <#return "number">
    <#elseif simpleType=="int">
        <#return "number">
    <#elseif simpleType=="Integer">
        <#return "number">
    <#elseif simpleType=="long">
        <#return "number">
    <#elseif simpleType=="Double">
        <#return "number">
    <#elseif simpleType=="Long">
        <#return "number">
    <#elseif simpleType=="float">
        <#return "number">
    <#elseif simpleType=="double">
        <#return "number">
    <#elseif simpleType=="boolean">
        <#return "boolean">
    <#elseif simpleType=="Boolean">
        <#return "boolean">
    <#elseif simpleType=="char">
        <#return "string">
    <#elseif simpleType=="String">
        <#return "string">
    <#else>
        <#return "any">
    </#if>
</#function>

<#-- -------------------- -->
<#-- SPECIFIC MACROS -->
<#-- -------------------- -->

<#-- Adds the input fields for the filter with types -->
<#macro getNG2Type_Grid_Search>
  <#list pojo.fields as field>
      <md-input-container style="width:100%;">
        <input mdInput name ="${field.name}" type="${getType(field.type)}" ngModel [placeholder]= "'${variables.component}datagrid.columns.${field.name}' | translate">
      </md-input-container>
   </#list>
</#macro>

<#-- Adds the input fields for the New Element dialog with types -->
<#macro getNG2Type_Add_Dialog>
  <#list pojo.fields as field>
    <md-input-container style="width:100%;">
        <input mdInput type="${getType(field.type)}" name = "${field.name}" [placeholder]= "'${variables.component}datagrid.columns.${field.name}' | translate" [(ngModel)] = "items.${field.name}" required>
    </md-input-container>
   </#list>
</#macro>

<#-- -------------------- -->
<#-- OASP SPECIFIC MACROS -->
<#-- -------------------- -->

<#--
	Generates all field declaration whereas Entity references will be converted to appropriate id references
-->
<#macro generateFieldDeclarations_withRespectTo_entityObjectToIdReferenceConversion isSearchCriteria=false>
<#list elemDoc["self::node()/ownedAttribute"] as field>
<#assign fieldType=field["type/@xmi:idref"]?replace("EAJava_","")>
<#assign typeVar = JavaUtil.getExtType(fieldType)>
<#if typeVar?contains("Entity")> <#-- add ID getter & setter for Entity references only for ID references -->
  <#if !JavaUtil.isCollection(classObject, field["@name"])> <#-- do not generate field for multiple relation -->
   	 private ${typeVar?replace("[^<>,]+Entity","Long","r")} ${OaspUtil.resolveIdVariableName(classObject,field)};
  </#if>
<#elseif typeVar?contains("Embeddable")>
	<#if isSearchCriteria>
		private ${typeVar?replace("Embeddable","SearchCriteriaTo")} ${field["@name"]};
	<#else>
		private ${typeVar?replace("Embeddable","Eto")} ${field["@name"]};
	</#if>
<#elseif isSearchCriteria && JavaUtil.equalsJavaPrimitive(classObject,field["@name"])>
  private ${JavaUtil.boxJavaPrimitives(classObject,field["@name"])} ${field["@name"]};
<#else>
	private ${typeVar} ${field["@name"]};
</#if>
</#list>
</#macro>

<#--
	Generates all setter and getter for the fields whereas for Entity fields it will generate setter and getter for id references
-->
<#macro generateSetterAndGetter_withRespectTo_entityObjectToIdReferenceConversion implementsInterface=true, isInterface=false, isSearchCriteria=false>
<#list elemDoc["self::node()/ownedAttribute"] as field>
<#assign fieldType=field["type/@xmi:idref"]?replace("EAJava_","")>
<#assign typeVar = JavaUtil.getExtType(fieldType)>
<#if typeVar?contains("Entity")> <#-- add ID getter & setter for Entity references only for ID references -->
   <#if !JavaUtil.isCollection(classObject, field["@name"])> <#-- do not generate getters & setters for multiple relation -->
    	<#assign idVar = OaspUtil.resolveIdVariableName(classObject,field)>
    	<#if implementsInterface>@Override</#if>
    	public ${OaspUtil.getSimpleEntityTypeAsLongReference(typeVar)} ${OaspUtil.resolveIdGetter(typeVar,false,"")} <#if isInterface>;<#else>{
    		return ${idVar};
    	}</#if>
    
    	<#if implementsInterface>@Override</#if>
    	public void ${OaspUtil.resolveIdSetter(typeVar,false,"")}(${OaspUtil.getSimpleEntityTypeAsLongReference(typeVar)} ${idVar}) <#if isInterface>;<#else>{
    		this.${idVar} = ${idVar};
    	}</#if>
   </#if>
<#elseif typeVar?contains("Embeddable")>
	<#if isSearchCriteria>
		public ${typeVar?replace("Embeddable","SearchCriteriaTo")} <#if typeVar=='boolean'>is<#else>get</#if>${field["@name"]?cap_first}() <#if isInterface>;<#else>{
			return ${field["@name"]};
		}</#if>

		public void set${typeVar}(${typeVar?replace("Embeddable","SearchCriteriaTo")} ${field["@name"]}) <#if isInterface>;<#else>{
			this.${field["@name"]} = ${field["@name"]};
		}</#if>
	<#else>
		public ${typeVar?replace("Embeddable","")} <#if typeVar=='boolean'>is<#else>get</#if>${field["@name"]?cap_first}() <#if isInterface>;<#else>{
			return ${field["@name"]};
		}</#if>

		public void set${typeVar}(${typeVar?replace("Embeddable","")} ${field["@name"]}) <#if isInterface>;<#else>{
			this.${field["@name"]} = ${field["@name"]};
		}</#if>
	</#if>
<#else>
  <#if implementsInterface>@Override</#if>
	public <#if isSearchCriteria>${typeVar}<#else>${typeVar}</#if> <#if typeVar=='boolean'>is<#else>get</#if>${field["@name"]?cap_first}() <#if isInterface>;<#else>{
		return ${field["@name"]};
	}</#if>

	<#if implementsInterface>@Override</#if>
	public void set${JavaUtil.boxJavaPrimitives(field["@name"])?cap_first}(<#if isSearchCriteria>${typeVar}<#else>${typeVar}</#if> ${field["@name"]}) <#if isInterface>;<#else>{
		this.${field["@name"]} = ${field["@name"]};
	}</#if>
</#if>
</#list>
</#macro>
