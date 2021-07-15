<#function get_type field>
  <#assign type =  get_mapping_type(field.canonicalType)>
  <#if type?contains("VARCHAR") && field.annotations.javax_validation_constraints_Size?? && field.annotations.javax_validation_constraints_Size.max??>
     <#assign type = "VARCHAR(" + field.annotations.javax_validation_constraints_Size.max +")">
  </#if>
  <#if (field.annotations.javax_persistence_Column??
               && field.annotations.javax_persistence_Column.nullable??
               && field.annotations.javax_persistence_Column.nullable=="false") || field.annotations.javax_validation_constraints_NotNull?? >
        <#assign type = type + " NOT NULL">
  </#if>
  <#return type>
</#function>

<#function get_mapping_type input_type>
  <#if input_type?contains("Integer") || input_type=="int" || input_type?contains("Year") || input_type?contains("Month") || JavaUtil.isEnum(input_type)>
    <#assign type = "INTEGER">
  <#elseif input_type?contains("Long") || input_type=="long" || input_type?contains("Object")>
     <#assign type = "BIGINT">
  <#elseif input_type?contains("Short") || input_type=="short">
     <#assign type = "SMALLINT">
  <#elseif input_type?contains("Float") || input_type=="float">
     <#assign type = "FLOAT">
  <#elseif input_type?contains("Double") || input_type=="double">
     <#assign type = "DOUBLE">
  <#elseif input_type?contains("BigDecimal") || input_type?contains("BigInteger")>
     <#assign type = "NUMERIC">
  <#elseif input_type?contains("Character") || input_type=="char">
     <#assign type = "CHAR">
  <#elseif input_type?contains("Byte") || input_type=='byte'>
     <#assign type = "TINYINT">
  <#elseif input_type?contains("Boolean") || input_type=="boolean">
     <#assign type = "BOOLEAN">
  <#elseif input_type?contains("Instant") || input_type?contains("Timestamp")>
     <#assign type = "TIMESTAMP">
  <#elseif input_type?contains("Date") || input_type?contains("Calendar")>
     <#assign type = "DATE">
  <#elseif input_type?contains("Time")>
     <#assign type = 'TIME'>
  <#elseif input_type?contains("UUID")>
     <#assign type = "BINARY">
  <#elseif input_type?contains("Blob")>
     <#assign type = "BLOB">
  <#else>
     <#assign type = "VARCHAR">
  </#if>
  <#return type>
</#function>
