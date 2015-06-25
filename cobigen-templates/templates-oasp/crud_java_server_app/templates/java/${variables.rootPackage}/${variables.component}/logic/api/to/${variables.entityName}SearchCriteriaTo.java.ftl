package ${variables.rootPackage}.${variables.component}.logic.api.to;

import io.oasp.module.jpa.common.api.to.SearchCriteriaTo;

/**
 * This is the {@link SearchCriteriaTo search criteria} {@link net.sf.mmm.util.transferobject.api.TransferObject TO}
 * used to find {@link ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName}}s.
 *
 */
public class ${variables.entityName}SearchCriteriaTo extends SearchCriteriaTo {


  private static final long serialVersionUID = 1L;

  <#list pojo.fields as attr>
    private ${attr.type?replace("[^<>,]+Entity","Long","r")} ${attr.name};
  </#list>

  /**
   * The constructor.
   */
  public ${variables.entityName}SearchCriteriaTo() {

    super();
  }

  <#list pojo.fields as attr>
  <#compress>
  <#assign newAttrType=attr.type?replace("[^<>,]+Entity","Long","r")>
  <#assign attrCapName=attr.name?cap_first>
  <#assign suffix="">
  <#if attr.type?contains("Entity") && (attr.canonicalType?contains("java.util.List") || attr.canonicalType?contains("java.util.Set"))>
     <#assign suffix="Ids">
     <#-- Handle the standard case. Due to no knowledge of the interface, we have no other possibility than guessing -->
     <#-- Therefore remove (hopefully) plural 's' from attribute's name to attach it on the suffix -->
     <#if attrCapName?ends_with("s")>
       <#assign attrCapName=attrCapName?substring(0, attrCapName?length-1)>
     </#if>
  <#elseif attr.type?contains("Entity")>
     <#assign suffix="Id">
  </#if>
  </#compress>

  public ${newAttrType} <#if attr.type=='boolean'>is${attrCapName}<#else>get${attrCapName}${suffix}</#if>() {
    return ${attr.name};
  }

  public void set${attrCapName}${suffix}(${newAttrType} ${attr.name}) {
    this.${attr.name} = ${attr.name};
  }
  </#list>

}
