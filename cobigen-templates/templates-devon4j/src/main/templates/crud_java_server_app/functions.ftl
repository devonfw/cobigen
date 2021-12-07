<#-- -------------------- -->
<#-- devonfw SPECIFIC MACROS -->
<#-- -------------------- -->

<#--
	Generates all field declaration whereas Entity references will be converted to appropriate id references
-->
<#macro generateFieldDeclarations_withRespectTo_entityObjectToIdReferenceConversion isSearchCriteria=false>
<#list pojo.fields as field>
<#if field.type?contains("Entity")> <#-- add ID getter & setter for Entity references only for ID references -->
  <#if !JavaUtil.isCollection(classObject, field.name)> <#-- do not generate field for multiple relation -->
   	 private ${field.type?replace("[^<>,]+Entity","Long","r")} ${DevonfwUtil.resolveIdVariableName(classObject,field)};
  </#if>
<#elseif field.type?contains("Embeddable")>
	<#if isSearchCriteria>
		private ${field.type?replace("Embeddable","SearchCriteriaTo")} ${field.name};
	<#else>
		private ${field.type?replace("Embeddable","Eto")} ${field.name};
	</#if>
<#elseif isSearchCriteria && JavaUtil.equalsJavaPrimitive(classObject,field.name)>
  private ${JavaUtil.boxJavaPrimitives(classObject,field.name)} ${field.name};
<#elseif !isSearchCriteria || !JavaUtil.isCollection(classObject, field.name)>
	private ${field.type} ${field.name};
</#if>
</#list>
<#-- Used to generate StringSearchConfigTo for each field -->
<#if isSearchCriteria>
	<#list pojo.fields as field>
		<#if field.type="String">
			private StringSearchConfigTo ${field.name}Option;
		</#if>
	</#list>
</#if>
</#macro>

<#--
	Generates all setter and getter for the fields whereas for Entity fields it will generate setter and getter for id references
-->
<#macro generateSetterAndGetter_withRespectTo_entityObjectToIdReferenceConversion implementsInterface=true, isInterface=false, isSearchCriteria=false>
<#list pojo.fields as field>
<#if field.type?contains("Entity")> <#-- add ID getter & setter for Entity references only for ID references -->
   <#if !JavaUtil.isCollection(classObject, field.name)> <#-- do not generate getters & setters for multiple relation -->
    	<#assign idVar = DevonfwUtil.resolveIdVariableName(classObject,field)>
    	<#if !implementsInterface>
      /**
      * getter for ${field.name}Id attribute
      * @return ${field.name}Id
      */
      </#if>
    	<#if implementsInterface>@Override</#if>
    	public ${DevonfwUtil.getSimpleEntityTypeAsLongReference(field)} ${DevonfwUtil.resolveIdGetter(field,false,"")} <#if isInterface>;<#else>{
    		return ${idVar};
    	}</#if>
      <#if !implementsInterface>
      /**
      * @param ${field.name}
      *            setter for ${field.name} attribute
      */
      </#if>      
    	<#if implementsInterface>@Override</#if>
    	public void ${DevonfwUtil.resolveIdSetter(field,false,"")}(${DevonfwUtil.getSimpleEntityTypeAsLongReference(field)} ${idVar}) <#if isInterface>;<#else>{
    		this.${idVar} = ${idVar};
    	}</#if>
   </#if>
<#elseif field.type?contains("Embeddable")>
	<#if isSearchCriteria>
	    /**		 
		 * @return ${field.name}
		 */
		public ${field.type?replace("Embeddable","SearchCriteriaTo")} get${field.name?cap_first}() <#if isInterface>;<#else>{
			return ${field.name};
		}</#if>

		/**
		 * @param ${field.name}
		 *            setter for ${field.name} attribute
		 */
		public void set${field.name?cap_first}(${field.type?replace("Embeddable","SearchCriteriaTo")} ${field.name}) <#if isInterface>;<#else>{
			this.${field.name} = ${field.name};
		}</#if>
	<#else>
		/**
		 * @return ${field.name}Id
		 */
		public ${field.type?replace("Embeddable","")} <#if field.type=='boolean'>is<#else>get</#if>${field.name?cap_first}() <#if isInterface>;<#else>{
			return ${field.name};
		}</#if>

		/**
		 * @param ${field.name}
		 *            setter for ${field.name} attribute
		 */
		public void set${field.name?cap_first}(${field.type?replace("Embeddable","")} ${field.name}) <#if isInterface>;<#else>{
			this.${field.name} = ${field.name};
		}</#if>
	</#if>
<#elseif !isSearchCriteria || !JavaUtil.isCollection(classObject, field.name)>
  <#if !implementsInterface>
      /**
      * @return ${field.name}Id
      */
  </#if>
  <#if implementsInterface>@Override</#if>
	public <#if isSearchCriteria>${JavaUtil.boxJavaPrimitives(classObject,field.name)} get${field.name?cap_first}() <#else>${field.type} <#if field.type=='boolean'>is<#else>get</#if>${field.name?cap_first}()</#if> <#if isInterface>;<#else>{
		return ${field.name};
	}</#if>
  <#if !implementsInterface>
  /**
   * @param ${field.name}
   *            setter for ${field.name} attribute
   */
  </#if>
	<#if implementsInterface>@Override</#if>
	public void set${field.name?cap_first}(<#if isSearchCriteria>${JavaUtil.boxJavaPrimitives(classObject,field.name)}<#else>${field.type}</#if> ${field.name}) <#if isInterface>;<#else>{
		this.${field.name} = ${field.name};
	}</#if>
</#if>
</#list>
<#if isSearchCriteria>
	<#list pojo.fields as field>
		<#if field.type="String">
	/**
	* @return the {@link StringSearchConfigTo} used to search for {@link #get${field.name?cap_first}<#if field.type?contains("Entity")>Entity</#if>() ${field.name}}.
	*/
	public StringSearchConfigTo get${field.name?cap_first}Option() {

		return this.${field.name}Option;
	}

	/**
	* @param ${field.name}Option new value of {@link #get${field.name?cap_first}Option()}.
	*/
	public void set${field.name?cap_first}Option(StringSearchConfigTo ${field.name}Option) {

		this.${field.name}Option =${field.name}Option;
	}
		</#if>
	</#list>
</#if>
</#macro>
