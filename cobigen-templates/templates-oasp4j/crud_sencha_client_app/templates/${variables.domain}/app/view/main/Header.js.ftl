Ext.define('${variables.domain}.view.main.Header', {
    extend: 'Ext.Panel',

    alias: 'widget.main-header',

    requires: [
        'Ext.Img',
        '${variables.domain}.view.main.HeaderVC'
    ],

    layout: {
        type: 'hbox',
        align: 'middle'
    },
    border: false,

    controller: 'main-header',

    cls: 'main-header',
    height: 70,

    defaults: {
        border: false,
        bodyStyle: 'background: transparent; '
    },

    items: [{
        cls: 'main-header-title',
        xtype: 'label',
        html: i18n.main.header.title.replace(' ','<br/>')+'<img src="./resources/logo.jpg" style="margin-left:12px"/>'
    }, {
      xtype: 'component',
      flex:1
    }, {
        xtype: 'label',
        html: i18n.main.header.title,
        style: 'margin-right:5px',
        bind: {
            text: i18n.main.loggedInAs + '{currentUser.firstName} {currentUser.lastName}'
        }
    }, {
        xtype: 'combobox',
        style: 'margin-right:5px',
        editable: false,
        width: 100,
        queryMode: 'local',
        valueField: 'value',
        forceSelection: true,
        value: Devon.I18n.currentLocale,
        store: {
            fields: ['text', 'value'],
            data: [{
                'text': 'English',
                'value': 'en_EN'
            }, {
                'text': 'Castellano',
                'value': 'es_ES'
            }]
        },
        listeners: {
            change: 'languageChange'
        }
    }, {
        xtype: 'button',
        style: 'margin-right:5px',
        text: i18n.main.logOffButton,
        handler: 'onLogoffClick'
    }]
});