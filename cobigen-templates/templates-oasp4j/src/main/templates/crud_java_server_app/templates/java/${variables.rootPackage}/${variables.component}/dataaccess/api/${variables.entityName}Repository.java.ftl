<#include '/functions.ftl'>
package ${variables.rootPackage}.${variables.component}.dataaccess.api;

import static com.querydsl.core.alias.Alias.$;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.querydsl.jpa.impl.JPAQuery;

import ${variables.rootPackage}.${variables.component}.common.api.${variables.entityName};
import ${variables.rootPackage}.${variables.component}.common.api.to.${variables.entityName}SearchCriteriaTo;
import io.oasp.module.jpa.dataaccess.api.QueryUtil;
import io.oasp.module.jpa.dataaccess.api.data.DefaultRepository;


/**
 * {@link DefaultRepository} for {@link ${variables.entityName}}
 */
public interface ${variables.entityName}Repository extends DefaultRepository<${variables.entityName}Entity> {

}
