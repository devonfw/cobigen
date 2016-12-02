Ext.define('${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}CrudVC', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.${variables.etoName?lower_case}-crud-controller',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        }
    },

    onAfterRender: function() {
        var vm = this.getViewModel();
        var id = vm.get("${variables.etoName?lower_case}Id");

        if (id) {
            Devon.rest.${variables.component}.${variables.etoName?lower_case}.get({
                scope: this,
                uriParams: {
                    id: id
                },
                success: function(${variables.etoName?lower_case}) {
                    vm.set('${variables.etoName?lower_case}', ${variables.etoName?lower_case});
                }
            });
        }
    },

    on${variables.etoName?cap_first}CrudDone: function() {
        //Fire close event
        var parent =  this.getView().up();
        
        //If window we fire event
        if(parent.xtype=='window'){
            parent.fireEvent('eventDone', parent);
        }
        //If tabpanel, we close the tab
        else{
            this.getView().close();
        }
    },
    
    ${variables.etoName?lower_case}CrudSubmit: function() {
        //Fire event ${variables.etoName?lower_case} changed
        Ext.GlobalEvents.fireEvent('event${variables.etoName?cap_first}sChanged');
        
        //Fire close window event
        this.on${variables.etoName?cap_first}CrudDone();
    },

    on${variables.etoName?cap_first}CrudSubmit: function() {
        var form = this.getView().down('form');

        if (form.isValid()) {
            Devon.rest.${variables.component}.${variables.etoName?lower_case}.post({
                scope: this,
                jsonData: this.getViewModel().get('${variables.etoName?lower_case}'),
                referenceView: 'panel',
                success: this.${variables.etoName?lower_case}CrudSubmit
            });
        }
    }

});