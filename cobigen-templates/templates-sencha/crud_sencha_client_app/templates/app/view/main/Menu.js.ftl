Ext.define('${variables.domain}.view.main.Menu', {
    extend: 'Ext.Panel',

    alias: 'widget.main-menu',

    requires: [
        'Ext.toolbar.Toolbar',

        //by default use the Devon VC for this menu
        'Devon.view.main.MenuVC'
    ],

    controller: 'main-menu',
    cls:'main-menu',
    buttonAlign:'left',
    buttons: [{
        text: i18n.main.menu.${variables.etoName?lower_case},
        menu:[{
            text: i18n.main.menu.manage${variables.etoName?cap_first}s,
            eventName: 'eventOpen${variables.etoName?cap_first}List'
        },{
            text: i18n.main.menu.new${variables.etoName?cap_first}s,
            eventName: 'event${variables.etoName?cap_first}Add'
        }]   
    }]

});
