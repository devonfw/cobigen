package com.devonfw.cobigen.tsplugin.merger;

import javax.script.Invocable;
import javax.script.ScriptException;

/** Functional interface to specify Nashorn script executions. */
public @FunctionalInterface interface ScriptExecutable {

    /**
     * Run the script execution.
     * @param invocable
     *            Engine to run the script with.
     * @return the object returned by the script based on Nashorn specifications.
     * @throws NoSuchMethodException
     *             if the method called could not be found in the script
     * @throws ScriptException
     *             if the execution of the script was erroneous
     */
    Object exec(Invocable invocable) throws NoSuchMethodException, ScriptException;

}
