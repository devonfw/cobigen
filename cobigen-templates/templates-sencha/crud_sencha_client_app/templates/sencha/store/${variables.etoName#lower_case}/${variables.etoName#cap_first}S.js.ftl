Ext.define('${variables.rootPackage}.store.${variables.etoName?lower_case}.${variables.etoName?cap_first}S', {
    extend: 'Ext.data.Store',
    requires: ['${variables.rootPackage}.store.${variables.etoName?lower_case}.${variables.etoName?cap_first}M'],
    model: '${variables.rootPackage}.store.${variables.etoName?lower_case}.${variables.etoName?cap_first}M',
    alias: 'store.${variables.etoName?cap_first},
    storeId: 'myStore',
    autoLoad: true,
    proxy: {
        type: 'rest',
        url: Devon.Url.build('${variables.component}/v1/${variables.etoName?lower_case}'),
        withCredentials: true
    }
});