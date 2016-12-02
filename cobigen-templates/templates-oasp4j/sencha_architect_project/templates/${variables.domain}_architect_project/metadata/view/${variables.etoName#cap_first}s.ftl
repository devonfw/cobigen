<#include '/functions.ftl'>
{
    "type": "Ext.panel.Panel",
    "reference": {
        "name": "items",
        "type": "array"
    },
    "codeClass": null,
    "userConfig": {
        "designer|initialView": false,
        "designer|userAlias": "${variables.etoName?lower_case}s",
        "designer|userClassName": "${variables.etoName?cap_first}s",
        "height": "auto",
        "title": "${variables.etoName?cap_first}s",
        "width": "auto"
    },
    "name": "MyPanel",
    "designerId": "${IDGenerator.viewId}",
    "viewControllerInstanceId": "${IDGenerator.viewControllerId}",
    "viewModelInstanceId": "${IDGenerator.viewModelId}",
    "cn": [
        {
            "type": "Ext.form.Label",
            "reference": {
                "name": "items",
                "type": "array"
            },
            "codeClass": null,
            "userConfig": {
                "designer|createAlias": "label",
                "designer|displayName": "${variables.etoName?lower_case}Label",
                "dock": null,
                "html": "List of ${variables.etoName?lower_case}s",
                "padding": 10,
                "text": null,
                "width": null
            },
            "name": "MyLabel"
        },
        {
            "type": "Ext.grid.Panel",
            "reference": {
                "name": "items",
                "type": "array"
            },
            "codeClass": null,
            "userConfig": {
                "allowDeselect": true,
                "designer|createAlias": "grid",
                "padding": "0 10 10 10",
                "reference": "${variables.etoName?lower_case}sgrid",
                "selection": null,
                "store": [
                    "{${variables.etoName?lower_case}s}"
                ],
                "subGridXType": null,
                "title": null
            },
            "name": "MyGridPanel",
            "configAlternates": {
                "store": "binding",
                "vbvb": "object"
            },
            "cn": [
                {
                    "type": "Ext.view.Table",
                    "reference": {
                        "name": "items",
                        "type": "array"
                    },
                    "codeClass": null,
                    "userConfig": {
                        "designer|displayName": "${variables.etoName?lower_case}grid"
                    },
                    "name": "MyTable"
                },
                          <@generateGridMetaDataColumns/>
                {
                    "type": "Ext.toolbar.Toolbar",
                    "reference": {
                        "name": "dockedItems",
                        "type": "array"
                    },
                    "codeClass": null,
                    "userConfig": {
                        "designer|createAlias": "",
                        "designer|displayName": "toolbar",
                        "dock": "top"
                    },
                    "name": "MyToolbar",
                    "cn": [
                        {
                            "type": "Ext.button.Button",
                            "reference": {
                                "name": "items",
                                "type": "array"
                            },
                            "codeClass": null,
                            "userConfig": {
                                "layout|flex": null,
                                "text": "Add"
                            },
                            "name": "MyButton",
                            "cn": [
                                {
                                    "type": "fixedfunction",
                                    "reference": {
                                        "name": "items",
                                        "type": "array"
                                    },
                                    "codeClass": null,
                                    "userConfig": {
                                        "designer|params": [
                                            "button",
                                            "e"
                                        ],
                                        "designer|viewControllerFn": "onAddClick",
                                        "fn": "handler",
                                        "implHandler": [
                                            "Ext.GlobalEvents.fireEvent('event${variables.etoName?cap_first}Add');"
                                        ]
                                    },
                                    "name": "handler"
                                }
                            ]
                        },
                        {
                            "type": "Ext.button.Button",
                            "reference": {
                                "name": "items",
                                "type": "array"
                            },
                            "codeClass": null,
                            "userConfig": {
                                "layout|flex": null,
                                "text": "Edit"
                            },
                            "name": "MyButton1",
                            "cn": [
                                {
                                    "type": "fixedfunction",
                                    "reference": {
                                        "name": "items",
                                        "type": "array"
                                    },
                                    "codeClass": null,
                                    "userConfig": {
                                        "designer|params": [
                                            "button",
                                            "e"
                                        ],
                                        "designer|viewControllerFn": "onEditClick",
                                        "fn": "handler",
                                        "implHandler": [
                                            "var rec = this.getViewModel().get('selectedItem');",
                                            "        Ext.GlobalEvents.fireEvent('event${variables.etoName?cap_first}Edit', {",
                                            "            id: rec.id",
                                            "        });"
                                        ]
                                    },
                                    "name": "handler"
                                }
                            ]
                        },
                        {
                            "type": "Ext.button.Button",
                            "reference": {
                                "name": "items",
                                "type": "array"
                            },
                            "codeClass": null,
                            "userConfig": {
                                "layout|flex": null,
                                "text": "Delete"
                            },
                            "name": "MyButton2",
                            "cn": [
                                {
                                    "type": "fixedfunction",
                                    "reference": {
                                        "name": "items",
                                        "type": "array"
                                    },
                                    "codeClass": null,
                                    "userConfig": {
                                        "designer|params": [
                                            "button",
                                            "e"
                                        ],
                                        "designer|viewControllerFn": "onDeleteClick",
                                        "fn": "handler",
                                        "implHandler": [
                                            "var me = this;",
                                            "",
                                            "        Ext.MessageBox.confirm('Confirmar', i18n.main.deleteConfirmMsg,",
                                            "            function(buttonPressed) {",
                                            "                if (buttonPressed == 'no' || buttonPressed == 'cancel') {",
                                            "                    return;",
                                            "                }",
                                            "",
                                            "                var rec = me.getViewModel().get('selectedItem');",
                                            "",
                                            "                Devon.rest.${variables.component}.${variables.etoName?lower_case}.del({",
                                            "                    scope: me,",
                                            "                    uriParams: {",
                                            "                        id: rec.get('id')",
                                            "                    },",
                                            "                    success: me.refreshGrid",
                                            "                });",
                                            "",
                                            "            });"
                                        ]
                                    },
                                    "name": "handler"
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
}