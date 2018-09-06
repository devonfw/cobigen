<#include '/makros.ftl'>
package ${variables.rootPackage}.common.builders<#if variables.subPackage != "null">.${variables.subPackage}</#if>;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;

import ${pojo.package}.${pojo.name};
import ${variables.rootPackage}.common.builders.P;

public class ${pojo.name}Builder {

   public ${pojo.name} persist(EntityManager em) {
        ${pojo.name} ${pojo.name?lower_case} = createNew();
        em.persist(${pojo.name?lower_case});
        return ${pojo.name?lower_case};
    }

    public List<${pojo.name}> persistAndDuplicate(EntityManager em, int quantity) {

        List<${pojo.name}> ${pojo.name?lower_case}List = new LinkedList<${pojo.name}>();
        for (int i = 0; i < quantity; i++) {
            ${pojo.name} ${pojo.name?lower_case} = createNew();
            // TODO alter at least values with unique key constraints to prevent from exceptions while persisting
            em.persist(${pojo.name?lower_case});
            ${pojo.name?lower_case}List.add(${pojo.name?lower_case});
        }

        return ${pojo.name?lower_case}List;
    }

}