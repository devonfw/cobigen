Ext.define('${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}ListVC', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.${variables.etoName?lower_case}-${variables.etoName?lower_case}s',

    requires: [
        'Ext.grid.Panel',
        '${variables.domain}.model.${variables.etoName?lower_case}.${variables.etoName?cap_first}'
    ],

    listen: {
        global: {
            event${variables.etoName?cap_first}sChanged: 'on${variables.etoName?cap_first}sChanged'
        }
    },

    markSelectedAs: function(status) {
        var me = this;
        var table = me.getViewModel().get('selectedItem').data;
        table.state = status;

        Devon.rest.${variables.component}.${variables.etoName?lower_case}.post({
            scope: me,
            jsonData: ${variables.etoName?lower_case},
            success: function(){
                Ext.GlobalEvents.fireEvent('event${variables.etoName?cap_first}sChanged');
            }
        });

    },

    onAddClick: function() {
        Ext.GlobalEvents.fireEvent('event${variables.etoName?cap_first}Add');
    },

    onEditClick: function() {
        var rec = this.getViewModel().get('selectedItem');
        Ext.GlobalEvents.fireEvent('event${variables.etoName?cap_first}Edit', {
            id: rec.id
        });
    },

    onDeleteClick: function() {
        var me = this;

        Ext.MessageBox.confirm('Confirmar', i18n.main.deleteConfirmMsg,
            function(buttonPressed) {
                if (buttonPressed == 'no' || buttonPressed == 'cancel') {
                    return;
                }

                var rec = me.getViewModel().get('selectedItem');

                Devon.rest.${variables.component}.${variables.etoName?lower_case}.del({
                    scope: me,
                    uriParams: {
                        id: rec.get('id')
                    },
                    success: me.refreshGrid
                });

            });

    },

    refreshGrid: function() {
        var grid = this.lookupReference('${variables.etoName?lower_case}sgrid');
        grid.getStore().reload();
    },

    on${variables.etoName?cap_first}sChanged: function() {
        this.refreshGrid();
    }

});