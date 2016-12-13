<#include '/functions.ftl'>
{
    "type": "Ext.data.Model",
    "reference": {
        "name": "items",
        "type": "array"
    },
    "codeClass": null,
    "userConfig": {
        "designer|userClassName": "${variables.etoName?cap_first}"
    },
    "name": "${variables.etoName?cap_first}Model",
    "designerId": "${IDGenerator.modelId}",
    "cn": [
        <@generateMetaDataModelFields/>
        {
            "type": "Ext.data.proxy.Rest",
            "reference": {
                "name": "proxy",
                "type": "object"
            },
            "codeClass": null,
            "userConfig": {
                "url": "Devon.Url.build('${variables.component}/v1/${variables.etoName?lower_case}')"
            },
            "name": "MyRestProxy"
        }
    ]
}