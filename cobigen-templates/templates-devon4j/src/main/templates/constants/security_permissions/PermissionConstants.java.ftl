<#macro toUnderScore camelCase>
${camelCase?cap_first?replace("[A-Z]", "_$0", 'r')?upper_case?replace('_', '', 'f')}</#macro>
<#macro toComment camelCase>
${camelCase?cap_first?replace("[A-Z]", "_$0", 'r')?upper_case?replace('_', ' ')?lower_case}</#macro>

package ${variables.rootPackage}.general.common.api.constants;

import com.devonfw.module.security.common.api.accesscontrol.AccessControlPermission;

/**
* Contains constants for the keys of all
* {@link AccessControlPermission Permission}s.
*/
public abstract class PermissionConstants {

<#list .vars['access-control-schema'].Children as group>
<#if group._nodeName_ == "group">
<#list group.permissions.Children as permission>
/** {@link AccessControlPermission Permission} to<@toComment camelCase=permission._at_id/>. */
public static final String <@toUnderScore camelCase=permission._at_id/> = "${permission._at_id}";

</#list>
</#if>
</#list>
}