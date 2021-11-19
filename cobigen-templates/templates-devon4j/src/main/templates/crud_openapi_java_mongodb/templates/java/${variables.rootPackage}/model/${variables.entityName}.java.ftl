<#include '/makros.ftl'>
package ${variables.rootPackage}.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
//Al posto di avere un ENTITY su DBMS è una COLLECTION su NoSql
@Document(collection = "${variables.entityName?lower_case}s")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ${variables.entityName} {


  <#list model.properties as property>
  <#if property.name == "id">@${property.name?cap_first}<#else>@NotBlank(message="${property.description!}")</#if>
  <#if property.type == "date">
  @DateTimeFormat(pattern="dd-MM-yyyy")
  @NotBlank(message="${property.description!}")
  </#if>
    private <#if property.type == "date">LocalDate<#else>${property.type?cap_first}</#if> ${property.name};

  </#list>
}
