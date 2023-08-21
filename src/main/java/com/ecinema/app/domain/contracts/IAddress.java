package com.ecinema.app.domain.contracts;

import com.ecinema.app.domain.enums.UsState;

/**
 * The interface Address.
 */
public interface IAddress {

    /**
     * Gets street.
     *
     * @return the street
     */
    String getStreet();

    /**
     * Sets street.
     *
     * @param street the street
     */
    void setStreet(String street);

    /**
     * Gets city.
     *
     * @return the city
     */
    String getCity();

    /**
     * Sets city.
     *
     * @param city the city
     */
    void setCity(String city);

    /**
     * Gets us state.
     *
     * @return the us state
     */
    UsState getUsState();

    /**
     * Sets us state.
     *
     * @param usState the us state
     */
    void setUsState(UsState usState);

    /**
     * Gets zipcode.
     *
     * @return the zipcode
     */
    String getZipcode();

    /**
     * Sets zipcode.
     *
     * @param zipcode the zipcode
     */
    void setZipcode(String zipcode);

    /**
     * Set.
     *
     * @param address the address
     */
    default void set(IAddress address) {
        setStreet(address.getStreet());
        setCity(address.getCity());
        setUsState(address.getUsState());
        setZipcode(address.getZipcode());
    }

}
