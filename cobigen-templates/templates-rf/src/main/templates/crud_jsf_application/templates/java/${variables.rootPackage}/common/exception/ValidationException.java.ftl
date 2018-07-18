package ${variables.rootPackage}.common.exception;

/**
 * This exception will be thrown on validation errors.
 * @generated
 */
public class ValidationException extends TechnicalRuntimeException {

    /**
     * Constructor.
     *
     * @param exceptionId
     *            {@link String} The exception Key defined by the ExceptionKey-Class
     * @param parameter
     *            String... Some specific parameters. The parameters could be accessed via '{0}', '{1}', ...
     *            markers within the exception error description properties file
     */
    public ValidationException(String exceptionId, String... parameter) {
        super(exceptionId, parameter);
    }

}
