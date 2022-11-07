${SQLUtil.debug(pojo)}
<#if pojo.annotations.javax_persistence_Table??>
    <#assign tableName = pojo.annotations.javax_persistence_Table.name>
<#else>
    <#assign tableName = SQLUtil.tableName(pojo.name)>
</#if>
<#assign statements = []>
<#assign joinTables = []>

CREATE TABLE ${tableName} (
<#list pojo.methodAccessibleFields as field>
    <#--    Skip Transient fields -->
    <#if field.annotations.javax_persistence_Transient??>
        <#continue>
    </#if>
    <#--    Primary Key statement -->
    <#if field.annotations.javax_persistence_Id??>
        <#assign statements += [SQLUtil.primaryKeyStatement(field)]>
    <#elseif field.annotations.javax_persistence_JoinColumn>
    </#if>
</#list>
<#list statements as statement>
    ${statement},
</#list>
);

<#list joinTables as tb>

</#list>