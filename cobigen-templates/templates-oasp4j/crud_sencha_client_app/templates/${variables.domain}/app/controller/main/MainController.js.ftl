Ext.define('${variables.domain}.controller.main.MainController', {
    extend: 'Ext.app.Controller',

    requires: [
        '${variables.domain}.view.main.i18n.Main_en_EN',
        '${variables.domain}.view.main.i18n.Main_es_ES',
        '${variables.domain}.view.main.Home',
        '${variables.domain}.view.main.Header',
        '${variables.domain}.view.main.Menu',
        '${variables.domain}.view.main.Content',
        '${variables.domain}.view.main.LeftSidePanel',
        '${variables.domain}.view.main.${variables.domain?cap_first}Status'
    ]
});