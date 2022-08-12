CREATE TABLE ${variables.entityName?upper_case} (
  ID                            DECIMAL(10,0),
  VERSION                       INTEGER NOT NULL,
<#list pojo.fields as field>
<#if !field.type?starts_with("List<") && !field.type?starts_with("Set<")>
  <#if field.type?contains("Entity") || field.type=='long' || field.type=='java.lang.Long'>
    <#assign type = 'DECIMAL(10,0)'>
  <#elseif field.type=='int' || field.type=='java.lang.Integer' || field.type=='java.time.Year' || field.type=='java.time.Month'>
    <#assign type = 'INTEGER'>
  <#elseif field.type=='java.time.Instant' || field.type=='java.sql.Timestamp'>
    <#assign type = 'TIMESTAMP'>
  <#elseif field.type=='java.util.Date'>
    <#assign type = 'DATE'>
  <#elseif field.type=='boolean'>
    <#assign type = 'NUMBER(1) DEFAULT 0'>
  <#else>
    <#assign type = 'VARCHAR2(255 CHAR)'>
  </#if>
  ${field.name?upper_case?right_pad(30)}${type},
</#if>
</#list>
  CONSTRAINT PK_${variables.entityName?upper_case} PRIMARY KEY(ID)
)