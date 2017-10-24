<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${variables.rootPackage}.${variables.component}.mapper.${variables.entityName}Mapper">
<select id="fetch" resultType="${variables.rootPackage}.${variables.component}.dataaccess.api.${pojo.name}" parameterType="${variables.rootPackage}.${variables.component}.logic.api.to.${variables.entityName}SearchCriteria">
        select * from ${variables.entityName}
        
        <if test ="searchCriteria != null">
        <where>
        <#list pojo.fields as field>
            <if test="searchCriteria.${field.name} != null">
               and ${field.name} = ${r"#{"}searchCriteria.${field.name}}
            </if>
         </#list>
       </where>
        </if>
        
       
    </select>
</mapper>
