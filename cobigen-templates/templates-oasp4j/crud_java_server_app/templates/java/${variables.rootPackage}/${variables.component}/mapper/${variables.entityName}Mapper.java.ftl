package ${variables.rootPackage}.${variables.component}.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;
import org.springframework.context.annotation.Primary;

import ${variables.rootPackage}.${variables.component}.dataaccess.api.${pojo.name};
import com.capgemini.devonfw.module.mybatis.common.SearchCriteria;
import com.capgemini.devonfw.module.mybatis.mapper.GenericMybatisMapper;

/**
 * Mybatis Mapper Interface for ${variables.component?cap_first} component.
 */
public interface ${variables.entityName}Mapper extends GenericMybatisMapper<${pojo.name}, Long> {
   /**
   * Saves a ${variables.entityName?uncap_first} and store it in the database.
   *
   * @param ${pojo.name?uncap_first} {@link ${pojo.name}}.
  */
  @Options(useGeneratedKeys = true)
  @Insert("Insert into ${variables.entityName}(id, modificationCounter, <#list pojo.fields as field>${field.name} <#sep>,</#list>) values (${r"#{"}id} ,${r"#{"}modificationCounter} ,<#list pojo.fields as field>${r"#{"}${field.name}} <#sep>,</#list>)")
  void insert(${pojo.name} ${pojo.name?uncap_first});

/**
   * Returns a list of ${variables.entityName}s;.
   *
   * @return the list of {@link ${pojo.name}}
  */
  @Select("select * from ${variables.entityName}")
  Collection<${pojo.name}> selectAll();
  
  /**
   * Deletes a ${variables.entityName?uncap_first} from the database by its id .
   *
   * @param id The id 'id' of the ${variables.entityName}.
  */
  @Delete("delete from ${variables.entityName} where id = ${r"#{"}id}")
  void delete(long id);
  
  /**
   * Returns a paginated list of ${variables.entityName}s matching the search criteria.
   *
   * @param criteria the {@link ${variables.entityName}SearchCriteria}.
   * @return the list of {@link ${pojo.name}}
   */
  List<${pojo.name}> fetch(@Param("searchCriteria") SearchCriteria searchCriteria, RowBounds rowBounds);
  
   /**
   * Returns a ${variables.entityName} by its id 'id'.
   *
   * @param id The id 'id' of the ${variables.entityName}.
   * @return The {@link ${pojo.name}} with id 'id'
   */
  @Select("select * from ${variables.entityName} where id = ${r"#{"}id}")
  ${pojo.name} fetchById(long id);
  

}