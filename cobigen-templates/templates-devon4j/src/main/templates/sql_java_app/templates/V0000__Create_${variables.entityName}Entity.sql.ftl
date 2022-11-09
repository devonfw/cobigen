<#if pojo.annotations.javax_persistence_Table??>
    <#assign tableName = pojo.annotations.javax_persistence_Table.name>
<#else>
    <#assign tableName = SQLUtil.tableName(pojo.name)>
</#if>
<#assign statements = []>
<#assign joinTables = []>
<#list pojo.methodAccessibleFields as field>
<#--    Skip Transient fields -->
    <#if field.annotations.javax_persistence_Transient??>
        <#continue>
    <#--    Primary Key statement -->
    <#elseif field.annotations.javax_persistence_Id??>
        <#assign statements += [SQLUtil.primaryKeyStatement(field)]>
    <#--    OneToOne statement -->
    <#elseif field.annotations.javax_persistence_OneToOne??>
    <#--        Key mapped on other table, skip -->
        <#if field.annotations.javax_persistence_OneToOne.mappedBy != "">
            <#continue>
        </#if>
        <#assign statements += [SQLUtil.foreignKeyStatement(field)]>
    <#--      Skip OneToMany as it's just a Foreign Key on a different table  -->
    <#elseif field.annotations.javax_persistence_OneToMany??>
        <#continue>
    <#--    ManyToOne: create Foreign Keystatement from the field -->
    <#elseif field.annotations.javax_persistence_ManyToOne??>
        <#assign statements += [SQLUtil.foreignKeyStatement(field)]>
    <#--  TODO: Check if there is a JoinTable specified that should be created.      -->
    <#elseif field.annotations.javax_persistence_ManyToMany?? && field.annotations.javax_persistence_JoinTable??>
        <#assign joinTableAnnotation = field.annotations.javax_persistence_JoinTable>

        <#--       Parse joinColumns to generate Foreign Keys -->
        <#assign joinColumns = joinTableAnnotation.joinColumns>
        <#assign inverseJoinColumns = joinTableAnnotation.inverseJoinColumns>
        <#--  Statement collector list      -->
        <#assign statements = [] >
        <#assign defaultFieldTable = SQLUtil.tableName(field.type)>
        <#list joinColumns as jcol>
            <#assign jcolAnnotation = jcol.javax_persistence_JoinColumn>
            ${SQLUtil.debug(defaultFieldTable)}
            <#assign statements += [SQLUtil.parseJoinColumn(jcolAnnotation, defaultFieldTable)]>
        </#list>

        <#-- When parsing inverse join columns pass the tableName as a default for the reference to this parsed Entity  -->
        <#list inverseJoinColumns as jcol>
            <#assign jcolAnnotation = jcol.javax_persistence_JoinColumn>
            <#assign statements += [SQLUtil.parseJoinColumn(jcolAnnotation, tableName)]>
        </#list>
        <#--       Build joinTable with parsed data -->
        <#assign joinTable = {}>
        <#assign joinTable += { "statements": statements }>
        <#assign joinTable += { "name": joinTableAnnotation.name }>
        <#-- Append result to collector list -->
        <#assign joinTables += [joinTable]>

    <#-- Try generating simple SQL statement from field -->
    <#else>
        <#assign statements += [SQLUtil.basicStatement(field)]>
    </#if>
</#list>
CREATE TABLE ${tableName} (
<#list statements as statement>
    ${statement},
</#list>
);
<#-- TODO: parse generated JoinTables -->
<#list joinTables as tb>
CREATE TABLE ${tb.name} (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
<#list tb.statements as statement>
    ${statement},
</#list>
);
</#list>