package ${variables.rootPackage}.common.exception;

import de.bund.bva.pliscommon.exception.FehlertextProvider;
import de.bund.bva.pliscommon.exception.PlisTechnicalRuntimeException;
import de.bund.bva.pliscommon.util.exception.MessageSourceFehlertextProvider;

/**
 * Abstract technical <i>unchecked</i> main exception.
 * @generated
 */
public abstract class TechnicalRuntimeException extends PlisTechnicalRuntimeException {

    public static FehlertextProvider ERROR_MESSAGE_PROVIDER = new MessageSourceFehlertextProvider();

    /**
     * @param exceptionId
     *            String The exception Key defined by the {@link ExceptionKeys}-Class
     */
    protected TechnicalRuntimeException(String exceptionId) {
        super(exceptionId, ERROR_MESSAGE_PROVIDER);
    }

    /**
     * @param exceptionId
     *            String The exception Key defined by the {@link ExceptionKeys}-Class
     * @param parameter
     *            String [] Some specific parameters. The parameters could be accessed via '{0}', '{1}', ...
     *            markers within the exception error description properties file
     */
    protected TechnicalRuntimeException(String exceptionId, String[] parameter) {
        super(exceptionId, ERROR_MESSAGE_PROVIDER, parameter);
    }

    /**
     * @param exceptionId
     *            String The exception Key defined by the {@link ExceptionKeys}-Class
     * @param throwable
     *            Throwable The thrown exception
     * @param parameter
     *            String [] Some specific parameters. The parameters could be accessed via '{0}', '{1}', ...
     *            markers within the exception error description properties file
     */
    protected TechnicalRuntimeException(String exceptionId, Throwable throwable, String[] parameter) {
        super(exceptionId, throwable, ERROR_MESSAGE_PROVIDER, parameter);
    }
}
