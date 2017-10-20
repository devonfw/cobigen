package main.java.umlTest;

<#list doc["xmi:XMI"].Children as parent>
</#list>

<#list .vars['xmi:XMI'].Children as parent>
<#if parent._nodeName_ == "uml:Model">
	// ${parent._at_name}
	<#list parent.Children as firstChild>
	// ${firstChild._at_name}
		<#list firstChild.Children as secondChild>
public class ${secondChild._at_name} {
			// CLASS NAME: ${secondChild._at_name}
		</#list>
	</#list>
</#if>
</#list>

public int x;

public String y;

}
