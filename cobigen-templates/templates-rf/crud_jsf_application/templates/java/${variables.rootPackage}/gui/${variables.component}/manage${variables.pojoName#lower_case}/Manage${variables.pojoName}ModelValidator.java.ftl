package ${variables.rootPackage}.gui.${variables.component}.manage${pojo.name?lower_case};

import org.springframework.binding.validation.ValidationContext;

/**
 * Validator for validating the {@link Manage${pojo.name}Model}.
 */
public class Manage${pojo.name}ModelValidator {

    /**
     * Validation logic for a ${pojo.name}
     *
     * @param manage${pojo.name}Model
     *            Manage${pojo.name}Model The model for the current validated mask
     * @param context
     *            ValidationContext The current context
     */
    public void validateManage${pojo.name}ViewState(Manage${pojo.name}Model manage${pojo.name}Model, ValidationContext context) {
        // any custom validation of the manage${pojo.name}Model
    }

}
