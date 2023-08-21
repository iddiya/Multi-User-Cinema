package com.ecinema.app.domain.contracts;

/**
 * The interface Password.
 */
public interface IPassword {

    /**
     * Sets the password.
     *
     * @param password the password
     */
    void setPassword(String password);

    /**
     * Gets the password.
     *
     * @return the password
     */
    String getPassword();

    /**
     * Sets the password confirmation.
     *
     * @param confirmPassword the confirmation password
     */
    void setConfirmPassword(String confirmPassword);

    /**
     * Gets the password confirmation.
     *
     * @return the confirmation password
     */
    String getConfirmPassword();

    /**
     * Sets the fields of this Password to those of the provided Password.
     *
     * @param o the other Password to fetch fields from
     */
    default void setToIPassword(IPassword o) {
        setPassword(o.getPassword());
        setConfirmPassword(o.getConfirmPassword());
    }

    /**
     * Returns if the fields of this Password are equal to those of the other Password.
     *
     * @param o the other Password to compare to
     * @return true if the fields of this and the other Password match
     */
    default boolean passwordEquals(IPassword o) {
        return getPassword().equals(o.getPassword()) &&
                getConfirmPassword().equals(o.getConfirmPassword());
    }

}
