<#include '/functions.ftl'>
Ext.define('${variables.component}.model.${variables.etoName?cap_first}.${variables.etoName}M', {
    extend: 'Ext.data.Model',
    fields: [
        <@generateSenchaModelFields/>
    ]
});