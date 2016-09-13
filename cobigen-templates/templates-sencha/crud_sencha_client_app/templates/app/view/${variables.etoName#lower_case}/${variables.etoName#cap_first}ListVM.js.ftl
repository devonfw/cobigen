Ext.define('${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}ListVM', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.${variables.etoName?lower_case}-${variables.etoName?lower_case}s',
    requires: ['${variables.domain}.model.${variables.etoName?lower_case}.${variables.etoName?cap_first}'],

    data: {
        selectedItem: false,
        name: '${variables.domain}',
        stateFilter: null
    },

    stores: {
        ${variables.etoName?lower_case}s: {
            model: '${variables.domain}.model.${variables.etoName?lower_case}.${variables.etoName?cap_first}',
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