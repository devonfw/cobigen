<#-- Copyright © Capgemini 2013. All rights reserved. -->
package ${variables.rootPackage}.core.${variables.component}.common;

/**
 * @generated
 */
public class ExceptionKeys extends ${variables.rootPackage}.common.constants.ExceptionKeys {

    /* No such table exception */
    public static final String NO_SUCH_TABLE = "${variables.component?upper_case}00001";

    /* table already exists exception */
    public static final String TABLE_ALREADY_EXISTS = "${variables.component?upper_case}00002";

    /* Negative table id exception */
    public static final String TABLE_ID_NOT_ALLOWED = "${variables.component?upper_case}00003";

    /* Table is in use exception */
    public static final String TABLE_IN_USE = "${variables.component?upper_case}00004";

    /* Table already reserved exception */
    public static final String TABLE_ALREADY_RESERVED = "${variables.component?upper_case}00005";

    /* Table already occupied exception */
    public static final String TABLE_ALREADY_OCCUPIED = "${variables.component?upper_case}00006";

    /* Table state can't be change exception */
    public static final String TABLE_STATE_CANNOT_BE_CHANGED = "${variables.component?upper_case}00007";

}
