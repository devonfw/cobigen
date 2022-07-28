<#-- -------------------- -->
<#-- devonfw SPECIFIC MACROS -->
<#-- -------------------- -->

<#--
	Generates all field declaration whereas Entity references will be converted to appropriate id references
-->
<#macro generateFieldDeclarations_withRespectTo_entityObjectToIdReferenceConversion>
<#list pojo.fields as field>
<#if field.type?contains("Entity")> <#-- add ID getter & setter for Entity references only for ID references -->
   	private ${field.type?replace("[^<>,]+Entity","Long","r")} ${resolveIdVariableName(field)};
<#else>
	private ${field.type} ${field.name};
</#if>
</#list>
</#macro>

<#--
	Generates all setter and getter for the fields whereas for Entity fields it will generate setter and getter for id references
-->
<#macro generateSetterAndGetter_withRespectTo_entityObjectToIdReferenceConversion implementsInterface=true>
<#list pojo.fields as field>
<#if field.type?contains("Entity")> <#-- add ID getter & setter for Entity references only for ID references -->

	<#assign idVar = DevonfwUtil.resolveIdVariableName(classObject,field)>
	<#if implementsInterface>
	@Override</#if>
	public ${DevonfwUtil.getSimpleEntityTypeAsLongReference(field)} ${DevonfwUtil.resolveIdGetter(classObject,field)} {
		return ${idVar};
	}

	<#if implementsInterface>
	@Override</#if>
	public void ${DevonfwUtil.resolveIdSetter(classObject,field)}(${DevonfwUtil.getSimpleEntityTypeAsLongReference(field)} ${idVar}) {
		this.${idVar} = ${idVar};
	}
<#else>
   	<#if implementsInterface>
	@Override</#if>
	public ${field.type} <#if field.type=='boolean'>is<#else>get</#if>${field.name?cap_first}() {
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

