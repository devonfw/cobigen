package com.maven.project.common.builders;

import java.util.LinkedList;
import java.util.List;
import com.maven.project.sampledatamanagement.dataaccess.api.SampleDataEntity;
import com.maven.project.common.builders.P;
import javax.persistence.EntityManager;

/**
 * Test data builder for SampleDataEntity generated with cobigen.
 */
public class SampleDataEntityBuilder {

	private List<P<SampleDataEntity>> parameterToBeApplied;

	/**
	 * The constructor.
	 */
	public SampleDataEntityBuilder() {
	
	
	
		this.parameterToBeApplied = new LinkedList<>();
		fillMandatoryFields();
		fillMandatoryFields_custom();
	}

	/**
	 * @param name the name to add.
	 * @return the builder for fluent population of fields.
	 */
	public SampleDataEntityBuilder name(final String name) {
        this.parameterToBeApplied.add(new P<SampleDataEntity>() {
            @Override
            public void apply(SampleDataEntity target) {
                target.setName(name);
            }
        });
        return this;
    }

	/**
	 * @param surname the surname to add.
	 * @return the builder for fluent population of fields.
	 */
	public SampleDataEntityBuilder surname(final String surname) {
        this.parameterToBeApplied.add(new P<SampleDataEntity>() {
            @Override
            public void apply(SampleDataEntity target) {
                target.setSurname(surname);
            }
        });
        return this;
    }

	/**
	 * @param mail the mail to add.
	 * @return the builder for fluent population of fields.
	 */
	public SampleDataEntityBuilder mail(final String mail) {
        this.parameterToBeApplied.add(new P<SampleDataEntity>() {
            @Override
            public void apply(SampleDataEntity target) {
                target.setMail(mail);
            }
        });
        return this;
    }

	/**
	 * @param age the age to add.
	 * @return the builder for fluent population of fields.
	 */
	public SampleDataEntityBuilder age(final Integer age) {
        this.parameterToBeApplied.add(new P<SampleDataEntity>() {
            @Override
            public void apply(SampleDataEntity target) {
                target.setAge(age);
            }
        });
        return this;
    }

	/**
	 * @return the populated SampleDataEntity.
	 */
	public SampleDataEntity createNew() {
        SampleDataEntity sampledataentity = new SampleDataEntity();
        for (P<SampleDataEntity> parameter : parameterToBeApplied) {
            parameter.apply(sampledataentity);
        }
        return sampledataentity;
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
	 * @return the SampleDataEntity
	 */
	public SampleDataEntity persist(EntityManager em) {
        SampleDataEntity sampledataentity = createNew();
        em.persist(sampledataentity);
        return sampledataentity;
    }

	/**
	 * @param em the {@link EntityManager}
	 * @param quantity the quantity
	 * @return a list of SampleDataEntity
	 */
	public List<SampleDataEntity> persistAndDuplicate(EntityManager em, int quantity) {

        List<SampleDataEntity> sampledataentityList = new LinkedList<>();
        for (int i = 0; i < quantity; i++) {
            SampleDataEntity sampledataentity = createNew();
            // TODO alter at least values with unique key constraints to prevent from exceptions while persisting
            em.persist(sampledataentity);
            sampledataentityList.add(sampledataentity);
        }

        return sampledataentityList;
    }

}
