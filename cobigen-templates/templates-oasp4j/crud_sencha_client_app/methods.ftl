<#-- -------------------- -->
<#-- OASP SPECIFIC MACROS -->
<#-- -------------------- -->

<#--
  Generates all field declaration
-->
<#macro generateSenchaModelFields>
  <#list pojo.fields as field>
    <#if field?has_next>
      { name: '${field.name}', type: '${JavaUtil.getSenchaType(field.type)}' },
    <#else>
      { name: '${field.name}', type: '${JavaUtil.getSenchaType(field.type)}' }
    </#if>
  </#list>
</#macro>

<#-- 
  Generates columns for list grid
-->
<#macro generateGridColumns>
  <#list pojo.fields as field>
    <#if field?has_next>
        { text: i18n.${variables.etoName?lower_case}s.grid.${field.name?lower_case}, dataIndex: '${field.name}' },
    <#else>
        { text: i18n.${variables.etoName?lower_case}s.grid.${field.name?lower_case}, dataIndex: '${field.name}' }
    </#if>
  </#list>
</#macro>

<#--
  Generates items for CRUD
-->
<#macro generateItemsCrud>
  <#assign x=1>
  <#list pojo.fields as field>
    <#if field?has_next>
      <#if JavaUtil.getSenchaType(field.type) == "auto">
         {
            xtype: 'combo',
            reference: '${field.name}',
            name: '${field.name}',
            fieldLabel: i18n.${variables.etoName?lower_case}Crud.${field.name?lower_case},
            queryMode: 'local',
            displayField: 'code',
            valueField: 'code',
            bind: {
                store: '{${field.name}s}',
                value: '{${variables.etoName?lower_case}.${field.name?lower_case}}'
            },
            tabIndex: ${x},
            minValue: 1
        },
      <#else>
        <#if JavaUtil.getSenchaType(field.type) == 'int'>
          {
              xtype: 'numberfield',
              reference: '${field.name?lower_case}',
              name: '${field.name?lower_case}',
              fieldLabel: i18n.${variables.etoName?lower_case}Crud.${field.name?lower_case},
              bind: {
                  value: '{${variables.etoName?lower_case}.${field.name}}'
              },
              tabIndex: ${x},
              minValue: 1
          },
        <#elseif JavaUtil.getSenchaType(field.type)=='string'>
          {
              xtype: 'textfield',
              reference: '${field.name?lower_case}',
              name: '${field.name?lower_case}',
              fieldLabel: i18n.${variables.etoName?lower_case}Crud.${field.name?lower_case},
              bind: {
                  value: '{${variables.etoName?lower_case}.${field.name}}'
              },
              tabIndex: ${x},
              minValue: 1
          },
        </#if>
      </#if>
    <#else>
      <#if JavaUtil.getSenchaType(field.type) == "auto">
       {
            xtype: 'combo',
            reference: '${field.name}',
            name: '${field.name}',
            fieldLabel: i18n.${variables.etoName?lower_case}Crud.${field.name?lower_case},
            tabIndex: 2,
            queryMode: 'local',
            displayField: 'code',
            valueField: 'code',
            bind: {
                store: '{${field.name}s',
                value: '{${variables.etoName?lower_case}.${field.name}}'
            },
            tabIndex: ${x},
            minValue: 1
        }
      <#else>
        <#if JavaUtil.getSenchaType(field.type) == 'int'>
          {
              xtype: 'numberfield',
              reference: '${field.name?lower_case}',
              name: '${field.name?lower_case}',
              fieldLabel: i18n.${variables.etoName?lower_case}Crud.${field.name?lower_case},
              bind: {
                  value: '{${variables.etoName?lower_case}.${field.name}}'
              },
              tabIndex: ${x},
              minValue: 1
          }
        <#elseif JavaUtil.getSenchaType(field.type)=='string'>
          {
              xtype: 'textfield',
              reference: '${field.name?lower_case}',
              name: '${field.name?lower_case}',
              fieldLabel: i18n.${variables.etoName?lower_case}Crud.${field.name?lower_case},
              bind: {
                  value: '{${variables.etoName?lower_case}.${field.name}}'
              },
              tabIndex: ${x},
              minValue: 1
          }
        </#if>
      </#if>
    </#if>
    <#assign x++>
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
