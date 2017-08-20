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
<#elseif simpleType=="char">
<#return "string">
<#else>
<#return "string">
</#if>
</#function>

<#-- -------------------- -->
<#-- OASP SPECIFIC MACROS -->
<#-- -------------------- -->

<#--
	Generates all field declaration whereas Entity references will be converted to appropriate id references
-->
<#macro getNG2Type_Grid_Search>
  <#list pojo.fields as field>
      <md-input-container>
        <input mdInput name ="${field.name}" type="${getType(field.type)}" ngModel [placeholder]= "'${variables.etoName}DataGrid.cobigen_columns.${field.name}' | translate">
      </md-input-container>
   </#list>
</#macro>

<#macro getNG2Type_Add_Dialog>
<#list pojo.fields as field>
    <md-input-container style="width:100%;">
        <input mdInput type="${getType(field.type)}" name = "${field.name}" [placeholder]= "'${variables.etoName}DataGrid.cobigen_columns.${field.name}' | translate" [(ngModel)] = "cobigen_item.${field.name}" required>
    </md-input-container>
    </#list>
</#macro>

