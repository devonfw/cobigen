package ${variables.component?replace(' ', '')};

<#assign package = doc["XMI/Model/packagedElement/packagedElement/packagedElement"]>

public class ${variables.className} {

    //scope type name;
    //variable {package["packagedElement/ownedAttribute/@name"]} 

    <#if (package["packagedElement/ownedAttribute/@name"])?has_content>
        ${package["packagedElement/ownedAttribute/@visibility"]} ${package["packagedElement/ownedAttribute/type/@idref"]?replace("EAJava_","")} ${package["packagedElement/ownedAttribute/@name"]};
    </#if>

    <#if (package["packagedElement/ownedOperation/@name"])?has_content>
        ${package["packagedElement/ownedOperation/@visibility"]} void ${package["packagedElement/ownedOperation/@name"]}(<#list package["packagedElement/ownedOperation/ownedParameter"] as parameter> <#if parameter["@direction"]?contains("in")>${parameter["@type"]?replace("EAJava_","")} ${parameter["@name"]}</#if></#list>){

            <#list package["packagedElement/ownedOperation/ownedParameter"] as parameter> 
                <#if parameter["@direction"]?contains("return")>
                    return ${parameter["@type"]?replace("EAJava_","")} ${parameter["@name"]};
                </#if>
            </#list>
        }
    </#if>
}
