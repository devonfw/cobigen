<#-- -------------------- -->
<#-- SPECIFIC MACROS -->
<#-- -------------------- -->

<#-- Adds the input fields for the filter with types -->
<#macro getNG2Type_Grid_Search>
  <#list elemDoc["self::node()/ownedAttribute"] as field>
      <md-input-container style="width:100%;">
        <#if field["type/@xmi:idref"]?has_content>
          <#assign fieldType=field["type/@xmi:idref"]?replace("EAJava_","")>
          <input mdInput name ="${field["@name"]}"  type="${JavaUtil.getSimpleType(fieldType)}" ngModel [placeholder]= "'${variables.component}datagrid.columns.${field["@name"]}' | translate">
        </#if>
      </md-input-container>
   </#list>
</#macro>

<#-- Adds the input fields for the New Element dialog with types -->
<#macro getNG2Type_Add_Dialog>
<#list elemDoc["self::node()/ownedAttribute"] as field>
    <md-input-container style="width:100%;">
    <#if field["type/@xmi:idref"]?has_content>
      <#assign fieldType=field["type/@xmi:idref"]?replace("EAJava_","")>
      <input mdInput type="${JavaUtil.getSimpleType(fieldType)}" name = "${field["@name"]}" [placeholder]= "'${variables.component}datagrid.columns.${field["@name"]}' | translate" [(ngModel)] = "items.${field["@name"]}" required>
    </#if>
    </md-input-container>
   </#list>
</#macro>
