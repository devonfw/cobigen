Ext.define('${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}EditVM', {
    extend: 'Ext.app.ViewModel',
    
    alias: 'viewmodel.${variables.etoName?lower_case}-edit-model',

    data: {
        selectedItem: false,
    },

    stores: {
        
    }

});