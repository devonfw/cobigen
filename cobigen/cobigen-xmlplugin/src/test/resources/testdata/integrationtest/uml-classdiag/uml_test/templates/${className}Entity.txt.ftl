<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#compress>
<#assign name = elemDoc["/packagedElement/@name"]>

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name=${variables.className})
public class ${variables.className}Entity extends ApplicationPersistenceEntity implements ${variables.className} {

    private static final long serialVersionUID = 1L;

    <#list elemDoc["/packagedElement/ownedAttribute"] as attribute>
        <#if (attribute["@name"])?has_content>
    ${attribute["@visibility"]} ${attribute["type/@xmi:idref"]?replace("EAJava_","")} ${attribute["@name"]};
        </#if>
    </#list>

    
    <#list elemDoc["/packagedElement/ownedAttribute"] as attribute>
        <#if (attribute["@name"])?has_content>
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