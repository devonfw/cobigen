<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1"}>
<#assign name = elemDoc["self::node()/@name"]>
<#assign connectors = doc["xmi:XMI/xmi:Extension/connectors/connector"]>

<#-- Class connections/associations -->
<#list connectors as connector>
    <#assign source = connector["source"]>
    <#assign target = connector["target"]> 
    ${OaspUtil.resolveConnectorsContent(source, target, name)}
</#list>
    ${OaspUtil.generateConnectorsVariablesMethodsText("")}