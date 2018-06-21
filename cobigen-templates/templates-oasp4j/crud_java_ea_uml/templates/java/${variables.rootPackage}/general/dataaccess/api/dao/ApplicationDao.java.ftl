<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#compress>
<#assign name = elemDoc["self::node()/@name"]>
<#assign connectors = doc["xmi:XMI/xmi:Extension/connectors/connector"]>
package ${variables.rootPackage}.general.dataaccess.api.dao;

import io.oasp.module.jpa.dataaccess.api.Dao;

import net.sf.mmm.util.entity.api.PersistenceEntity;


/**
 * Interface for all {@link Dao DAOs} (Data Access Object) of this application.
 *
 * @param <ENTITY> is the type of the managed entity.
 */
public interface ApplicationDao<ENTITY extends PersistenceEntity<Long>> extends Dao<ENTITY> {

}
</#compress>