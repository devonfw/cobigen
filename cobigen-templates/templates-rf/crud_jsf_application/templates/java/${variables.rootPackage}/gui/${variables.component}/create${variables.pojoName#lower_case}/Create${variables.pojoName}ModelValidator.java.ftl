package ${variables.rootPackage}.gui.${variables.component}.create${pojo.name?lower_case};

import org.springframework.binding.validation.ValidationContext;

/**
 * Validator for validating the {@link Create${pojo.name}Model}.
 */
public class Create${pojo.name}ModelValidator {

    /**
     * Validation logic for a ${pojo.name}
     *
     * @param create${pojo.name}Model
     *            Create${pojo.name}Model The model for the current validated mask
     * @param context
     *            ValidationContext The current context
     */
    public void validateCreate${pojo.name}ViewState(Create${pojo.name}Model create${pojo.name}Model, ValidationContext context) {
        // any custom validation of the create${pojo.name}Model
    }

}
