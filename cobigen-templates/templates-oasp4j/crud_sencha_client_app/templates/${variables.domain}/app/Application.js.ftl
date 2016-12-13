/**
 * The main application class. An instance of this class is created by app.js when it calls
 * Ext.application(). This is the ideal place to handle application launch and initialization
 * details.
 *
 *
 */
Ext.define('${variables.domain}.Application', {
    extend: 'Devon.App',

    name: '${variables.domain}',

    controllers: [
        '${variables.domain}.controller.main.MainController',
        '${variables.domain}.controller.${variables.etoName?lower_case}.${variables.etoName?cap_first}Controller'
    ],

    launch: function() {
        Devon.Log.trace('${variables.domain}.app launch');
        console.log('${variables.domain}.app launch');

        this.callParent(arguments);
    }
});
