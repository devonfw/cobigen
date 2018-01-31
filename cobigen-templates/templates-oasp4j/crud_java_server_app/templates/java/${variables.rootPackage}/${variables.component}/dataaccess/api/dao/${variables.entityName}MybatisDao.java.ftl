package ${variables.rootPackage}.${variables.component}.dataaccess.api.dao;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.devonfw.module.mybatis.dataaccess.AbstractGenericMybatisDao;
import ${variables.rootPackage}.general.mapper.GenericMybatisMapper;
import ${variables.rootPackage}.${variables.component}.mapper.${variables.entityName}Mapper;

/**
 * Data access interface for ${variables.entityName} 
 */
@Service
public class ${variables.entityName}MybatisDao<T, PK> extends AbstractGenericMybatisDao<T, PK> {

  @Autowired
  private ${variables.entityName}Mapper ${variables.entityName?uncap_first}Mapper;

  /**
   * The constructor.
   *
   * @param myBatisMapper
   */
  @Autowired
  public ${variables.entityName}MybatisDao(${variables.entityName}Mapper myBatisMapper) {
    super(myBatisMapper);
  }
  
  /**
   * fetch all the objects from table
   *
   * @return List of objects
   */
  public List selectAll() {

    return (List) this.${variables.entityName?uncap_first}Mapper.selectAll();
  }
  

}
