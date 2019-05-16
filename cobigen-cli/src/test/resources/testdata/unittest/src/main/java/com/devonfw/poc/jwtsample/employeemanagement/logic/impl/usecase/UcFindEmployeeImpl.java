package com.devonfw.poc.jwtsample.employeemanagement.logic.impl.usecase;

import com.devonfw.poc.jwtsample.employeemanagement.logic.api.to.EmployeeEto;
import com.devonfw.poc.jwtsample.employeemanagement.logic.api.usecase.UcFindEmployee;
import com.devonfw.poc.jwtsample.employeemanagement.logic.base.usecase.AbstractEmployeeUc;
import com.devonfw.poc.jwtsample.employeemanagement.dataaccess.api.EmployeeEntity;
import com.devonfw.poc.jwtsample.employeemanagement.logic.api.to.EmployeeSearchCriteriaTo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import javax.inject.Named;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use case implementation for searching, filtering and getting Employees
 */
@Named
@Validated
@Transactional
public class UcFindEmployeeImpl extends AbstractEmployeeUc implements UcFindEmployee {

	/**
	 * Logger instance.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(UcFindEmployeeImpl.class);

	@Override
	public EmployeeEto findEmployee(long id) {
      LOG.debug("Get Employee with id {} from database.", id);
      Optional<EmployeeEntity> foundEntity = getEmployeeRepository().findById(id);
      if (foundEntity.isPresent())
        return getBeanMapper().map(foundEntity.get(), EmployeeEto.class);
      else
        return null;
    }

	@Override
	public Page<EmployeeEto> findEmployees(EmployeeSearchCriteriaTo criteria) {
      Page<EmployeeEntity> employees = getEmployeeRepository().findByCriteria(criteria);
    return mapPaginatedEntityList(employees, EmployeeEto.class);
  }

	@Override
	public EmployeeCto findEmployeeCto(long id) {
      LOG.debug("Get EmployeeCto with id {} from database.", id);
      EmployeeEntity entity = getEmployeeRepository().find(id);
      EmployeeCto cto = new EmployeeCto();
      cto.setEmployee(getBeanMapper().map(entity, EmployeeEto.class));
   
      return cto;
    }

	@Override
	public Page<EmployeeCto> findEmployeeCtos(EmployeeSearchCriteriaTo criteria) {
  
      Page<EmployeeEntity> employees = getEmployeeRepository().findByCriteria(criteria);
      List<EmployeeCto> ctos = new ArrayList<>();
      for (EmployeeEntity entity : employees.getContent()) {
        EmployeeCto cto = new EmployeeCto();
        cto.setEmployee(getBeanMapper().map(entity, EmployeeEto.class));
        ctos.add(cto);
      }
      Pageable pagResultTo = PageRequest.of(criteria.getPageable().getPageNumber(), criteria.getPageable().getPageSize());
      
      return new PageImpl<>(ctos, pagResultTo, employees.getTotalElements());
    }

}
