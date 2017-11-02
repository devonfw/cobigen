package ${variables.component?replace(' ', '')};

<#assign root = doc["XMI/Model/packagedElement/packagedElement[@name='${variables.className}']"]>

public class ${variables.className} {

    //public {root["ownedAttribute/type/@idref"]} {root["ownedAttribute/@name"]};
}
