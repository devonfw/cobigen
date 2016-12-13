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

<#function getExtType simpleType>
  <#if simpleType=="short">
    <#return "Integer">
  <#elseif simpleType=="int">
    <#return "Integer">
  <#elseif simpleType=="Long">
    <#return "Integer">
  <#elseif simpleType=="float">
    <#return "Number">
  <#elseif simpleType=="double">
    <#return "Number">
  <#elseif simpleType=="boolean">
    <#return "Boolean">
  <#elseif simpleType=="char">
    <#return "String">
  <#elseif simpleType=="String">
    <#return "String">
  <#elseif simpleType=="Date">
    <#return "Date">
  <#else>
    <#return "Field">
  </#if>
</#function>


<#-- -------------------- -->
<#--   SPECIFIC MACROS    -->
<#-- -------------------- -->

<#-- 
  Generates columns for list grid
-->
<#macro generateGridMetaDataColumns>
  <#list pojo.fields as field>
                {
                    "type": "Ext.grid.column.Column",
                    "reference": {
                      "name": "columns",
                      "type": "array"
                    },
                    "codeClass": null,
                    "userConfig": {
                        "dataIndex": "${field.name}",
                        "text": "${field.name?cap_first}"
                     },
                     "name": "${field.name}Column"
                },              
  </#list>
</#macro>

<#--
  Generates CRUD data fields
-->
<#macro generateMetaDataModelFields>
  <#list pojo.fields as field>
  <#assign extType=getExtType(field.type)>
       {
              "type": "Ext.data.field.${extType}",
              "reference": {
                  "name": "fields",
                  "type": "array"
              },
              "codeClass": null,
              "userConfig": {
                  "name": "${field.name}"
              },
              "name": "${field.name}"
          },
  </#list>
</#macro>

<#--
  Generates CRUD data fields
-->
<#macro generateControllerMetaDataFields>
  <#list pojo.fields as field>
  		            "                    }, {",
                    "                        xtype: '${field.name}field',",
                    "                        reference: '${field.name}',",
                    "                        name: '${field.name}',",
                    "                        fieldLabel: i18n.${variables.etoName?lower_case}.${field.name},",
                    "                        bind: {",
                    "                            value: '{${variables.etoName?lower_case}.${field.name}}'",
                    "                        },",
                    "                        tabIndex: 1",
  </#list>
</#macro>



