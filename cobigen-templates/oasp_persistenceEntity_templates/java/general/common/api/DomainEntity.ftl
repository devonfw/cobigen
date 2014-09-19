package ${variables.rootPackage}.general.common.api;

import net.sf.mmm.util.entity.api.MutableGenericEntity;

/**
 * This is the abstract interface for a {@link MutableGenericEntity} of the restaurant. We are using {@link Long} for
 * all {@link #getId() primary keys}.
 *
 * @author hohwille
 */
public abstract interface ${variables.domain?cap_first}Entity extends MutableGenericEntity<Long> {

}
