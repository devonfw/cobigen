<#include '/makros.ftl'>
package ${variables.rootPackage}.persistence.${variables.component}.dao.impl;

import java.util.List;

import javax.persistence.Query;

import ${variables.rootPackage}.common.constants.NamedQueries;
import ${variables.rootPackage}.persistence.common.AbstractDomainDao;
import ${variables.rootPackage}.persistence.${variables.component}.dao.${pojo.name}Dao;
import ${variables.rootPackage}.persistence.${variables.component}.entity.${pojo.name};

/**
 * This is the implementation of {@link ${pojo.name}Dao}.
 * @generated
 */
public class ${pojo.name}DaoImpl extends AbstractDomainDao<${pojo.name}, <@insertIdObjectType/>> implements ${pojo.name}Dao {

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(${pojo.name} ${pojo.name?uncap_first}) {

      ${pojo.name} persistent${pojo.name} = getEntityManager().merge(${pojo.name?uncap_first});
      super.save(persistent${pojo.name});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(${pojo.name} ${pojo.name?uncap_first}) {

      <@defineAndRetrieveAllIds/>
      ${pojo.name} persistent${pojo.name} = searchById(<@insertIdParameterValues/>);
      super.delete(persistent${pojo.name});
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<${pojo.name}> getAll${pojo.name}s() {

        Query query = getEntityManager().createNamedQuery(NamedQueries.GET_ALL_${pojo.name?upper_case}S, ${pojo.name}.class);

        List<${pojo.name}> persistence${pojo.name}s = query.getResultList();

        return persistence${pojo.name}s;
    }
}
