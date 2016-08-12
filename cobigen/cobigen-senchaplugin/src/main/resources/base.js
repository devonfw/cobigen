Ext.define('restaurant.model.table.TableM', {
	extend: 'Ext.data.Model',
	config: {
		listen: {
			global: {
				eventOpenTableList: 'onMenuOpenTables'
			}
		}
	},
	fields: [
	     { name: 'id', type: 'int' },
	     { name: 'number', type: 'int', allowNull: true },
	     { name: 'state', type: 'auto'}
	],
	init: function() {
		Base.File.define({
			fieldBase : 'BaseValue',
		});
	}
});