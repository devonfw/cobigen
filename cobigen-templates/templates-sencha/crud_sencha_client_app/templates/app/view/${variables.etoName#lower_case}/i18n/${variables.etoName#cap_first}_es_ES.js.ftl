Ext.define('${variables.domain}.view.${variables.etoName?lower_case}.i18n.${variables.etoName?cap_first}_es_ES', {
    extend: 'Devon.I18nBundle',
    singleton: true,
    i18n: {
        ${variables.etoName?lower_case}s: {
            title: '${variables.etoName?cap_first}_ES',
            html: 'Lista de ${variables.etoName?lower_case}s_ES',
            grid: {
                id: 'ID',
                <#list pojo.fields as field>
                  <#if field?has_next>
                ${field.name?lower_case}: '${field.name?upper_case}_ES',
                  <#else>
                ${field.name?lower_case}: '${field.name?upper_case}_ES'
                  </#if>
                </#list>
            },
            buttons: {
                add: 'Añadir',
                edit: 'Editar',
                del: 'Borrar',
                refresh: 'Refrescar'
            }
        },

        ${variables.etoName?lower_case}Edit: {
            title: '${variables.etoName?cap_first}_ES: ',
            newTitle: 'Nuevo ${variables.etoName?lower_case}',
            add: 'Añadir',
            remove: 'Eliminar',
            submit: 'Enviar',
            cancel: 'Cancelar',
            html: 'Detalles de ${variables.etoName?lower_case}_ES #',
            grid: {
              title: 'Título',
              <#list pojo.fields as field>
                  <#if field?has_next>
              ${field.name?lower_case}: '${field.name?upper_case}_ES',
                  <#else>
              ${field.name?lower_case}: '${field.name?upper_case}_ES'
                  </#if>
              </#list>
            }
        },

        ${variables.etoName?lower_case}Crud: {
            title: 'Complete los datos',
            <#list pojo.fields as field>
            ${field.name?lower_case}: '${field.name?upper_case}_ES',
            </#list>
            submit: 'Guardar',
            cancel: 'Cancelar'
        }
    }
});