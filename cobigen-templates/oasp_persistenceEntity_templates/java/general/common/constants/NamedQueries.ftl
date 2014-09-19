package ${variables.rootPackage}.general.common.constants;

/**
 * Constants for commonly used named queries
 */
public abstract class NamedQueries {

    public static final String GET_ALL_${variables.entityName?upper_case}S = "get.all.${variables.entityName?lower_case}s";

}
