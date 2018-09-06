<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#compress>
<#assign name = elemDoc["./@name"]>

${packagedElement._at_visibility} ${name} ${(doc["//packagedElement[@name='" + name + "']/@xmi:id"][0])!}
</#compress>