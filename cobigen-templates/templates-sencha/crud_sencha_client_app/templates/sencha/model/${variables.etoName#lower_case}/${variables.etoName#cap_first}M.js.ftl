<#include '/functions.ftl'>
Ext.define('${variables.rootPackage}.model.${variables.etoName?cap_first}.${variables.etoName}M', {
    extend: 'Ext.data.Model',
    fields: [
        <@generateSenchaModelFields/>
    ]
});