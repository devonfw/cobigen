{
    "type": "Ext.app.ViewController",
    "reference": {
        "name": "items",
        "type": "array"
    },
    "codeClass": null,
    "userConfig": {
        "designer|userAlias": "${variables.etoName?lower_case}s",
        "designer|userClassName": "${variables.etoName?cap_first}sViewController",
        "listen": [
            " {",
            "        global: {",
            "            event${variables.etoName?cap_first}sChanged: 'on${variables.etoName?cap_first}sChanged'",
            "        }",
            "    }"
        ]
    },
    "designerId": "${IDGenerator.viewControllerId}",
    "cn": [
        {
            "type": "basicfunction",
            "reference": {
                "name": "items",
                "type": "array"
            },
            "codeClass": null,
            "userConfig": {
                "designer|params": [
                    "view",
                    "record",
                    "item",
                    "index",
                    "e",
                    "eOpts"
                ],
                "fn": "onEditDblclick",
                "implHandler": [
                    "Ext.GlobalEvents.fireEvent('event${variables.etoName?cap_first}Edit', {",
                    "            id: record.get('id')",
                    "        });"
                ]
            },
            "name": "onEditDblclick"
        },
        {
            "type": "basicfunction",
            "reference": {
                "name": "items",
                "type": "array"
            },
            "codeClass": null,
            "userConfig": {
                "fn": "refreshGrid",
                "implHandler": [
                    "var grid = this.lookupReference('${variables.etoName?lower_case}sgrid');",
                    "grid.getStore().reload();"
                ]
            },
            "name": "refreshGrid"
        },
        {
            "type": "basicfunction",
            "reference": {
                "name": "items",
                "type": "array"
            },
            "codeClass": null,
            "userConfig": {
                "fn": "on${variables.etoName?cap_first}sChanged",
                "implHandler": [
                    "this.refreshGrid();"
                ]
            },
            "name": "on${variables.etoName?cap_first}sChanged"
        }
    ]
}