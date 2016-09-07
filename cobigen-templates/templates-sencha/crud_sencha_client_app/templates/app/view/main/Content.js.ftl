/*
main-content is the panel where all main pages are opened.

It can be a tabpanel or a panel with cardlayout
*/
Ext.define('${variables.domain}.view.main.Content', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.main-content',
    id: 'main-content',

    items: [{
        xtype: 'main-home'
    }],
   
   
    cls: 'main-content',
    reference: 'content'

});