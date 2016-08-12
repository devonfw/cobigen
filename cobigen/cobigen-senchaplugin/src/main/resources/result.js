Base.File('restaurant.model.table.TableM', {
    extend: 'Ext.data.ExtendPatch',
    config : {
        listen: {
            global: {
                eventOpenTableList: 'onMenuOpenTables'
            }
        }
    },
    init: function() {
  Patch.File.define({PatchField: 'PatchValue'});
},
    trial: 'newProperty',
    fields : [
        {name: 'number', type: 'int', allowNull: true},
        {name: 'state', type: 'auto'},
        {name: 'newField', type: 'int'},
        {type: 'float', name: 'id'}
    ]
});