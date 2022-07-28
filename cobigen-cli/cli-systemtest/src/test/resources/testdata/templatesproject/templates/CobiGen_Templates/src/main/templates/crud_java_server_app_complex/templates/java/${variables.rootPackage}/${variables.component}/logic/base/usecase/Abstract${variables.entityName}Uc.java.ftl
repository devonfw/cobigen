package ${variables.rootPackage}.${variables.component}.logic.base.usecase;

import ${variables.rootPackage}.general.logic.base.AbstractUc;
import ${variables.rootPackage}.${variables.component}.dataaccess.api.repo.${variables.entityName}Repository;

import javax.inject.Inject;

/**
 * Abstract use case for ${variables.entityName}s, which provides access to the commonly necessary data access objects.
 */
public abstract class Abstract${variables.entityName}Uc extends AbstractUc {

    /** @see #get${variables.entityName}Repository() */
    @Inject
    private ${variables.entityName}Repository ${variables.entityName?uncap_first}Repository;

    /**
     * @return the {@link ${variables.entityName}Repository} instance.
     */
    public ${variables.entityName}Repository get${variables.entityName}Repository() {

      return this.${variables.entityName?uncap_first}Repository;
    }

}
