package ${variables.rootPackage}.core.common;

import org.apache.log4j.Logger;

import ${variables.rootPackage}.common.exception.ValidationException;

/**
 * Abstract Uc providing a logger instance and a basic validation logic.
 */
public abstract class AbstractUc {

    /**
     * Logger provided to Uc's.
     */
    protected final Logger LOG = Logger.getLogger(getClass());

    /**
     * This method provides basic validation logic. The following potential errors are handled:
     * <ul>
     * <li>entity == null</li>
     * </ul>
     *
     * @param entity
     *            The entity to validate
     * @param exceptionKey
     *            The exception message key.
     * @param exceptionMessage
     *            A specific error message.
     * @return {@link Boolean#TRUE}, if the validation is successful and no exception occurs.
     *         {@link Boolean#FALSE} otherwise.
     * @throws ValidationException
     *             This exception will be thrown on validation errors (see above, which are handled)
     */
    protected <T> Boolean validateProvidedEntity(T entity, String exceptionKey, String exceptionMessage)
        throws ValidationException {
        /*
         * Validation
         */
        if (entity == null) {
            throw new ValidationException(exceptionKey, exceptionMessage);
        }
        return true;
    }

}
