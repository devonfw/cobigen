/*
 * Copyright © Capgemini 2013. All rights reserved.
 */
package com.capgemini.cobigen.extension;

/**
 * This interface should be inherited by implementations to cover the interpretation of new trigger types.
 * Therefore you have to provide exactly one {@link IInputReader} and one {@link IMatcher}.
 * @author mbrunnli (08.04.2014)
 */
public interface ITriggerInterpreter {

    /**
     * This function should return the type name, which could be declared in the type parameter of the XML
     * &lt;trigger&gt; element and therefore invoke this interpreter
     * @return the type name (not null)
     * @author mbrunnli (08.04.2014)
     */
    public String getType();

    /**
     * This function should return the {@link IInputReader} for reading the intended input format for this
     * trigger interpreter
     * @return the {@link IInputReader} (not null)
     * @author mbrunnli (08.04.2014)
     */
    public IInputReader getInputReader();

    /**
     * This function should return the {@link IMatcher} for matching a given input as a valid input to be
     * processed and resolving the values of all variable assignments
     * @return the {@link IMatcher} (not null)
     * @author mbrunnli (08.04.2014)
     */
    public IMatcher getMatcher();
}
