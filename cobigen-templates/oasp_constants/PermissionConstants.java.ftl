<#macro toUnderScore camelCase>
${camelCase?cap_first?replace("[A-Z]", "_$0", 'r')?upper_case?replace('_', '', 'f')}</#macro>
<#macro toComment camelCase>
${camelCase?cap_first?replace("[A-Z]", "_$0", 'r')?upper_case?replace('_', ' ')?lower_case}</#macro>
<#assign pathtoAccessControlPermission = variables.pathprefix+".module.security.common.api.accesscontrol.AccessControlPermission">
package ${variables.rootPackage}.${variables.component}.general.common.api.constants;
/**
* Contains constants for the keys of all
* {@link ${variables.rootPackage}.module.security.common.api.accesscontrol.AccessControlPermission}s.
*
*/
public abstract class PermissionConstants {

<#list .vars['access-control-schema'].Children as group>
<#if group._nodeName_ == "group">
<#list group.permissions.Children as permission>
/** {@link ${pathtoAccessControlPermission}} to<@toComment camelCase=permission._at_id/>. */
public static final String <@toUnderScore camelCase=permission._at_id/> = "${permission._at_id}";

</#list>
</#if>
</#list>
}