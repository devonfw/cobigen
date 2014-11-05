<!-- @generated -->
<div xmlns:ui="http://java.sun.com/jsf/facelets"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:s="http://bva.bund.de/taglib"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:t="http://myfaces.apache.org/tomahawk">

	<div class="form_wide">

		<div class="button">
			<t:commandButton forceId="true" id="${pojo.name}"
				value="${r"#{msg.MEL_"}${pojo.name}_Overview}" action="to${pojo.name}Overview"
				styleClass="btn suchen re" />
		</div>

	</div>

</div>
