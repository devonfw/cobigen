<#macro callNotNullPropertyWithDefaultValue attr>
<#if attr.canonicalType = "byte" || attr.canonicalType = "java.lang.Byte" >
	${attr.name}((byte)1);
<#elseif attr.canonicalType  = "short" || attr.canonicalType = "java.lang.Short" >
	${attr.name}((short)1);
<#elseif attr.canonicalType  = "int" || attr.canonicalType = "java.lang.Integer" >
	${attr.name}(1);
<#elseif attr.canonicalType  = "long" || attr.canonicalType = "java.lang.Long" >
	${attr.name}(1L);
<#elseif attr.canonicalType  = "float" || attr.canonicalType = "java.lang.Float" >
	${attr.name}(1f);
<#elseif attr.canonicalType  = "double" || attr.canonicalType = "java.lang.Double" >
	${attr.name}(1d);
<#elseif attr.canonicalType  = "char" || attr.canonicalType = "java.lang.Character" >
	${attr.name}('c');
<#elseif attr.canonicalType  = "boolean" || attr.canonicalType = "java.lang.Boolean" >
	${attr.name}(true);
<#elseif attr.canonicalType   = "java.lang.String">
	${attr.name}("DefaultString");
<#elseif attr.canonicalType   = "java.lang.Number">
	${attr.name}(1);
<#elseif attr.canonicalType   = "java.util.Currency">
  ${attr.name}(Currency.getInstance("USD"));
<#elseif attr.canonicalType   = "java.math.BigDecimal">
  ${attr.name}(BigDecimal.valueOf(1.0));
<#elseif attr.canonicalType   = "java.time.LocalTime">
  ${attr.name}(LocalTime.now());
<#elseif attr.canonicalType   = "java.time.LocalDate">
  ${attr.name}(LocalDate.now());
<#elseif attr.canonicalType   = "java.time.LocalDateTime">
  ${attr.name}(LocalDateTime.now());
<#elseif attr.canonicalType   = "java.time.ZonedDateTime">
  ${attr.name}(ZonedDateTime.now());
<#elseif JavaUtil.isEnum(attr.canonicalType)>
  ${attr.name}(${attr.type}.${JavaUtil.getFirstEnumValue(attr.canonicalType)});
<#elseif attr.canonicalType?ends_with("[]")>
  ${attr.name}(new ${attr.type?keep_before_last("[]")}[0]);
<#elseif getPackage(attr.canonicalType) = pojo.package>
	${attr.name}(new ${attr.name?cap_first}Builder().createNew());
<#else>
	//TODO ${attr.name}(...); //set Default ${attr.canonicalType}
</#if>
</#macro>

<#function getPackage fqn>
	<#assign lastIndexOfDot=fqn?last_index_of(".")+1 />
	<#assign package = fqn?substring(0, lastIndexOfDot) />
	<#if package = "">
		<#-- Heuristic, which might cause faulty generation results in some not often occuring cases -->
		<#assign package = pojo.package>
	</#if>
	<#return package>
</#function>
