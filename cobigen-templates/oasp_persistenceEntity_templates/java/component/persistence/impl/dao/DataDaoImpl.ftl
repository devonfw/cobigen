package ${variables.rootPackage}.${variables.component}.persistence.impl.dao;

import java.util.List;

import javax.persistence.Query;

import ${variables.rootPackage}.general.common.constants.NamedQueries;
import ${variables.rootPackage}.general.persistence.base.dao.AbstractDaoImpl;
import ${variables.rootPackage}.${variables.component}.persistence.api.dao.${variables.entityName}Dao;
import ${variables.rootPackage}.${variables.component}.persistence.api.${pojo.name};

/**
 * This is the implementation of {@link ${variables.entityName}Dao}.
 */
public class ${variables.entityName}DaoImpl extends AbstractDaoImpl<Long, ${pojo.name}> implements ${variables.entityName}Dao {

    /**
      * {@inheritDoc}
      */
     @Override
     public Class<? extends ${pojo.name}> getEntityClass() {
       return ${pojo.name}.class;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<${pojo.name}> getAll${variables.entityName}s() {

        Query query = getEntityManager().createNamedQuery(NamedQueries.GET_ALL_${variables.entityName?upper_case}S, ${pojo.name}.class);

        List<${pojo.name}> persistence${variables.entityName}s = query.getResultList();

        return persistence${variables.entityName}s;
    }
    
}
