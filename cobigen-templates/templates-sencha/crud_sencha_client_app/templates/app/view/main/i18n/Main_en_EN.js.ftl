Ext.define('${variables.domain}.view.main.i18n.Main_en_EN', {
    extend: 'Devon.I18nBundle',
    singleton: true,
    i18n: {
        main: {
            loggedInAs: 'Logged in as:',
            logOffButton: 'Log off',
            header: {
                title: '${variables.domain?cap_first} Application '
            },
            menu: {
                ${variables.etoName?lower_case}: '${variables.etoName?cap_first}sEN',
                manage${variables.etoName?cap_first}s: 'manage${variables.etoName?cap_first}sEN',
                new${variables.etoName?cap_first}s: 'new${variables.etoName?cap_first}sEN'
            },
            home: {
                tabTitle: 'Home',
                content: 'Welcome to the ${variables.domain?upper_case} Sencha client'
            },
            deleteConfirmMsg: 'Are you sure to delete this record?'
        },
        ${variables.domain?cap_first}Status:{
            title:'${variables.domain?cap_first}',
            ${variables.etoName?lower_case}s: '${variables.etoName?cap_first}s'
        }
    }
});