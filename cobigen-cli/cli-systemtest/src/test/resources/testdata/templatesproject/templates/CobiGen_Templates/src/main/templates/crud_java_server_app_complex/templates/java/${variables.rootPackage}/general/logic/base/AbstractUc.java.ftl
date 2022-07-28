package ${variables.rootPackage}.general.logic.base;

/**
 * Abstract base class for any <em>use case</em> in this application. Actual implementations need to be annotated with
 * {@link javax.inject.Named} and {@link ${variables.rootPackage}.general.logic.api.UseCase}.
 *
 */ 
public class AbstractUc extends AbstractLogic  {

  /**
  * The constructor.
  */
  public AbstractUc() {

    super();
  }

}