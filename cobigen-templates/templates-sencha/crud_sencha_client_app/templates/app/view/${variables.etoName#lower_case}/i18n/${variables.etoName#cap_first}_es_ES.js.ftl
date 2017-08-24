Ext.define('${variables.domain}.view.${variables.etoName?lower_case}.i18n.${variables.etoName?cap_first}_es_ES', {
    extend: 'Devon.I18nBundle',
    singleton: true,
    i18n: {
        ${variables.etoName?lower_case}s: {
            title: '${variables.etoName?cap_first}ES',
            html: 'Lista de ${variables.etoName?lower_case}sES',
            grid: {
                id: 'ID',
                state: 'ESTADO'
            },
            buttons: {
                add: 'Añadir',
                edit: 'Editar',
                del: 'Borrar',
                refresh: 'Refrescar'
            }
        },

        ${variables.etoName?lower_case}Edit: {
            title: '${variables.etoName?cap_first}ES: ',
            newTitle: 'Nueva mesa',
            status: 'ESTADO',
            add: 'Añadir',
            remove: 'Eliminar',
            submit: 'Enviar',
            cancel: 'Cancelar',
            html: 'Detalles de ${variables.etoName?lower_case}ES #',
            grid: {
                number: 'Número',
                title: 'Título',
                status: ' ESTADO'
            }
        },

        ${variables.etoName?lower_case}Crud: {
            title: 'Complete los datos',
            number: 'Número',
            state: 'Estado',
            submit: 'Guardar',
            cancel: 'Cancelar'
        }
    }
});