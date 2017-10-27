<#include '/methods.ftl'>
Ext.define('${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}CrudVM', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.${variables.etoName?lower_case}-crud-model',

    data:{
        ${variables.etoName?lower_case}: {
            id : null,
            <@generateCrudData/>
        }
    },

    ${variables.etoName?lower_case}Id: null,

    stores: {
     // Here your stores configurations
     
     // Example for states field of oasp4j-sample Tables
     // states: {
     //   fields: ['code'],
     //   data: [{
     //       'code': 'FREE'
     //   }, {
     //       'code': 'OCCUPIED'
     //   }, {
     //      'code': 'RESERVED'
     //   }]
     // }
    }

});