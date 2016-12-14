<#include '/methods.ftl'>
Ext.define('${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}Crud', {
    extend: "Ext.panel.Panel",
    alias: 'widget.${variables.etoName?lower_case}crud',

    requires: [
        'Ext.panel.Panel',
        'Ext.form.Panel',
        '${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}CrudVM',
        '${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}CrudVC',
        'Devon.plugin.PreventDataLoss'
    ],

    controller: "${variables.etoName?lower_case}-crud-controller",

    viewModel: {
        type: "${variables.etoName?lower_case}-crud-model"
    },

    initComponent: function() {
        Ext.apply(this, {
            items: [{
                xtype: 'fieldset',
                title: i18n.${variables.etoName?lower_case}Crud.title,
                margin: 10,
                items: [
                    this.formPanel
                ]
            }]
        });
        this.callParent(arguments);

    },

    formPanel:  {
        xtype: 'form',
        plugins: [{
            ptype: 'preventdataloss'
        }],
        reference: 'panel',
        defaults: {
            margin: 5
        },
        bind:{
            values : '{${variables.etoName?lower_case}}'
        },
        items: [{
            xtype: 'hiddenfield',
            reference: 'id',
            name: 'id',
            bind: {
                value: '{${variables.etoName?lower_case}.id}'
            }
        },<@generateItemsCrud/>
        ]
        
    },
    
    bbar: [
        '->', {
            text: i18n.${variables.etoName?lower_case}Crud.submit,
            handler: 'on${variables.etoName?cap_first}CrudSubmit'
        }, {
            text: i18n.${variables.etoName?lower_case}Crud.cancel,
            handler: 'on${variables.etoName?cap_first}CrudDone'
        }
    ]
});