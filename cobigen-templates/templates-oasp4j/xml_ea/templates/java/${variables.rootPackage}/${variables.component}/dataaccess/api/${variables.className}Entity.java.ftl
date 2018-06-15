<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#compress>
<#assign name = elemDoc["self::node()/@name"]>
<#assign connectors = doc["xmi:XMI/xmi:Extension/connectors/connector"]>
package ${variables.rootPackage}.${variables.component}.dataaccess.api;

import ${variables.rootPackage}.${variables.component}.common.api.${variables.className};
import ${variables.rootPackage}.general.dataaccess.api.ApplicationPersistenceEntity;

<#-- Class connections/associations -->
<#list connectors as connector>
    <#assign source = connector["source"]>
    <#assign target = connector["target"]> 
    <#-- We store the information of the connectors of this class to a variable -->
    ${OaspUtil.resolveConnectorsContent(source, target, name)}
</#list>

<#-- For generating the needed imports from each connected class -->
<#list OaspUtil.getConnectedClasses() as connectedClass>
import ${variables.rootPackage}.${variables.component}.common.api.${connectedClass};
</#list>

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name="${variables.className}")


  <#-- Decide if extends tag is needed -->
  <#assign boolean = false>
  <#list connectors as connector>
        <#-- I am source -->
        <#if (connector["source/model/@type"] == "Class")>
            <#-- If I am the source connector, check target's multiplicity -->
            <#if ((connector["source/model/@name"]) == '${variables.className}')>
              <#if (boolean == false)>
                <#assign ext = connector["target/model/@name"]>  
              </#if>
              <#assign boolean = true>
            </#if>
        </#if>
  </#list>
  <#if boolean = true>
    public class ${variables.className}Entity extends ApplicationPersistenceEntity implements ${variables.className} {
  <#else>
    public class ${variables.className}Entity extends ApplicationPersistenceEntity implements ${variables.className} {  
  </#if>

    private static final long serialVersionUID = 1L;

    <#-- Class attributes -->
    <#list elemDoc["self::node()/ownedAttribute"] as attribute>
        <#if (attribute["@name"])??>
    ${attribute["@visibility"]} ${attribute["type/@xmi:idref"]?replace("EAJava_","")} ${attribute["@name"]};
        </#if>
    </#list>

    <#-- For generating the variables and methods of all the connected classes to this class -->
    ${OaspUtil.generateConnectorsVariablesMethodsText("")}
    
    <#list elemDoc["self::node()/ownedAttribute"] as attribute>
        <#if (attribute["@name"])??>
    @Override
            <#if (attribute["type/@xmi:idref"]) == "EAJava_int">
    public Integer get${attribute["@name"]?cap_first}(){
            <#else>    
    public ${(attribute["type/@xmi:idref"]?replace("EAJava_",""))?cap_first} get${attribute["@name"]?capitalize}(){
            </#if>
        return this.${attribute["@name"]};
    }
            <#if (attribute["type/@xmi:idref"]) == "EAJava_int">
    public void set${attribute["@name"]?cap_first}(Integer ${attribute["@name"]}){
            <#else>    
    public void set${attribute["@name"]?cap_first}(${(attribute["type/@xmi:idref"]?replace("EAJava_",""))?capitalize}){
            </#if>
        this.${attribute["@name"]} = ${attribute["@name"]};
    }
        </#if>
    </#list>
}

</#compress>