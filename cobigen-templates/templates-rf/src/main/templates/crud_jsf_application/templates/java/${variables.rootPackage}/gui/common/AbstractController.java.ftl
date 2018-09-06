package ${variables.rootPackage}.gui.common;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

/**
 * Abstract class for all controllers.
 */
public abstract class AbstractController {

    protected Logger LOG = Logger.getLogger(this.getClass());

    /**
     * Displays a faces message 'msg' with severity 'severity'.
     *
     * @param severity
     *            Severity The severity of the message 'msg'.
     * @param msg
     *            String The message to display.
     */
    protected void displayFacesMessage(Severity severity, String msg) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(severity, msg, msg));
    }

}
