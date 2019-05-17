package com.maven.project.general.logic.base;

/**
 * Abstract base class for any <em>use case</em> in this application. Actual implementations need to be annotated with
 * {@link javax.inject.Named} and {@link com.maven.project.general.logic.api.UseCase}.
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