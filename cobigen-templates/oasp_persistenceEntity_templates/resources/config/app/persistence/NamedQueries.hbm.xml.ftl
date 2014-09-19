<?xml version="1.0"?>
<!DOCTYPE hibernate-mapping PUBLIC "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
                                   "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">

<hibernate-mapping default-lazy="true">

	<query name="get.all.${pojo.name?lower_case}s">
    	<![CDATA[SELECT t FROM ${pojo.name} t]]>
	</query>
	
</hibernate-mapping>
