<#include '/functions.ftl'>
{
    "type": "Ext.app.Controller",
    "reference": {
        "name": "items",
        "type": "array"
    },
    "codeClass": null,
    "userConfig": {
        "designer|userClassName": "${variables.etoName?cap_first}Controller",
        "listen": [
            "{",
            "            global: {",
            "                eventOpen${variables.etoName?cap_first}List: 'onMenuOpen${variables.etoName?cap_first}s',",
            "                event${variables.etoName?cap_first}Add: 'on${variables.etoName?cap_first}Add',",
            "                event${variables.etoName?cap_first}Edit: 'on${variables.etoName?cap_first}Edit'",
            "            }",
            "        }"
        ]
    },
    "name": "MyController",
    "designerId": "${IDGenerator.controllerId}",
    "cn": [
        {
            "type": "fixedfunction",
            "reference": {
                "name": "items",
                "type": "array"
            },
            "codeClass": null,
            "userConfig": {
                "fn": "init",
                "implHandler": [
                    "Devon.Ajax.define({",
                    "            '${variables.component}.${variables.etoName?lower_case}': {",
                    "                url: '${variables.component}/v1/${variables.etoName?lower_case}/{id}'",
                    "            },",
                    "            '${variables.component}.search': {",
                    "                url: '${variables.component}/v1/${variables.etoName?lower_case}/search',",
                    "                pagination: true",
                    "            }",
                    "        });"
                ]
            },
            "name": "init"
        },
        {
            "type": "basicfunction",
            "reference": {
                "name": "items",
                "type": "array"
            },
            "codeClass": null,
            "userConfig": {
                "fn": "on${variables.etoName?cap_first}Add",
                "implHandler": [
                    "    var window = Ext.create('Ext.window.Window', {",
                    "            title: i18n.${variables.etoName?lower_case}Edit.newTitle,",
                    "            width: 400,",
                    "            layout: 'fit',",
                    "            closable:false,",
                    "            resizable:false,",
                    "            modal:true,",
                    "            items: [{",
                    "                initComponent: function() {",
                    "                    Ext.apply(this, {",
                    "                        items: [{",
                    "                            xtype: 'fieldset',",
                    "                            title: i18n.${variables.etoName?lower_case}.title,",
                    "                            margin: 10,",
                    "                            items: [",
                    "                                this.formPanel",
                    "                            ]",
                    "                        }]",
                    "                    });",
                    "                    this.callParent(arguments);",
                    "",
                    "                },",
                    "",
                    "                formPanel:  {",
                    "                    xtype: 'form',",
                    "                    plugins: [{",
                    "                        ptype: 'preventdataloss'",
                    "                    }],",
                    "                    reference: 'panel',",
                    "                    defaults: {",
                    "                        margin: 5",
                    "                    },",
                    "                    bind:{",
                    "                        values : '{${variables.etoName?lower_case}}'",
                    "                    },",
                    "                    items: [{",
                    "                        xtype: 'hiddenfield',",
                    "                        reference: 'id',",
                    "                        name: 'id',",
                    "                        bind: {",
                    "                            value: '{${variables.etoName?lower_case}.id}'",
                    "                        }",
										 <@generateControllerMetaDataFields/>
                    "                    }]",
                    "",
                    "                },",
                    "",
                    "                bbar: [",
                    "                    '->', {",
                    "                        text: i18n.${variables.etoName?lower_case}.submit,",
                    "                        handler: 'this.on${variables.etoName?cap_first}Submit'",
                    "                    }, {",
                    "                        text: i18n.${variables.etoName?lower_case}.cancel,",
                    "                        handler: 'this.on${variables.etoName?cap_first}Done'",
                    "                    }",
                    "                ]",
                    "            }],",
                    "        listeners: {",
                    "            scope: this,",
                    "            eventDone: 'closeWindow'",
                    "        }",
                    "    }).show();"
                ]
            },
            "name": "on${variables.etoName?cap_first}Add"
        }
    ]
}