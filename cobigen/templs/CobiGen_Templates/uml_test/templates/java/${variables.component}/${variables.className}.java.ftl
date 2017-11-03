package ${variables.component?replace(' ', '')};

<#assign root = doc["XMI/package/packagedElement"]>

public class ${variables.className} {

    //scope type name;
    //public {root["ownedAttribute/type/@idref"]} {root["ownedAttribute/@name"]};
}
