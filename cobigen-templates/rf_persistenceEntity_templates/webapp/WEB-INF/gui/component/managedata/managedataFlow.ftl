<?xml version="1.0" encoding="UTF-8"?>
<!-- @generated -->
<flow xmlns="http://www.springframework.org/schema/webflow"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/webflow
                          http://www.springframework.org/schema/webflow/spring-webflow-2.0.xsd"
	parent="plisParentFlow">

	<var name="manage${pojo.name}Model"
    class="${variables.rootPackage}.gui.${variables.component}.manage${pojo.name?lower_case}.Manage${pojo.name}Model" />

  <view-state id="manage${pojo.name?lower_case}ViewState" model="manage${pojo.name}Model">
    <on-render>
      <evaluate
        expression="manage${pojo.name}Model.setSelected${pojo.name}(conversationScope.selected${pojo.name})" />
    </on-render>

    <transition on="save" to="end">
      <evaluate expression="manage${pojo.name}Controller.update${pojo.name}(manage${pojo.name}Model)" />
    </transition>

    <transition on="back" to="end" />

  </view-state>

  <end-state id="end"></end-state>

</flow>
