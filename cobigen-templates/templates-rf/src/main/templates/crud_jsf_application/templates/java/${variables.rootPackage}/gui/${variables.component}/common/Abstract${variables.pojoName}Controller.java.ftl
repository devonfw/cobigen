package ${variables.rootPackage}.gui.${variables.component}.common;

import ${variables.rootPackage}.gui.common.AbstractController;
import ${variables.rootPackage}.gui.${variables.component}.corewrapper.${variables.component?cap_first}CoreWrapper;

/**
 * Abstract controller ${pojo.name} processing controllers.
 */
public abstract class Abstract${pojo.name}Controller extends AbstractController {

    protected ${variables.component?cap_first}CoreWrapper coreWrapper;

    /**
     * Sets the field 'coreWrapper'.
     * @param coreWrapper
     *            New value for coreWrapper
     */
    public void setCoreWrapper(${variables.component?cap_first}CoreWrapper coreWrapper) {
        this.coreWrapper = coreWrapper;
    }
}
