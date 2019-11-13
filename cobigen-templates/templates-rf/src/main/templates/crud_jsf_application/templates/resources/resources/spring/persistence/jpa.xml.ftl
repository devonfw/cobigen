<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
	xmlns:tx="http://www.springframework.org/schema/tx" xmlns:context="http://www.springframework.org/schema/context"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
                           http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.0.xsd
                           http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd">

	<import resource="../common.xml" />
	<context:component-scan
		base-package="com.devonfw.gastronomy.restaurant.persistence" />
	<!-- ======================================================================
		By this bean an EntityManagerFactory is created. ====================================================================== -->
	<bean id="entityManagerFactory"
		class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
		<property name="persistenceProviderClass" value="org.hibernate.ejb.HibernatePersistence" />
		<property name="persistenceUnitName" value="hibernatePersistence" />
		<property name="dataSource">
			<ref bean="appDataSource" />
		</property>
		<property name="jpaDialect">
			<bean class="org.springframework.orm.jpa.vendor.HibernateJpaDialect" />
		</property>
		<property name="jpaProperties">
			<props>
				<prop key="hibernate.dialect">org.hibernate.dialect.H2Dialect</prop>
				<!-- <prop key="hibernate.connection.isolation">4</prop> <prop key="hibernate.jdbc.use_streams_for_binary">true</prop> -->
				<prop key="hibernate.hbm2ddl.auto">update</prop>
				<prop key="hibernate.connection.useUnicode">true</prop>
				<prop key="hibernate.connection.characterEncoding">utf-8</prop>
				<prop key="hibernate.jdbc.batch_size">0</prop>
				<prop key="hibernate.show_sql">false</prop>
				<prop key="hibernate.format_sql">false</prop>
				<prop key="hibernate.default_schema">${r"${database.schema.default}"}</prop>
				<!-- Hibernate-Mappings und Lazy-Loading per Default werden hibernate.cfg.xml
					konfiguriert -->
				<prop key="hibernate.ejb.cfgfile">/resources/persistence/hibernate.cfg.xml</prop>
				<prop key="hibernate.ejb.metamodel.generation">enabled</prop>
			</props>
		</property>
	</bean>

	<bean id="appDataSource" class="org.h2.jdbcx.JdbcDataSource">
		<property name="URL" value="${r"${database.url}"}" />
		<property name="user" value="${r"${database.username}"}" />
		<property name="password" value="${r"${database.password}"}" />
	</bean>

	<!-- <bean id="appDataSource" class="oracle.jdbc.pool.OracleDataSource"
		destroy-method="close"> <property name="URL" value="${r"${database.url}"}" /> <property
		name="user" value="${r"${database.username}"}" /> <property name="password" value="${r"${database.password}"}"
		/> <property name="connectionCachingEnabled" value="true" /> <property name="connectionProperties">
		<props> <prop key="MinLimit">${r"${database.connections.max.active}"}</prop> <prop
		key="MaxLimit">${r"${database.connections.max.active}"}</prop> </props> </property>
		</bean> -->

	<!-- ======================================================================
		The Transaction-Manager to be used. ====================================================================== -->
	<bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
		<property name="entityManagerFactory">
			<ref bean="entityManagerFactory" />
		</property>
	</bean>

	<!-- Transaction handling by annotations -->
	<tx:annotation-driven transaction-manager="transactionManager" />

	<!-- This bean ensures, that JPA-Exceptions of DAOs, declared with "@Repository",
		will be mapped to more treatable Spring-Persistence-Excetions. -->
	<bean
		class="org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor" />

	<bean id="entityManagerFactoryBean"
		class="org.springframework.orm.jpa.support.SharedEntityManagerBean">
		<property name="entityManagerFactory" ref="entityManagerFactory" />
	</bean>

	<!-- ======================================================================
		DAO bean declaration. ====================================================================== -->

	<bean id="abstractDomainDao" abstract="true"
		class="${variables.rootPackage}.persistence.common.AbstractDomainDao">
		<property name="entityManager" ref="entityManagerFactoryBean" />
	</bean>

	<bean id="${pojo.name?uncap_first}Dao" parent="abstractDomainDao"
		class="${variables.rootPackage}.persistence.${variables.component}.dao.impl.${pojo.name}DaoImpl" />

</beans>
