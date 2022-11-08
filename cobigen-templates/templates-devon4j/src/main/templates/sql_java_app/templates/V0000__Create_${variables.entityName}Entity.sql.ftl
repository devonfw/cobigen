${SQLUtil.debug(pojo)}
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
        <#assign statements = statements + [SQLUtil.primaryKeyStatement(field)]>
    <#--    OneToOne statement -->
    <#elseif field.annotations.javax_persistence_OneToOne??>
    <#--        Key mapped on other table, skip -->
        <#if field.annotations.javax_persistence_OneToOne.mappedBy != "">
            <#continue>
        </#if>
        <#assign statements = statements + [SQLUtil.foreignKeyStatement(field)]>
    <#--    OneToMany Foreign Keystatement -->
    <#elseif field.annotations.javax_persistence_OneToMany??>
        <#assign statements = statements + [SQLUtil.foreignKeyStatement(field)]>
    <#elseif field.annotations.javax_persistence_ManyToOne??>
        <#--      Skip ManyToOne as it's just a Foreign Key on a different table  -->
        <#continue>
    <#elseif field.annotations.javax_persistence_ManyToMany??>
        <#--  Check if there is a JoinTable specified....      -->
        <#continue>
    <#else>
        <#assign statements += [SQLUtil.basicStatement(field)]>
    </#if>
</#list>
CREATE TABLE ${tableName} (
<#list statements as statement>
    ${statement},
</#list>
);
<#-- TODO: parse generated JoinTables (NOT SURE IF NECESSARY!
  AS TICKET MAYBE IMPLIES 1 SQL FILE FOR EACH ENTITY.
-->
<#list joinTables as tb>
</#list>