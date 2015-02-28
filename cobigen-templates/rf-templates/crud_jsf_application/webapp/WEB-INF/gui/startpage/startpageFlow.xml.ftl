<?xml version="1.0" encoding="UTF-8"?>
<!-- @generated -->
<flow xmlns="http://www.springframework.org/schema/webflow"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/webflow
                          http://www.springframework.org/schema/webflow/spring-webflow-2.0.xsd"
	parent="plisParentFlow">

	<view-state id="startpageViewState">
		<transition on="to${pojo.name}Overview" to="${pojo.name?lower_case}OverviewMask" />

		<transition on="back" to="end" />
	</view-state>

	<subflow-state id="${pojo.name?lower_case}OverviewMask" subflow="${pojo.name?lower_case}overviewFlow">
		<transition on="end" to="startpageViewState" />
	</subflow-state>

	<end-state id="end"></end-state>

</flow>
