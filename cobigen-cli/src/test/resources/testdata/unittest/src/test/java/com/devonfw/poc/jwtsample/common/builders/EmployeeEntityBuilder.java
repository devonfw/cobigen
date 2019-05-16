package com.devonfw.poc.jwtsample.common.builders;

import java.util.LinkedList;
import java.util.List;
import com.devonfw.poc.jwtsample.employeemanagement.dataaccess.api.EmployeeEntity;
import com.devonfw.poc.jwtsample.common.builders.P;
import javax.persistence.EntityManager;

/**
 * Test data builder for EmployeeEntity generated with cobigen.
 */
public class EmployeeEntityBuilder {

	private List<P<EmployeeEntity>> parameterToBeApplied;

	/**
	 * The constructor.
	 */
	public EmployeeEntityBuilder() {
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		this.parameterToBeApplied = new LinkedList<>();
		fillMandatoryFields();
		fillMandatoryFields_custom();
	}

	/**
	 * @param employeeId the employeeId to add.
	 * @return the builder for fluent population of fields.
	 */
	public EmployeeEntityBuilder employeeId(final Long employeeId) {
        this.parameterToBeApplied.add(new P<EmployeeEntity>() {
            @Override
            public void apply(EmployeeEntity target) {
                target.setEmployeeId(employeeId);
            }
        });
        return this;
    }

	/**
	 * @param name the name to add.
	 * @return the builder for fluent population of fields.
	 */
	public EmployeeEntityBuilder name(final String name) {
        this.parameterToBeApplied.add(new P<EmployeeEntity>() {
            @Override
            public void apply(EmployeeEntity target) {
                target.setName(name);
            }
        });
        return this;
    }

	/**
	 * @param surname the surname to add.
	 * @return the builder for fluent population of fields.
	 */
	public EmployeeEntityBuilder surname(final String surname) {
        this.parameterToBeApplied.add(new P<EmployeeEntity>() {
            @Override
            public void apply(EmployeeEntity target) {
                target.setSurname(surname);
            }
        });
        return this;
    }

	/**
	 * @param email the email to add.
	 * @return the builder for fluent population of fields.
	 */
	public EmployeeEntityBuilder email(final String email) {
        this.parameterToBeApplied.add(new P<EmployeeEntity>() {
            @Override
            public void apply(EmployeeEntity target) {
                target.setEmail(email);
            }
        });
        return this;
    }

	/**
	 * @return the populated EmployeeEntity.
	 */
	public EmployeeEntity createNew() {
        EmployeeEntity employeeentity = new EmployeeEntity();
        for (P<EmployeeEntity> parameter : parameterToBeApplied) {
            parameter.apply(employeeentity);
        }
        return employeeentity;
    }

	/**
	 * Might be enriched to users needs (will not be overwritten)
	 */
	private void fillMandatoryFields_custom() {
    
    }

	/**
	 * Fills all mandatory fields by default. (will be overwritten on re-generation)
	 */
	private void fillMandatoryFields() {
    }

	/**
	 * @param em the {@link EntityManager}
	 * @return the EmployeeEntity
	 */
	public EmployeeEntity persist(EntityManager em) {
        EmployeeEntity employeeentity = createNew();
        em.persist(employeeentity);
        return employeeentity;
    }

	/**
	 * @param em the {@link EntityManager}
	 * @param quantity the quantity
	 * @return a list of EmployeeEntity
	 */
	public List<EmployeeEntity> persistAndDuplicate(EntityManager em, int quantity) {

        List<EmployeeEntity> employeeentityList = new LinkedList<>();
        for (int i = 0; i < quantity; i++) {
            EmployeeEntity employeeentity = createNew();
            // TODO alter at least values with unique key constraints to prevent from exceptions while persisting
            em.persist(employeeentity);
            employeeentityList.add(employeeentity);
        }

        return employeeentityList;
    }

}
