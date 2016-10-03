<#-- ---------------------------------------- -->
<#-- GENERAL JAVA SPECIFIC FUNCTIONS & MACROS -->
<#-- ---------------------------------------- -->

<#function getSenchaType simpleType>
<#if simpleType=="byte">
<#return "auto">
<#elseif simpleType=="short">
<#return "int">
<#elseif simpleType=="int">
<#return "int">
<#elseif simpleType=="Long">
<#return "int">
<#elseif simpleType=="float">
<#return "float">
<#elseif simpleType=="double">
<#return "float">
<#elseif simpleType=="boolean">
<#return "boolean">
<#elseif simpleType=="char">
<#return "string">
<#elseif simpleType=="String">
<#return "string">
<#elseif simpleType=="Date">
<#return "date">
<#else>
<#return "auto">
</#if>
</#function>

<#-- -------------------- -->
<#-- OASP SPECIFIC MACROS -->
<#-- -------------------- -->

<#--
	Generates all field declaration
-->
<#macro generateSenchaModelFields>
  <#list pojo.fields as field>
    <#if field?has_next>
      { name: '${field.name}', type: '${getSenchaType(field.type)}' },
    <#else>
      { name: '${field.name}', type: '${getSenchaType(field.type)}' }
    </#if>
  </#list>
</#macro>

<#-- 
  Generates columns for list grid
-->
<#macro generateGridColumns>
  <#list pojo.fields as field>
    <#if field?has_next>
      <#if getSenchaType(field.type) == "auto">
        { text: 'i18n.${variables.etoName?lower_case}s.grid.${field.name}', dataIndex: '${field.name}' },
      <#else>
        { text: '${field.name?upper_case}', dataIndex: '${field.name}' },
      </#if>
    <#else>
      <#if getSenchaType(field.type) == "auto">
        { text: 'i18n.${variables.etoName?lower_case}s.grid.${field.name}', dataIndex: '${field.name}' }
      <#else>
        { text: '${field.name?upper_case}', dataIndex: '${field.name}' }
      </#if>
    </#if>
  </#list>
</#macro>

<#--
  Generates items for CRUD
-->
<#macro generateItemsCrud>
  <#list pojo.fields as field>
    <#if field?has_next>
      <#if getSenchaType(field.type) == "auto">
         {
            xtype: 'combo',
            reference: '${field.name}',
            name: '${field.name}',
            fieldLabel: i18n.${variables.etoName?lower_case}Crud.${field.name},
            tabIndex: 2,
            queryMode: 'local',
            displayField: 'code',
            valueField: 'code',
            bind: {
                store: '{${field.name}s',
                value: '{${variables.etoName?lower_case}.${field.name}}'
            },
            tabIndex: 1,
            minValue: 1
        },
      <#else>
        {
            xtype: '${field.name}field',
            reference: '${field.name}',
            name: '${field.name}',
            fieldLabel: i18n.${variables.etoName?lower_case}Crud.${field.name},
            bind: {
                value: '{${variables.etoName?lower_case}.${field.name}}'
            },
            tabIndex: 1,
            minValue: 1
        },
      </#if>
    <#else>
      <#if getSenchaType(field.type) == "auto">
       {
            xtype: 'combo',
            reference: '${field.name}',
            name: '${field.name}',
            fieldLabel: i18n.${variables.etoName?lower_case}Crud.${field.name},
            tabIndex: 2,
            queryMode: 'local',
            displayField: 'code',
            valueField: 'code',
            bind: {
                store: '{${field.name}s',
                value: '{${variables.etoName?lower_case}.${field.name}}'
            },
            tabIndex: 1,
            minValue: 1
        }
      <#else>
        {
            xtype: '${field.name}field',
            reference: '${field.name}',
            name: '${field.name}',
            fieldLabel: i18n.${variables.etoName?lower_case}Crud.${field.name},
            bind: {
                value: '{${variables.etoName?lower_case}.${field.name}}'
            },
            tabIndex: 1,
            minValue: 1
        }
      </#if>
    </#if>
  </#list>
</#macro>

<#--
  Generates CRUD data fields
-->
<#macro generateCrudData>
  <#list pojo.fields as field>
    <#if field?has_next>
      ${field.name}: null,
    <#else>
      ${field.name}: null
    </#if>
  </#list>
</#macro>

<#macro generateCrud>
  <#list pojo.fields as field>
      ${field.name}: ${field.name?upper_case},
  </#list>
</#macro>


