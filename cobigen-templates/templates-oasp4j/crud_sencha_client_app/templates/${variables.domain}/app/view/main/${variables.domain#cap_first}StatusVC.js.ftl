Ext.define('${variables.domain}.view.main.${variables.domain?cap_first}StatusVC', {
  extend: 'Ext.app.ViewController',

    alias: 'controller.main-${variables.domain}-status',

    listen: {
        global: {
            event${variables.etoName?cap_first}sChanged: 'on${variables.etoName?cap_first}sChanged'
        }
    },

    control: {
        '#': {
            afterrender: 'onAfterRender'
        }
    },

    on${variables.etoName?cap_first}sChanged: function(){
        this.refreshTree();
    },

    onAfterRender: function() {
        this.refreshTree();
    },

    refreshTree: function(){
        var store=this.getView().getStore();
        Devon.rest.${variables.component}.search.post({
            jsonData:{}
        });
    },

    onSelect : function(tree, record){
        var nodeId=record.getId();
        if(Ext.String.startsWith(nodeId,'${variables.etoName?lower_case}')){
            var title=i18n.${variables.etoName?lower_case}s.title;
            Ext.GlobalEvents.fireEvent('eventOpen${variables.etoName?cap_first}List',{title:title});
        }
    }

});