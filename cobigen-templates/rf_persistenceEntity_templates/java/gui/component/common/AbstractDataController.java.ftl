package ${variables.rootPackage}.gui.${variables.component}.common;

import ${variables.rootPackage}.gui.common.AbstractController;
import ${variables.rootPackage}.gui.${variables.component}.corewrapper.${pojo.name}ManagementCoreWrapper;

/**
 * @generated
 */
public abstract class Abstract${pojo.name}Controller extends AbstractController {

    protected ${pojo.name}ManagementCoreWrapper coreWrapper;

    /**
     * Sets the field 'coreWrapper'.
     * @param coreWrapper
     *            New value for coreWrapper
     */
    public void setCoreWrapper(${pojo.name}ManagementCoreWrapper coreWrapper) {
        this.coreWrapper = coreWrapper;
    }
}
