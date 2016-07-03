<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
	xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
       http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd">


	<bean id="abstract${pojo.name}Uc" abstract="true"
		class="${variables.rootPackage}.core.${variables.component}.common.Abstract${pojo.name}Uc">
		<property name="${pojo.name?uncap_first}Dao" ref="${pojo.name?uncap_first}Dao" />
	</bean>

	<bean name="${pojo.name?uncap_first}Management" parent="abstractLayerImpl"
		class="${variables.rootPackage}.core.${variables.component}.impl.${pojo.name}ManagementImpl">
		<property name="ucFind${pojo.name}">
			<bean
				class="${variables.rootPackage}.core.${variables.component}.impl.UcFind${pojo.name}"
				parent="abstract${pojo.name}Uc" />
		</property>
		<property name="ucManage${pojo.name}">
			<bean
				class="${variables.rootPackage}.core.${variables.component}.impl.UcManage${pojo.name}"
				parent="abstract${pojo.name}Uc" />
		</property>
	</bean>
</beans>
