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
  <#assign extType=JavaUtil.getExtType(field.type)>
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



