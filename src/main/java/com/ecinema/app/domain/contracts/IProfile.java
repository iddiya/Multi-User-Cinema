package com.ecinema.app.domain.contracts;

import java.time.LocalDate;

/**
 * Defines the contract of the profile.
 */
public interface IProfile {

    /**
     * Gets the first name.
     *
     * @return the first name
     */
    String getFirstName();

    /**
     * Sets the first name.
     *
     * @param firstName the first name
     */
    void setFirstName(String firstName);

    /**
     * Gets the last name.
     *
     * @return the last name
     */
    String getLastName();

    /**
     * Sets the last name.
     *
     * @param lastName the last name
     */
    void setLastName(String lastName);

    /**
     * Gets the birthdate.
     *
     * @return the birthdate
     */
    LocalDate getBirthDate();

    /**
     * Sets the birthdate.
     *
     * @param birthDate the birthdate
     */
    void setBirthDate(LocalDate birthDate);

    /**
     * Sets the fields of this Profile to those of the provided Profile.
     *
     * @param o the other Profile to fetch fields from
     */
    default void setToIProfile(IProfile o) {
        setFirstName(o.getFirstName());
        setLastName(o.getLastName());
        setBirthDate(o.getBirthDate());
    }

    /**
     * Returns if the fields of this Profile are equal to that of the provided Profile
     *
     * @param o the other Profile to compare to
     * @return true if the fields of this and the other Profile match
     */
    default boolean profileEquals(IProfile o) {
        return getFirstName().equals(o.getFirstName()) &&
                getLastName().equals(o.getLastName()) &&
                getBirthDate().equals(o.getBirthDate());
    }

}
