Ext.define('${variables.domain}.view.${variables.etoName?lower_case}.i18n.${variables.etoName?cap_first}_en_EN', {
    extend: 'Devon.I18nBundle',
    singleton: true,
    i18n: {
         ${variables.etoName?lower_case}s: {
            title: '${variables.etoName?cap_first}_EN',
            html: 'List of ${variables.etoName?lower_case}s_EN',
            grid: {
                id: 'ID',
                <#list pojo.fields as field>
                  <#if field?has_next>
                ${field.name?lower_case}: '${field.name?upper_case}_EN',
                  <#else>
                ${field.name?lower_case}: '${field.name?upper_case}_EN'
                  </#if>
                </#list>
            },
            buttons: {
                add: 'Add',
                edit: 'Edit',
                del: 'Delete',
                refresh: 'Refresh'
            }
        },

        ${variables.etoName?lower_case}Edit: {
            title: '${variables.etoName?lower_case}: ',
            newTitle: 'New ${variables.etoName?lower_case}_EN',
            add: 'Add',
            remove: 'Remove',
            submit: 'Submit',
            cancel: 'Cancel',
            html: 'Details for ${variables.etoName?lower_case}_EN #',
            grid: {
                title: 'Title',
                <#list pojo.fields as field>
                  <#if field?has_next>
                ${field.name?lower_case}: '${field.name?upper_case}_EN',
                  <#else>
                ${field.name?lower_case}: '${field.name?upper_case}_EN'
                  </#if>
                </#list>
            }
        },

        ${variables.etoName?lower_case}Crud: {
            title: 'Fill data',
            <#list pojo.fields as field>
            ${field.name?lower_case}: '${field.name?upper_case}_EN',
            </#list>
            submit: 'Submit',
            cancel: 'Cancel'
        }
    }
});