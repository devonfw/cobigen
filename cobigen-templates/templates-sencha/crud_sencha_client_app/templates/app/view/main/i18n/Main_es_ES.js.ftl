Ext.define('${variables.domain}.view.main.i18n.Main_es_ES', {
    extend: 'Devon.I18nBundle',
    singleton: true,
    i18n: {
        main: {
            loggedInAs: 'Identificado como:',
            logOffButton: 'Salir',
            header: {
                title: 'Ejemplo aplicación de restaurante'
            },
            menu: {
                ${variables.etoName?lower_case}: '${variables.etoName?cap_first}ES',
                manage${variables.etoName?cap_first}s: 'manage${variables.etoName?cap_first}sES',
                new${variables.etoName?cap_first}s: 'new${variables.etoName?cap_first}sES'
            },
            home: {
                tabTitle: 'Inicio',
                content: 'Bienvenido al ejemplo de cliente para OASP con Sencha'
            },
            deleteConfirmMsg: 'Estás seguro de borrar el registro?'
        },
        ${variables.domain?cap_first}Status:{
            title:'${variables.domain?cap_first}',
            ${variables.etoName?lower_case}s: '${variables.etoName?cap_first}s'
        }
    }
});