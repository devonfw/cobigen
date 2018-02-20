Ext.define('${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}Edit', {
    extend: "Ext.panel.Panel",
    alias: 'widget.${variables.etoName?lower_case}edit',

    requires: [
        '${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}EditVM',
        '${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}EditVC'
    ],

    controller: "${variables.etoName?lower_case}-edit-controller",

    viewModel: {
        type: "${variables.etoName?lower_case}-edit-model"
    },

    closable: true,

    bind: {
        loading: '{!orderInfo}'
    },

    tbar: {
        items: [
            '->', {
                text: i18n.${variables.etoName?lower_case}Edit.submit,
                handler: '${variables.etoName?lower_case}EditSubmit'
            }, {
                text: i18n.${variables.etoName?lower_case}Edit.cancel,
                handler: '${variables.etoName?lower_case}EditCancel'
            }
        ]
    },

    items: [{
        padding: 10,
        bind: {
            html: i18n.${variables.etoName?lower_case}Edit.html + '{${variables.etoName?lower_case}Id}'
        },
        border: false
    }, {
        layout: {
            type: 'hbox',
            pack: 'center',
            align: 'middle'
        },
        padding: 10,
        border: false,
        items: [{
            xtype: 'combo',
            fieldLabel: i18n.${variables.etoName?lower_case}Edit.orderPos,
            reference: 'offerCombo',
            valueField: 'id',
            displayField: 'description',
            bind: {
                store: '{offers}',
                value: '{positionSelected}'
            }
        }, {
            xtype: 'button',
            text: i18n.${variables.etoName?lower_case}Edit.add,
            handler: 'addPositionClick',
            bind: {
                disabled: '{!positionSelected}'
            }
        }]
    }, {
        xtype: 'grid',
        reference: 'menugrid',
        allowDeselect: true,

        columns: [{
            text: i18n.${variables.etoName?lower_case}Edit.grid.number,
            dataIndex: 'id'
        }, {
            text: i18n.${variables.etoName?lower_case}Edit.grid.title,
            dataIndex: 'offerName',
            flex: 1
        }, {
            text: i18n.${variables.etoName?lower_case}Edit.grid.status,
            dataIndex: 'state'
        }],
        bind: {
            store: '{positions}',
            selection: '{selectedItem}'
        },
        bbar: {
            items: [{
                    text: i18n.${variables.etoName?lower_case}Edit.remove,
                    bind: {
                        disabled: '{!selectedItem}'
                    },
                    handler: 'positionRemove'
                },
                '->', {
                    text: i18n.${variables.etoName?lower_case}Edit.submit,
                    handler: '${variables.etoName?lower_case}EditSubmit'
                }, {
                    text: i18n.${variables.etoName?lower_case}Edit.cancel,
                    handler: '${variables.etoName?lower_case}EditCancel'
                }
            ]
        }
    }]
});