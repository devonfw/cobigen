Ext.define('${variables.domain}.view.${variables.etoName?lower_case}.${variables.etoName?cap_first}EditVC', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.${variables.etoName?lower_case}-edit-controller',

    control: {
        '${variables.etoName?lower_case}edit': {
            afterrender: 'onAfterRender'
        }
    },

    onAfterRender: function() {  
        var vm = this.getViewModel();

        this.get${variables.etoName?cap_first}(vm.get("${variables.etoName?lower_case}Id"));
    },

    get${variables.etoName?cap_first}: function(id) {
        Devon.rest.${variables.component}.${variables.etoName?lower_case}.get({
            scope: this,
            uriParams: {
                id: id
            },
            success: 'do something'
        });
    },

    ${variables.etoName?lower_case}EditCancel: function() {
        this.${variables.etoName?lower_case}EditClose();
    },


    ${variables.etoName?lower_case}EditClose: function() {
        this.getView().destroy();
    },

    ${variables.etoName?lower_case}EditSubmit: function() {

        var vm = this.getViewModel();
        var orderInfo = vm.get("orderInfo");
        var positions = vm.get("positions");

        var jsonData = {
            order: orderInfo.order,
            positions: Ext.Array.map(positions.getRange(),
                function(record) {
                    return record.getData({
                        serialize: true
                    });
                })
        };
    },

    positionRemove: function() {
        var model = this.getViewModel();
        var positions = model.get("positions");
        var selectedItem = model.get("selectedItem");

        positions.remove(selectedItem);
    },

    addPositionClick: function() {
        var vm = this.getViewModel();
        var offers = vm.get("offers");
        var positionValue = vm.get("positionSelected");

        var position = offers.findRecord('id', positionValue);

        var orderInfo = vm.get("orderInfo");

        vm.get("positions").add({
            id: null,
            orderId: orderInfo.order.id,
            offerId: position.get("id"),
            offerName: position.get("description"),
            state: ${variables.domain}.model.${variables.etoName?lower_case}.${variables.etoName?cap_first}.state.ORDERED,
            price: position.get("price")
        });
    }

});