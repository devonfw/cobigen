<#include '/makros.ftl'>
package ${variables.rootPackage}.common.builders;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.EntityManager;

import ${pojo.package}.${pojo.name};

/**
 * Test data builder for ${pojo.name} generated with cobigen.
 */
public class ${pojo.name}Builder {

  /**
   * @param em the {@link EntityManager}
   * @return the ${pojo.name}
   */
   public ${pojo.name} persist(EntityManager em) {
        ${pojo.name} ${pojo.name?lower_case} = createNew();
        em.persist(${pojo.name?lower_case});
        return ${pojo.name?lower_case};
    }
    
  /**
   * @param em the {@link EntityManager}
   * @param quantity the quantity
   * @return a list of ${pojo.name}
   */
    public List<${pojo.name}> persistAndDuplicate(EntityManager em, int quantity) {

        List<${pojo.name}> ${pojo.name?lower_case}List = new LinkedList<>();
        for (int i = 0; i < quantity; i++) {
            ${pojo.name} ${pojo.name?lower_case} = createNew();
            // TODO alter at least values with unique key constraints to prevent from exceptions while persisting
            em.persist(${pojo.name?lower_case});
            ${pojo.name?lower_case}List.add(${pojo.name?lower_case});
        }

        return ${pojo.name?lower_case}List;
    }

}