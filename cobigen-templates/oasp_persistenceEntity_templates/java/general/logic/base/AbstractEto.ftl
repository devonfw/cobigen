package ${variables.rootPackage}.general.logic.base;

import net.sf.mmm.util.transferobject.api.EntityTo;

/**
 * Abstract base class for an <em>{@link EntityTo entity transfer-object}</em> in this application.
 *
 * @author hohwille
 * @author erandres
 */
public class AbstractEto extends EntityTo<Long> {

  private static final long serialVersionUID = 1L;

  /**
   * The constructor.
   */
  public AbstractEto() {

    super();
  }

}
