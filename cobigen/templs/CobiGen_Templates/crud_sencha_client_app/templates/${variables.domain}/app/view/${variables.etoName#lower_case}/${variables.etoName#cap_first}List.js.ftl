<#include '/methods.ftl'>
Ext.define('${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}List', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.${variables.etoName?lower_case}s',

    requires: [
        'Ext.grid.Panel',
        'Devon.grid.plugin.Pagination',
        '${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}ListVM',
        '${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}ListVC'
    ],

    closable: true,
    controller: '${variables.etoName?lower_case}-${variables.etoName?lower_case}s',

    title: i18n.${variables.etoName?lower_case}s.title,

    viewModel: {
        type: '${variables.etoName?lower_case}-${variables.etoName?lower_case}s'
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        {
          xtype: 'label',
          padding: 10,
          html: i18n.${variables.etoName?lower_case}s.html
        },
        {
          xtype: 'grid',
          reference: '${variables.etoName?lower_case}sgrid',
          flex: 1,
          padding: '0 10 10 10',
          allowDeselect: true,
          columns: [
                  <@generateGridColumns/>
          ],
  
          bind: {
              store: '{${variables.etoName?lower_case}s}',
              selection: '{selectedItem}'
          },
          plugins: ['pagination'],
          tbar: {
              items: [
                {
                  text: i18n.${variables.etoName?lower_case}s.buttons.add,
                  handler: 'onAddClick'
                },
                {
                  text: i18n.${variables.etoName?lower_case}s.buttons.edit,
                  bind: {
                      disabled: '{!selectedItem}'
                  },
                  handler: 'onEditClick'
                },
                {
                  text: i18n.${variables.etoName?lower_case}s.buttons.del,
                  bind: {
                      disabled: '{!selectedItem}'
                  },
                  handler: 'onDeleteClick'
                }, 
                '-'
              ]
          }
    }]
});
