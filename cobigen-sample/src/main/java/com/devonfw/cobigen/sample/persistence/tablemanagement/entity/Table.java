package com.capgemini.cobigen.sample.persistence.tablemanagement.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

import com.capgemini.cobigen.sample.common.datatype.TableState;

/**
 * The persistent entity representing a table of the restaurant. A table has a unique {@link #getNumber() number}
 * (primary key) can be {@link TableState#isReserved() reserved}, {@link TableState#isOccupied() occupied} and may have
 * a waiter assigned.
 * 
 * @author hohwille
 */
@Entity
@javax.persistence.Table(name = "RestaurantTable")
public class Table implements Serializable {

    /** UID for serialization. */
    private static final long serialVersionUID = 1L;

    private long number;

    private Long waiterId;

    private int version;

    private TableState state;

    /**
     * Returns the field 'number'.
     * 
     * @return Value of number
     */
    @Id
    public long getNumber() {

        return this.number;
    }

    /**
     * Sets the field 'number'.
     * 
     * @param number New value for number
     */
    public void setNumber(long number) {

        this.number = number;
    }

    /**
     * Returns the field 'version'.
     * 
     * @return Value of version
     */
    @Version
    public int getVersion() {

        return this.version;
    }

    /**
     * Sets the field 'version'.
     * 
     * @param version New value for version
     */
    public void setVersion(int version) {

        this.version = version;
    }

    /**
     * Returns the field 'waiterId'.
     * 
     * @return Value of waiterId
     */
    public Long getWaiterId() {

        return this.waiterId;
    }

    /**
     * Sets the field 'waiterId'.
     * 
     * @param waiterId New value for waiterId
     */
    public void setWaiterId(Long waiterId) {

        this.waiterId = waiterId;
    }

    /**
     * Returns the field 'state'.
     * 
     * @return Value of state
     */
    public TableState getState() {

        return this.state;
    }

    /**
     * Sets the field 'state'.
     * 
     * @param state New value for state
     */
    public void setState(TableState state) {

        this.state = state;
    }

}
