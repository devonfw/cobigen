<#include '/methods.ftl'>
Ext.define('${variables.domain}.model.${variables.etoName?lower_case}.${variables.etoName?cap_first}', {
    extend: 'Ext.data.Model',
    fields: [
        <@generateSenchaModelFields/>
    ],
    
    proxy: {
        type: 'rest',
        url: Devon.Url.build('${variables.component}/v1/${variables.etoName?lower_case}')
    }
});