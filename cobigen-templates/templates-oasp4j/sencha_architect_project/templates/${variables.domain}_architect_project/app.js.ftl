Ext.application({
    models: [
        '${variables.etoName?cap_first}'
    ],
    controllers: [
        '${variables.etoName?cap_first}Controller'
    ],
    views: [
        '${variables.etoName?cap_first}s'
    ],
    stores: [
    ],
    name: '${variables.domain}'
});