{
    "paths": {
        "tmp": ".tmp",
        "dist": "dist",
        "app": "app",
        "test": "test"
    },
    "modules": [
        "main",
        "oasp-mock",
        "oasp",
        "oasp/oasp-security",
        "oasp/oasp-ui",
        "oasp/oasp-i18n",
        "${variables.component}"
    ],
    "appBuild": true,
    "proxy": "http://localhost:8081",
    "proxyContext": "/oasp4j-sample-server"
}