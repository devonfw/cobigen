package x_rootpackage_x.general.common.x_scope_x.security;

import com.devonfw.cobigen.api.annotation.CobiGenProperties;
import com.devonfw.cobigen.api.annotation.CobiGenProperty;
import com.devonfw.cobigen.api.annotation.CobiGenTemplate;
import com.devonfw.cobigen.api.template.CobiGenJavaIncrements;
import com.devonfw.cobigen.api.template.CobiGenJavaProperties;

/**
 * Constants for permissions of this application.
 */
@CobiGenTemplate(value = CobiGenJavaIncrements.LOGIC, constant = true)
@CobiGenProperties({
@CobiGenProperty(key = CobiGenJavaProperties.KEY_SCOPE, value = CobiGenJavaProperties.VALUE_SCOPE_API),
@CobiGenProperty(key = CobiGenJavaProperties.KEY_MODULE, value = CobiGenJavaProperties.VALUE_MODULE_CORE) })
public class ApplicationAccessControlConfig {

  /**
   * The namespace prefix build from {@link #APP_ID} and prepended to every permission to avoid name-clashing of
   * permissions with other applications within the same application landscape in identity & access management (IAM).
   */
  private static final String PREFIX = "appId.";

  /** Permission to for {@link x_rootpackage_x.x_component_x.logic.x_scope_x.x_detail_x.UcFindX_EntityName_X}. */
  public static final String PERMISSION_FIND_X_ENTITY_NAME_X = PREFIX + "FindX_EntityName_X";

  /** Permission to for {@link x_rootpackage_x.x_component_x.logic.x_scope_x.x_detail_x.UcSaveX_EntityName_X}. */
  public static final String PERMISSION_SAVE_X_ENTITY_NAME_X = PREFIX + "SaveX_EntityName_X";

  /** Permission to for {@link x_rootpackage_x.x_component_x.logic.x_scope_x.x_detail_x.UcDeleteX_EntityName_X}. */
  public static final String PERMISSION_DELETE_X_ENTITY_NAME_X = PREFIX + "DeleteX_EntityName_X";

}
