{
    "type": "Ext.app.ViewModel",
    "reference": {
        "name": "items",
        "type": "array"
    },
    "codeClass": null,
    "userConfig": {
        "data": [
            " {",
            "        selectedItem: false,",
            "        name: '${variables.domain}',",
            "        stateFilter:null",
            "    }"
        ],
        "designer|userAlias": "${variables.etoName?lower_case}",
        "designer|userClassName": "${variables.etoName?cap_first}sViewModel"
    },
    "designerId": "${IDGenerator.viewModelId}",
    "cn": [
        {
            "type": "Ext.data.Store",
            "reference": {
                "name": "stores",
                "type": "array"
            },
            "codeClass": null,
            "userConfig": {
                "autoLoad": true,
                "model": "${variables.etoName?cap_first}",
                "name": "${variables.etoName?lower_case}s",
                "pageSize": 10,
                "remoteFilter": true,
                "remoteSort": true
            },
            "name": "${variables.etoName?lower_case}s",
            "cn": [
                {
                    "type": "Ext.data.proxy.Ajax",
                    "reference": {
                        "name": "proxy",
                        "type": "object"
                    },
                    "codeClass": null,
                    "userConfig": {
                        "api": null,
                        "designer|createAlias": "${variables.component}.search",
                        "designer|displayName": "proxy"
                    },
                    "name": "MyAjaxProxy"
                }
            ]
        }
    ]
}