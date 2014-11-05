<?xml version="1.0" encoding="UTF-8"?>
<!-- @generated -->
<flow xmlns="http://www.springframework.org/schema/webflow"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/webflow
                          http://www.springframework.org/schema/webflow/spring-webflow-2.0.xsd"
	parent="plisParentFlow">

	<var name="${pojo.name?uncap_first}OverviewModel"
		class="${variables.rootPackage}.gui.${variables.component}.${pojo.name?lower_case}overview.${pojo.name}OverviewModel" />

	<view-state id="${pojo.name?lower_case}overviewViewState" model="${pojo.name?uncap_first}OverviewModel">
		<on-render>
			<evaluate
				expression="${pojo.name?uncap_first}OverviewController.create${pojo.name}Overview(${pojo.name?uncap_first}OverviewModel)" />
		</on-render>

		<transition on="delete${pojo.name}" to="${pojo.name?lower_case}overviewViewState">
			<evaluate expression="${pojo.name?uncap_first}OverviewController.delete${pojo.name}(${pojo.name?uncap_first}OverviewModel)"></evaluate>
		</transition>

		<transition on="create${pojo.name}" to="create${pojo.name}Mask" validate="false" />

		<transition on="manage${pojo.name}" to="manage${pojo.name}Mask" validate="false" >
		  <evaluate expression="${pojo.name?uncap_first}OverviewModel.getSelected${pojo.name}()" result="conversationScope.selected${pojo.name}" />
		</transition>

		<transition on="back" to="end" />
	</view-state>

	<subflow-state id="create${pojo.name}Mask" subflow="create${pojo.name?lower_case}Flow">
	  <transition on="end" to="${pojo.name?lower_case}overviewViewState" />
	</subflow-state>

	<subflow-state id="manage${pojo.name}Mask" subflow="manage${pojo.name?lower_case}Flow">
    <transition on="end" to="${pojo.name?lower_case}overviewViewState" />
  </subflow-state>

	<end-state id="end"></end-state>
</flow>
