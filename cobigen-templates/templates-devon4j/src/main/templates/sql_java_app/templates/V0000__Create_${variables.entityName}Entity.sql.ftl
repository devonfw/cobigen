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
    <#--    OneToOne statement -->
    <#elseif field.annotations.javax_persistence_OneToOne>
        <#--        Key mapped on other table, skip -->
        <#if field.annotations.javax_persistence_OneToOne.mappedBy != "">
            <#continue>
        </#if>
        <#assign statements += [SQLUtil.foreignKeyStatement(field)]>
    <#--    OneToMany Foreign Keystatement -->
    <#elseif field.annotations.javax_persistence_OneToMany>
        <#assign statements += [SQLUtil.foreignKeyStatement(field)]>
    <#else>
        <#assign statements += [SQLUtil.basicTypeStatement(field)]>
    </#if>
</#list>
<#list statements as statement>
    ${statement},
</#list>
);

<#list joinTables as tb>

</#list>