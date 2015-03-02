<?xml version="1.0" encoding="UTF-8"?>
<!-- @generated -->
<flow xmlns="http://www.springframework.org/schema/webflow"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/webflow
                          http://www.springframework.org/schema/webflow/spring-webflow-2.0.xsd"
	parent="plisParentFlow">

	<var name="create${pojo.name}Model"
		class="${variables.rootPackage}.gui.${variables.component}.create${pojo.name?lower_case}.Create${pojo.name}Model" />

	<view-state id="create${pojo.name?lower_case}ViewState" model="create${pojo.name}Model">
		<transition on="create" to="end">
			<evaluate expression="create${pojo.name}Controller.create${pojo.name}(create${pojo.name}Model)" />
		</transition>

		<transition on="back" to="end" />

	</view-state>

	<end-state id="end"></end-state>

</flow>
