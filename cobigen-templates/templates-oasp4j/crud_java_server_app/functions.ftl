<#-- -------------------- -->
<#-- OASP SPECIFIC MACROS -->
<#-- -------------------- -->

<#--
	Generates all field declaration whereas Entity references will be converted to appropriate id references
-->
<#macro generateFieldDeclarations_withRespectTo_entityObjectToIdReferenceConversion isSearchCriteria=false>
<#list pojo.fields as field>
<#if field.type?contains("Entity")> <#-- add ID getter & setter for Entity references only for ID references -->
  <#if !JavaUtil.isCollection(classObject, field.name)> <#-- do not generate field for multiple relation -->
   	 private ${field.type?replace("[^<>,]+Entity","Long","r")} ${OaspUtil.resolveIdVariableName(classObject,field)};
  </#if>
<#elseif field.type?contains("Embeddable")>
	<#if isSearchCriteria>
		private ${field.type?replace("Embeddable","SearchCriteriaTo")} ${field.name};
	<#else>
		private ${field.type?replace("Embeddable","Eto")} ${field.name};
	</#if>
<#elseif isSearchCriteria && JavaUtil.equalsJavaPrimitive(classObject,field.name)>
  private ${JavaUtil.boxJavaPrimitives(classObject,field.name)} ${field.name};
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
   <#if !JavaUtil.isCollection(classObject, field.name)> <#-- do not generate getters & setters for multiple relation -->
    	<#assign idVar = OaspUtil.resolveIdVariableName(classObject,field)>
    	<#if implementsInterface>@Override</#if>
    	public ${OaspUtil.getSimpleEntityTypeAsLongReference(field)} ${OaspUtil.resolveIdGetter(field,false,"")} <#if isInterface>;<#else>{
    		return ${idVar};
    	}</#if>
    
    	<#if implementsInterface>@Override</#if>
    	public void ${OaspUtil.resolveIdSetter(field,false,"")}(${OaspUtil.getSimpleEntityTypeAsLongReference(field)} ${idVar}) <#if isInterface>;<#else>{
    		this.${idVar} = ${idVar};
    	}</#if>
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
	public <#if isSearchCriteria>${JavaUtil.boxJavaPrimitives(classObject,field.name)}<#else>${field.type}</#if> <#if field.type=='boolean'>is<#else>get</#if>${field.name?cap_first}() <#if isInterface>;<#else>{
		return ${field.name};
	}</#if>

	<#if implementsInterface>@Override</#if>
	public void set${field.name?cap_first}(<#if isSearchCriteria>${JavaUtil.boxJavaPrimitives(classObject,field.name)}<#else>${field.type}</#if> ${field.name}) <#if isInterface>;<#else>{
		this.${field.name} = ${field.name};
	}</#if>
</#if>
</#list>
</#macro>