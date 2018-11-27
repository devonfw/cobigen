package ${variables.rootPackage}.core.${variables.component}.common;

/**
 * Exception keys of component ${variables.component?cap_first}.
 */
public class ${variables.component?cap_first}ExceptionKeys {

    /**
     * No such ${pojo.name} exception
     */
    public static final String NO_SUCH_${pojo.name?upper_case} = "${pojo.name?upper_case}00001";

    /**
     * ${pojo.name} already exists exception
     */
    public static final String ${pojo.name?upper_case}_ALREADY_EXISTS = "${pojo.name?upper_case}00002";

    /**
     * Negative ${pojo.name} id exception
     */
    public static final String ${pojo.name?upper_case}_ID_NOT_ALLOWED = "${pojo.name?upper_case}00003";

    /**
     * ${pojo.name} is in use exception
     */
    public static final String ${pojo.name?upper_case}_IN_USE = "${pojo.name?upper_case}00004";

    /**
     * ${pojo.name} already reserved exception
     */
    public static final String ${pojo.name?upper_case}_ALREADY_RESERVED = "${pojo.name?upper_case}00005";

    /**
     * ${pojo.name} already occupied exception
     */
    public static final String ${pojo.name?upper_case}_ALREADY_OCCUPIED = "${pojo.name?upper_case}00006";

    /**
     * ${pojo.name} state can't be change exception
     */
    public static final String ${pojo.name?upper_case}_STATE_CANNOT_BE_CHANGED = "${pojo.name?upper_case}00007";

}
