Ext.define('restaurant.controller.table.Table', {
    extend: 'Ext.data.Model',
    proxy: {
        type: 'rest',
        url: Devon.Url.build('tablemanagement/v1/table')
    },
    fields: [{
        name: 'number',
        type: 'int'
    }, {
        name: 'state',
        type: 'auto'
    }]
});