<#include '/functions.ftl'>
<#assign tableName = JavaUtil.getEntityTableName(pojo.canonicalName)>
CREATE TABLE ${tableName} (
<#assign fkList = []>
<#assign columns = []>
<#assign refTables = []>
<#list pojo.methodAccessibleFields  as field>
    <#if !field.annotations.javax_persistence_Transient??>
      <#if field.annotations.javax_persistence_Column?? && field.annotations.javax_persistence_Column.name?has_content>
        <#assign name = field.annotations.javax_persistence_Column.name>
      <#else>
        <#assign name = field.name>
      </#if>
      <#--Field: primary key-->
      <#if field.annotations.javax_persistence_Id??>
        <#assign pk = name>
        <#assign type = get_type(field)>
        <#if !type?contains("NOT NULL")>
             <#assign type = type + " NOT NULL">
        </#if>
        <#if field.annotations.javax_persistence_GeneratedValue??
             && field.annotations.javax_persistence_GeneratedValue.strategy??>
          <#assign type = type + " AUTO_INCREMENT">
        </#if>
        <#assign columns = columns + [{"name": name, "type":type}]>
      <#elseif !JavaUtil.isCollection2(classObject, field.name)>
        <#--Field: simple entity-->
        <#if field.type?ends_with("Entity")>
          <#if field.annotations.javax_persistence_JoinColumn?? && field.annotations.javax_persistence_JoinColumn.referencedColumnName?has_content>
            <#assign id = field.annotations.javax_persistence_JoinColumn.referencedColumnName>
            <#assign type = get_mapping_type(JavaUtil.getCanonicalNameOfField(field.canonicalType, id))>
          <#else>
            <#assign pkReceived = JavaUtil.getPrimaryKey(field.canonicalType)?split(",")>
            <#assign type = get_mapping_type(pkReceived[0])>
            <#assign id = pkReceived[1]>
          </#if>
          <#if field.annotations.javax_persistence_JoinColumn?? && field.annotations.javax_persistence_JoinColumn.name?has_content>
            <#assign name = field.annotations.javax_persistence_JoinColumn.name>
          <#else>
            <#assign name = name + "_" + id>
          </#if>
          <#if field.annotations.javax_persistence_JoinColumn??
          && (field.annotations.javax_persistence_ManyToOne?? || field.annotations.javax_persistence_OneToOne??)>
            <#assign tableReceived = JavaUtil.getEntityTableName(field.canonicalType)>
            <#if tableReceived?has_content>
              <#assign table = tableReceived>
            <#else>
              <#assign table = field.type>
            </#if>
            <#assign fkList = fkList + [{"key": name, "table": table, "id": id}]>
          </#if>
        <#else>
          <#--Field: primitive-->
          <#assign type = get_type(field)/>
        </#if>
        <#assign columns = columns + [{"name": name, "type":type}]>
      <#else>
      <#if field.annotations.javax_persistence_ManyToMany?? && field.annotations.javax_persistence_JoinTable??>
        <#--Field: collection of entity-->
        <#assign entity = field.canonicalType?substring(field.canonicalType?index_of("<") + 1,field.canonicalType?length - 1)>
        <#assign entityTable = JavaUtil.getEntityTableName(entity)>
        <#assign table1 = tableName>
        <#assign table2 = entityTable>
        <#if field.annotations.javax_persistence_JoinTable.name?has_content>
          <#assign refTableName = field.annotations.javax_persistence_JoinTable.name>
        <#else>
          <#assign refTableName = table1 + "_" + table2>
        </#if>
        <#--not yet support multiple JoinColumns or no JoinColumn-->
        <#if field.annotations.javax_persistence_JoinTable.joinColumns?has_content
             && field.annotations.javax_persistence_JoinTable.joinColumns?is_enumerable
             && field.annotations.javax_persistence_JoinTable.joinColumns[0]?has_content>
          <#assign col = field.annotations.javax_persistence_JoinTable.joinColumns[0].javax_persistence_JoinColumn>
          <#assign name1 = col.name>
          <#if col.referencedColumnName?has_content>
           <#assign id1 = col.referencedColumnName>
           <#assign type1 = get_mapping_type(JavaUtil.getCanonicalNameOfField(pojo.canonicalName, id1))>
          <#else>
           <#assign result = JavaUtil.getPrimaryKey(pojo.canonicalName)?split(",")>
           <#assign type1 = get_mapping_type(result[0])>
           <#assign id1 = result[1]>
          </#if>
        <#else>
          <#continue>
        </#if>
        <#if field.annotations.javax_persistence_JoinTable.inverseJoinColumns?has_content
             && field.annotations.javax_persistence_JoinTable.inverseJoinColumns?is_enumerable
             && field.annotations.javax_persistence_JoinTable.inverseJoinColumns[0]?has_content>
          <#assign col = field.annotations.javax_persistence_JoinTable.inverseJoinColumns[0].javax_persistence_JoinColumn>
          <#assign name2 = col.name>
          <#if col.referencedColumnName?has_content>
           <#assign id2 = col.referencedColumnName>
           <#assign type2 = get_mapping_type(JavaUtil.getCanonicalNameOfField(entity, id2))>
          <#else>
            <#assign result = JavaUtil.getPrimaryKey(entity)?split(",")>
            <#assign type2 = get_mapping_type(result[0])>
            <#assign id2 = result[1]>
          </#if>
        <#else>
          <#continue>
        </#if>
        <#assign refTables = refTables + [{"table": refTableName, "columns":[{"name": name1, "id": id1, "type": type1, "table": table1}, {"name": name2, "id": id2, "type": type2, "table": table2}] }]>
      </#if>
      </#if>
    </#if>
</#list>
<#list columns as col>
  ${col.name?right_pad(30)} ${col.type},
</#list>
  CONSTRAINT PK_${tableName} PRIMARY KEY(${pk}),
<#list fkList as fk>
  CONSTRAINT FK_${tableName}_${fk.key} FOREIGN KEY(${fk.key}) REFERENCES ${fk.table}(${fk.id}),
</#list>
);
<#list refTables as tb>

CREATE TABLE ${tb.table} (
<#assign col1 = tb.columns[0]>
<#assign col2 = tb.columns[1]>
 ${col1.name?right_pad(30)} ${col1.type} NOT NULL,
 ${col2.name?right_pad(30)} ${col2.type} NOT NULL,
 CONSTRAINT PK_${tb.table} PRIMARY KEY(${col1.name}, ${col2.name}),
 CONSTRAINT FK_${tb.table}_${col1.name} FOREIGN KEY(${col1.name}) REFERENCES ${col1.table}(${col1.id}),
 CONSTRAINT FK_${tb.table}_${col2.name} FOREIGN KEY(${col2.name}) REFERENCES ${col2.table}(${col2.id}),
);
</#list>
