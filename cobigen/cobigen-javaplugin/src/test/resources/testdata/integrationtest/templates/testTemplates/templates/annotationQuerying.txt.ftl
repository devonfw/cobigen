<#list pojo.fields as field>
<#list field.annotations.org_junit_experimental_categories_Category.value as value><#compress>
${value},
</#compress></#list>
</#list>