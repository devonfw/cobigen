<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
	xmlns:util="http://www.springframework.org/schema/util"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
                    http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-3.0.xsd
                    http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-3.0.xsd">

	<!-- ======================================================================
		Core-Wrapper Link: CoreWrapper <-> GUI, has to be involved into the same
		Spring context as the TransactionManager. ====================================================================== -->

	<bean id="${pojo.name?uncap_first}ManagementCoreWrapper"
		class="${variables.rootPackage}.gui.${variables.component}.corewrapper.impl.${pojo.name}ManagementCoreWrapperImpl">
		<property name="${pojo.name?uncap_first}Management" ref="${pojo.name?uncap_first}Management" />
	</bean>

</beans>
