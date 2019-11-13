<!-- @generated -->
<div xmlns:ui="http://java.sun.com/jsf/facelets"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:s="http://bva.bund.de/taglib"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:t="http://myfaces.apache.org/tomahawk">

	<div class="table_left">

		<h:dataTable id="${pojo.name?lower_case}Overview" forceId="true"
			value="${r"#{"}${pojo.name?uncap_first}OverviewModel.${pojo.name?uncap_first}s}" var="${pojo.name?uncap_first}"
			styleClass="order-table" headerClass="order-table-header"
			rowClasses="order-table-odd-row,order-table-even-row">

			<#list pojo.fields as attr>

				<h:column>
					<!-- column header -->
					<f:facet name="header">${r"#{msg.MEL_"}${pojo.name}_${attr.name?cap_first}}</f:facet>
					<!-- row record -->
					${r"#{"}${pojo.name?uncap_first}.${attr.name}}
				</h:column>

			</#list>

			<h:column>
        <f:facet name="header">${r"#{msg.MEL_Action}"}</f:facet>
        <h:commandButton id="delete${pojo.name}"
          value="${r"#{msg.MEL_Delete}"}" action="delete${pojo.name}">
          <f:setPropertyActionListener value="${r"#{"}${pojo.name?uncap_first}}"
            target="${r"#{"}${pojo.name?uncap_first}OverviewModel.selected${pojo.name}}" />
        </h:commandButton>
        <h:commandButton id="edit${pojo.name}"
          value="${r"#{msg.MEL_Edit}"}" action="manage${pojo.name}">
          <f:setPropertyActionListener value="${r"#{"}${pojo.name?uncap_first}}"
            target="${r"#{"}${pojo.name?uncap_first}OverviewModel.selected${pojo.name}}" />
        </h:commandButton>
        </h:column>

		</h:dataTable>
	</div>

</div>

