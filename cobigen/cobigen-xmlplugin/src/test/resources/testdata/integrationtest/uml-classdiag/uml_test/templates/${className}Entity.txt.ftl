<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#compress>
<#assign name = elemDoc["./@name"]>
<#assign connectors = doc["xmi:XMI/xmi:Extension/connectors/connector"]>

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name=${variables.className})
public class ${variables.className}Entity extends ApplicationPersistenceEntity implements ${variables.className} {

    private static final long serialVersionUID = 1L;

    <#-- Class attributes -->
    <#list elemDoc["./ownedAttribute"] as attribute>
        <#if (attribute["@name"])??>
    ${attribute["@visibility"]} ${attribute["type/@xmi:idref"]?replace("EAJava_","")} ${attribute["@name"]};
        </#if>
    </#list>

    <#-- Class connections/associations -->
    <#list connectors as connector>
        <#-- I am source -->
        <#if (connector["source/model/@type"] == "Class")>
            <#-- If I am the source connector, check target's multiplicity -->
            <#if ((connector["source/model/@name"]) == '${variables.className}')>
                <#if (connector["target/type/@multiplicity"] )?is_string>
                    <#if (connector["target/type/@multiplicity"] == "1")>
    // I want one
    private ${connector["target/model/@name"]} ${connector["target/model/@name"]?uncap_first};
    @Override
    public ${connector["target/model/@name"]?cap_first} get${connector["target/model/@name"]?cap_first}(){
        return this.${connector["target/model/@name"]?uncap_first};
    }
    @Override
    public void set${connector["target/model/@name"]?cap_first}(${connector["target/model/@name"]?cap_first} ${connector["target/model/@name"]?uncap_first}){
       ${connector["target/model/@name"]?uncap_first} = this.${connector["target/model/@name"]?uncap_first};
    }
                    <#elseif (connector["target/type/@multiplicity"] == "*")>   
    // I want many
    private List<${connector["target/model/@name"]}> ${connector["target/model/@name"]?uncap_first}s;
    @Override
    public List<${connector["target/model/@name"]?cap_first}> get${connector["target/model/@name"]?cap_first}(){
        return this.${connector["target/model/@name"]?uncap_first};
    }
    @Override
    public void set${connector["target/model/@name"]?cap_first}(List<${connector["target/model/@name"]?cap_first}> ${connector["target/model/@name"]?uncap_first}){
       ${connector["target/model/@name"]?uncap_first} = this.${connector["target/model/@name"]?uncap_first};
    }
                    </#if>
                </#if>
            </#if>
        </#if>
        <#-- I am target -->
        <#if (connector["target/model/@type"] == "Class")>
            <#-- If I am the target connector, check sources' multiplicity -->
            <#if ((connector["target/model/@name"]) == '${variables.className}')>
                <#if (connector["source/type/@multiplicity"] )?is_string>
                    <#if (connector["source/type/@multiplicity"] == "1")>
    // I want one
    private ${connector["source/model/@name"]} ${connector["source/model/@name"]?uncap_first};
    @Override
    public ${connector["source/model/@name"]?cap_first} get${connector["source/model/@name"]?cap_first}(){
        return this.${connector["source/model/@name"]?uncap_first};
    }
    @Override
    public void set${connector["source/model/@name"]?cap_first}(${connector["source/model/@name"]?cap_first} ${connector["source/model/@name"]?uncap_first}){
       ${connector["source/model/@name"]?uncap_first} = this.${connector["source/model/@name"]?uncap_first};
    }
                    <#elseif (connector["source/type/@multiplicity"] == "*")>   
    // I want many
    private List<${connector["source/model/@name"]}> ${connector["source/model/@name"]?uncap_first}s;
    @Override
    public List<${connector["source/model/@name"]?cap_first}> get${connector["source/model/@name"]?cap_first}(){
        return this.${connector["source/model/@name"]?uncap_first};
    }
    @Override
    public void set${connector["source/model/@name"]?cap_first}(List<${connector["source/model/@name"]?cap_first}> ${connector["source/model/@name"]?uncap_first}){
       ${connector["source/model/@name"]?uncap_first} = this.${connector["source/model/@name"]?uncap_first};
    }
                    </#if>
                </#if>
            </#if>
        </#if>
    </#list>

    
    <#list elemDoc["./ownedAttribute"] as attribute>
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