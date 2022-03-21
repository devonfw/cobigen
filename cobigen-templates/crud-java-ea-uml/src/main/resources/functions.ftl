<#assign allConnectors=doc["xmi:XMI/xmi:Extension/connectors"]>
<#assign connectors = doc["xmi:XMI/xmi:Extension/connectors/connector"]>

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

<#function getConnectorName connector isType=false>
  <#assign targetName = connector["target/model/@name"]> 
  <#assign sourceName = connector["source/model/@name"]>
  <#if (connector["source/model/@type"] == "Class") || (connector["source/model/@type"]=="Class")>
    <#if ((sourceName) == '${variables.className}')>
      <#assign className=targetName>
      <#assign multiplicity=connector["target/type/@multiplicity"]>
    <#elseif ((targetName) == '${variables.className}')>
      <#assign className=sourceName>
      <#assign multiplicity=connector["source/type/@multiplicity"]>
    <#else>
      <#assign className="failure">
    </#if>
      <#if !(className=="failure")>
       <#if multiplicity?is_string>
         <#if (multiplicity == "1")>
           <#if isType>
              <#return "${className?cap_first}">
           <#else>
              <#return "${className?uncap_first}">
            </#if>
          <#elseif (multiplicity == "*")>
            <#if isType>
              <#return "List<${className?cap_first}>">
            <#else>
              <#return "${className?uncap_first}s">
            </#if>
          </#if>
        </#if>
      </#if>
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
<#-- devonfw SPECIFIC MACROS -->
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
     private ${typeVar?replace("[^<>,]+Entity","Long","r")} ${DevonfwUtil.resolveIdVariableName(classObject,field)};
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
<#if connectors?has_content>
  <#assign existing=[]>
  <#list connectors as connector>
    <#if getConnectorName(connector,true)??>
      <#if !existing?seq_contains("${getConnectorName(connector,false)}")>
    private ${getConnectorName(connector, true)} ${getConnectorName(connector, false)};
        <#assign existing=existing+["${getConnectorName(connector,false)}"]>
      </#if>
    </#if>
  </#list>
</#if>
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
      <#assign idVar = DevonfwUtil.resolveIdVariableName(classObject,field)>
      <#if implementsInterface>@Override</#if>
      public ${DevonfwUtil.getSimpleEntityTypeAsLongReference(typeVar)} ${DevonfwUtil.resolveIdGetter(typeVar,false,"")} <#if isInterface>;<#else>{
        return ${idVar};
      }</#if>
    
      <#if implementsInterface>@Override</#if>
      public void ${DevonfwUtil.resolveIdSetter(typeVar,false,"")}(${DevonfwUtil.getSimpleEntityTypeAsLongReference(typeVar)} ${idVar}) <#if isInterface>;<#else>{
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
<#if connectors?has_content>
  <#assign existing=[]>
  <#list connectors as connector>
    <#if getConnectorName(connector,true)??>
      <#if !existing?seq_contains("${getConnectorName(connector,false)}")>
    <#if implementsInterface>@Override</#if>
    public ${getConnectorName(connector,true)} get${getConnectorName(connector,false)?cap_first}(){
        return ${getConnectorName(connector,false)};
    }
    
    <#if implementsInterface>@Override</#if>
    public void set${getConnectorName(connector, false)?cap_first}(${getConnectorName(connector,true)} ${getConnectorName(connector,false)}){
        this.${getConnectorName(connector,false)}=${getConnectorName(connector,false)};
    }
        <#assign existing=existing+["${getConnectorName(connector,false)}"]>
      </#if>
    </#if>
  </#list>
</#if>
</#macro>