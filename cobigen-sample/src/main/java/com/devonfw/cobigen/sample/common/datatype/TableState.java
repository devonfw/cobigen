package com.capgemini.cobigen.sample.common.datatype;

/**
 * Represents the state of an {@link com.capgemini.gastronomy.restaurant.persistence.tablemanagement.entity.Table}.
 * 
 * @author etomety
 * @version $Id: TableState.java 857 2013-11-01 12:54:40Z mbrunnli $
 */
public enum TableState {
    /**
     * The state if the {@link com.capgemini.gastronomy.restaurant.persistence.tablemanagement.entity.Table} has been
     * marked as free.
     */
    FREE,
    /**
     * The state if the {@link com.capgemini.gastronomy.restaurant.persistence.tablemanagement.entity.Table} has been
     * marked as reserved.
     */
    RESERVED,
    /**
     * The state if the {@link com.capgemini.gastronomy.restaurant.persistence.tablemanagement.entity.Table} has been
     * marked as occupied.
     */
    OCCUPIED;

    /**
     * @return <code>true</code> if the
     *         {@link com.capgemini.gastronomy.restaurant.persistence.tablemanagement.entity.Table} is free.
     */
    public boolean isFree() {

        return (this == FREE);
    }

    /**
     * @return <code>true</code> if the
     *         {@link com.capgemini.gastronomy.restaurant.persistence.tablemanagement.entity.Table} is reserved.
     */
    public boolean isReserved() {

        return (this == RESERVED);
    }

    /**
     * @return <code>true</code> if the
     *         {@link com.capgemini.gastronomy.restaurant.persistence.tablemanagement.entity.Table} is occupied.
     */
    public boolean isOccupied() {

        return (this == OCCUPIED);
    }

}
