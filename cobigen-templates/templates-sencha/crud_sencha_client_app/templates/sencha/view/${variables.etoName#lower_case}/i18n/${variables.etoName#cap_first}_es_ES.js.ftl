Ext.define('${variables.domain}.view.${variables.etoName?lower_case}.i18n.${variables.etoName?cap_first}_en_EN', {
    extend: 'Devon.I18nBundle',
    singleton: true,
    i18n: {
        ${variables.etoName?uncap_first}: {
            title: '${variables.etoName?uncap_first}s',
            html: 'Lista de ${variables.etoName?uncap_first}s',
            grid: {
                <#list pojo.fields as field>
                  <#if !field.name?matches("Id")>
                    <#if field?has_next>
                      ${field.name}: '${field.name?upper_case}',
                    <#else>
                      ${field.name}: '${field.name?upper_case}'
                    </#if>
                  </#if>
                </#list>
            }
        }
    }
});