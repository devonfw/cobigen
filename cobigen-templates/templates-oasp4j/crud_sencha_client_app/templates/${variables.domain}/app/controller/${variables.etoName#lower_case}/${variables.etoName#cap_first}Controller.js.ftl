Ext.define('${variables.domain}.controller.${variables.etoName?lower_case}.${variables.etoName?cap_first}Controller', {
    extend: 'Ext.app.Controller',

    requires: [
        '${variables.domain}.view.${variables.etoName?lower_case}.i18n.${variables.etoName?cap_first}_en_EN',
        '${variables.domain}.view.${variables.etoName?lower_case}.i18n.${variables.etoName?cap_first}_es_ES',
        '${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}List',
        '${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}Crud'
    ],

    config: {
        listen: {
            global: {
                eventOpen${variables.etoName?cap_first}List: 'onMenuOpen${variables.etoName?cap_first}s',
                event${variables.etoName?cap_first}Add: 'on${variables.etoName?cap_first}Add',
                event${variables.etoName?cap_first}Edit: 'on${variables.etoName?cap_first}Edit'
            }
        }
    },

    init: function() {
        Devon.Ajax.define({
            '${variables.component}.${variables.etoName?lower_case}': {
                url: '${variables.component}/v1/${variables.etoName?lower_case}/{id}'
            },
            '${variables.component}.search': {
                url: '${variables.component}/v1/${variables.etoName?lower_case}/search',
                pagination: true
            }
        });
    },

    onMenuOpen${variables.etoName?cap_first}s: function(options) {
        var ${variables.etoName?lower_case}s = new ${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}List(options);

        Devon.Application.openInContentPanel(${variables.etoName?lower_case}s);

    },

    //We use window for add case to show an example of how to work with window
    on${variables.etoName?cap_first}Add: function() {
        var window = Ext.create('Ext.window.Window', {
            title: i18n.${variables.etoName?lower_case}Edit.newTitle,
            width: 400,
            layout: 'fit',
            closable:false,
            resizable:false,
            modal:true,
            items: [{
                xtype:'${variables.etoName?lower_case}crud'
            }],
            listeners: {
                scope: this,
                eventDone: 'closeWindow'
            }
        }).show();
    },

    //We use tab for edit case to show an example of how to edit multiple ${variables.etoName?lower_case}s in different tabs
    on${variables.etoName?cap_first}Edit: function(${variables.etoName?lower_case}Selected) {
        var id = ${variables.etoName?lower_case}Selected.id;
        var panel = new ${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}Crud({
            title: i18n.${variables.etoName?lower_case}Edit.title + id,
            closable:true,
            viewModel: {
                data: {
                    ${variables.etoName?lower_case}Id: id
                }
            }
        });
        
        Devon.Application.openInContentPanel(panel, {id: id});
        
        Ext.GlobalEvents.fireEvent('openedInContentPanel', panel, {id:id});
    },

    closeWindow: function(window){
        window.close();
    }
});