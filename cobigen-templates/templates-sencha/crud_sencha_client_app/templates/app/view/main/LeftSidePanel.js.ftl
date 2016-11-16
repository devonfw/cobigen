Ext.define('${variables.domain}.view.main.LeftSidePanel', {
    extend: 'Ext.Panel',

    alias: 'widget.main-leftsidepanel',

    requires: [
        '${variables.domain}.view.main.${variables.domain?cap_first}Status'
    ],

    cls:'main-leftsidepanel',
    width: 220,
    bodyPadding:0,
    resizable: {
        handles: 'e',
        pinned: true
    },

    layout: {
        type:'vbox',
      align:'stretch'
    },

    items: [{
        xtype: 'main-${variables.domain}-status',
        height:220
    }]
});