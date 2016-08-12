Ext.define('restaurant.model.table.TableM', {
	extend: 'Ext.data.ExtendPatch',
	trial: 'newProperty',
	fields: [
	     { name: 'newField', type: 'int'},
	     { type: 'float', name: 'id'}

	],
	init: function() {
		Patch.File.define({
			PatchField : 'PatchValue',
		});
	}
});
