Ext.define('${variables.domain}.view.main.${variables.domain?cap_first}Status', {
    extend: 'Ext.tree.Panel',
    alias: 'widget.main-${variables.domain}-status',
    requires:['${variables.domain}.view.main.${variables.domain?cap_first}StatusVC'],
    title: i18n.${variables.domain?cap_first}Status.title,
    collapsible:true,
    controller: 'main-${variables.domain}-status',
    rootVisible: false,
    root: {
      expanded: true,
      id:'root',
        children: [{
            text: i18n.${variables.domain?cap_first}Status.${variables.etoName?lower_case}s,
            id:'${variables.etoName?lower_case}s',
            expanded: true
        }]
    },

    listeners: {
        select: 'onSelect'
    }

});