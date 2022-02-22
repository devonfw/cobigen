<#macro toCamelCase points>
${points?replace(".a","A")?replace(".b","B")?replace(".c","C")?replace(".d","D")?replace(".e","E")?replace(".f","F")?replace(".g","G")?replace(".h","H")?replace(".i","I")?replace(".j","J")?replace(".k","K")?replace(".l","L")?replace(".m","M")?replace(".n","N")?replace(".o","O")?replace(".p","P")?replace(".q","Q")?replace(".r","R")?replace(".s","S")?replace(".t","T")?replace(".u","U")?replace(".v","V")?replace(".w","W")?replace(".x","X")?replace(".y","Y")?replace(".z","Z")}</#macro>
<#macro toUnderScore points>
${points?upper_case?replace('.', '_')}</#macro>
package ${variables.rootPackage}.general.common.api.constants;

/**
 * Constants of the named queries defined in <code>NamedQueries.xml</code>.
 */
public abstract class NamedQueries {

<#list .vars['entity-mappings'].Children as namedQuery>
<#if namedQuery._nodeName_ == "named-query">
public static final String <@toUnderScore points=namedQuery._at_name/> = "${namedQuery._at_name}";

</#if>
</#list>
}
