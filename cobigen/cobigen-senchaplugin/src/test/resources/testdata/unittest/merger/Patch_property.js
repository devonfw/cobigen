Ext.define('restaurant.controller.table.Table', {
    extend: 'Ext.data.Modelo',
    proxy: {
        type: 'rest',
        url: Devon.Url.build('tablemanagement/v1/table'),
        newIterate : 'iterateAdded'
    },
    newProperty: 'added',
    fields: [{
        name: 'number',
        type: 'int'
    }, {
        name: 'patata',
        type: 'auto'
    }]
});


