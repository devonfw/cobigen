Ext.define('${variables.rootPackage}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}ListVM', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.${variables.etoName?lower_case}-${variables.etoName?lower_case}s',
    requires: ['${variables.rootPackage}.model.${variables.etoName?lower_case}.${variables.etoName?cap_first}'],

    data: {
        selectedItem: false,
        name: '${variables.rootPackage}',
        stateFilter: null
    },

    stores: {
        ${variables.etoName?lower_case}s: {
            model: '${variables.rootPackage}.model.${variables.etoName?lower_case}.${variables.etoName?cap_first}',
            pageSize: 3,
            proxy: {
                type: '${variables.component}.search',
                extraParams:'{stateFilter}'
            },
            autoLoad: true,
            remoteSort:true,
            remoteFilter:true,
            sorters: {property:'number', direction:'ASC'}
        }
    },

    formulas: {
        
    }

});