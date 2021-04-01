<#list pojo.methods as method>
	<#if method.annotations.javax_ws_rs_Path?has_content>
${method.annotations.javax_ws_rs_Path.value}
	</#if>
</#list>