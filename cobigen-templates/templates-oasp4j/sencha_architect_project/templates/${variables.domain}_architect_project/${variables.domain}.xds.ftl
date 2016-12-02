{
    "name": "${variables.domain}",
    "settings": {
        "cmd": {
            "license": "commercial"
        },
        "urlPrefixTemplate": "http://localhost:{port}/{path}",
        "urlPrefix": "http://localhost/",
        "canvasControls": {
            "viewportSize": {
                "name": "Auto Expand",
                "builtIn": true,
                "height": null,
                "width": null
            }
        },
        "exportPath": ""
    },
    "xdsVersion": "4.0.1",
    "xdsBuild": 190,
    "schemaVersion": 1,
    "upgradeVersion": 310000000001,
    "framework": "ext60",
    "viewOrderMap": {
        "view": [
            "${IDGenerator.viewId}",
            "${IDGenerator.viewControllerId}",
            "${IDGenerator.viewModelId}"
        ],
        "store": [],
        "controller": [
            "${IDGenerator.controllerId}"
        ],
        "model": [
            "${IDGenerator.modelId}"
        ],
        "resource": [
        ],
        "app": [
            "application"
        ]
    }
}