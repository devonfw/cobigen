package ${variables.rootPackage}.${variables.component}.logic.base.usecase;

import ${variables.rootPackage}.general.logic.base.AbstractUc;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.dao.${variables.className}Dao;

import javax.inject.Inject;

/**
* Abstract use case for ${variables.className}s, which provides access to the commonly necessary data access objects.
*/
public class Abstract${variables.className}Uc extends AbstractUc {

        /** @see #get${variables.className}Dao() */
        @Inject
    private ${variables.className}Dao ${variables.className?uncap_first}Dao;

    /**
     * Returns the field '${variables.className?uncap_first}Dao'.
     * @return the {@link ${variables.className}Dao} instance.
     */
    public ${variables.className}Dao get${variables.className}Dao() {

      return this.${variables.className?uncap_first}Dao;
    }

}
