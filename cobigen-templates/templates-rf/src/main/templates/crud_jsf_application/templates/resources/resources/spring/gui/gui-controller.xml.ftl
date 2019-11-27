<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
	xmlns:flow="http://www.springframework.org/schema/webflow-config"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
                    http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd
                    http://www.springframework.org/schema/webflow-config
            http://www.springframework.org/schema/webflow-config/spring-webflow-config-1.1.xsd">

	<!-- ======================================================================
		General-Controller ====================================================================== -->
	<bean id="abstractController" abstract="true"
		class="${variables.rootPackage}.gui.common.AbstractController">
	</bean>

	<bean id="trefferlistenController"
		class="de.bund.bva.pliscommon.plisweb.trefferliste.controller.TrefferlistenController" />
	<bean id="dataScrollerActionListener"
		class="de.bund.bva.pliscommon.plisweb.trefferliste.actionlistener.DataScrollerActionListener" />

	<bean id="buttonActionListener"
		class="de.bund.bva.pliscommon.plisweb.trefferliste.actionlistener.ButtonActionListener" />

	<!-- ======================================================================
		Controller for ${variables.component} ====================================================================== -->
	<bean id="abstract${pojo.name}Controller" parent="abstractController"
		abstract="true"
		class="${variables.rootPackage}.gui.${variables.component}.common.Abstract${pojo.name}Controller">
		<property name="coreWrapper" ref="${pojo.name?uncap_first}ManagementCoreWrapper" />
	</bean>
	<bean id="create${pojo.name}Controller" parent="abstract${pojo.name}Controller"
		class="${variables.rootPackage}.gui.${variables.component}.create${pojo.name?lower_case}.Create${pojo.name}Controller">
	</bean>
	<bean id="manage${pojo.name}Controller" parent="abstract${pojo.name}Controller"
		class="${variables.rootPackage}.gui.${variables.component}.manage${pojo.name?lower_case}.Manage${pojo.name}Controller">
	</bean>
	<bean id="${pojo.name?uncap_first}OverviewController" parent="abstract${pojo.name}Controller"
		class="${variables.rootPackage}.gui.${variables.component}.${pojo.name?lower_case}overview.${pojo.name}OverviewController">
	</bean>

	<!-- ======================================================================
		Webflow-Validator for ${variables.component} ====================================================================== -->
	<bean id="create${pojo.name}ModelValidator"
		class="${variables.rootPackage}.gui.${variables.component}.create${pojo.name?lower_case}.Create${pojo.name}ModelValidator" />
	<bean id="manage${pojo.name}ModelValidator"
    class="${variables.rootPackage}.gui.${variables.component}.manage${pojo.name?lower_case}.Manage${pojo.name}ModelValidator" />
</beans>
