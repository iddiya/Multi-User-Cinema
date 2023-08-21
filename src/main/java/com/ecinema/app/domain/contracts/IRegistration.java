package com.ecinema.app.domain.contracts;

import com.ecinema.app.domain.enums.UserAuthority;

import java.util.Set;

/**
 * Defines the contract of the registration.
 */
public interface IRegistration extends IProfile, IPassword {

    /**
     * Returns if the password is encoded.
     *
     * @return true if the password is encoded, else false
     */
    Boolean getIsPasswordEncoded();

    /**
     * Sets if the password is encoded.
     *
     * @param isPasswordEncoded if the password is encoded
     */
    void setIsPasswordEncoded(Boolean isPasswordEncoded);

    /**
     * Gets if the security answer 1 is encoded.
     *
     * @return if the security answer 1 is encoded
     */
    Boolean getIsSecurityAnswer1Encoded();

    /**
     * Sets if the security answer 1 is encoded.
     *
     * @param isSecurityAnswer1Encoded if the security answer 1 is encoded
     */
    void setIsSecurityAnswer1Encoded(Boolean isSecurityAnswer1Encoded);

    /**
     * Gets if the security answer 2 is encoded.
     *
     * @return if the security answer 2 is encoded
     */
    Boolean getIsSecurityAnswer2Encoded();

    /**
     * Sets if the security answer 2 is encoded.
     *
     * @param isSecurityAnswer2Encoded if the security answer 2 is encoded
     */
    void setIsSecurityAnswer2Encoded(Boolean isSecurityAnswer2Encoded);

    /**
     * Sets the username.
     *
     * @param username the username
     */
    void setUsername(String username);

    /**
     * Gets the username.
     *
     * @return the username
     */
    String getUsername();

    /**
     * Sets the email.
     *
     * @param email the email
     */
    void setEmail(String email);

    /**
     * Gets the email.
     *
     * @return the email
     */
    String getEmail();

    /**
     * Gets the first security question.
     *
     * @return the first security question
     */
    String getSecurityQuestion1();

    /**
     * Sets the first security question.
     *
     * @param sq1 the first security question
     */
    void setSecurityQuestion1(String sq1);

    /**
     * Gets the first security answer.
     *
     * @return the first security answer
     */
    String getSecurityAnswer1();

    /**
     * Sets the first security answer.
     *
     * @param sa1 the first security answer
     */
    void setSecurityAnswer1(String sa1);

    /**
     * Gets the second security question.
     *
     * @return the second security question
     */
    String getSecurityQuestion2();

    /**
     * Sets the second security question.
     *
     * @param sq2 the second security question
     */
    void setSecurityQuestion2(String sq2);

    /**
     * Gets the second security answer.
     *
     * @return the second security answer
     */
    String getSecurityAnswer2();

    /**
     * Sets the second security answer.
     *
     * @param sa2 the second security answer
     */
    void setSecurityAnswer2(String sa2);

    /**
     * Gets the user authorities.
     *
     * @return the user authorities
     */
    Set<UserAuthority> getAuthorities();

    /**
     * Sets the user authorities.
     *
     * @param userAuthorities the user authorities
     */
    void setAuthorities(Set<UserAuthority> userAuthorities);

    /**
     * Sets the fields of this Registration to that of the provided Registration.
     *
     * @param o the other Registration to fetch fields from
     */
    default void setToIRegistration(IRegistration o) {
        setToIProfile(o);
        setToIPassword(o);
        setEmail(o.getEmail());
        setUsername(o.getUsername());
        setAuthorities(o.getAuthorities());
        setSecurityAnswer1(o.getSecurityAnswer1());
        setSecurityAnswer2(o.getSecurityAnswer2());
        setSecurityQuestion1(o.getSecurityQuestion1());
        setSecurityQuestion2(o.getSecurityQuestion2());
        setIsPasswordEncoded(o.getIsPasswordEncoded());
        setIsSecurityAnswer1Encoded(o.getIsSecurityAnswer1Encoded());
        setIsSecurityAnswer2Encoded(o.getIsSecurityAnswer2Encoded());
    }

    /**
     * Returns if the fields of this Registration are equal to those of the provided Registration.
     * The comparison includes the boolean methods that check if certain values are encoded.
     * If both this Registration and the provided Registration have {@link #getPassword()} equal to
     * "password123" but they differ in the value of {@link #getIsPasswordEncoded()}, then this
     * method will return false.
     *
     * @param o the other Registration to compare to
     * @return true if the fields of this and the other Registration match
     */
    default boolean registrationEquals(IRegistration o) {
        return profileEquals(o) &&
                passwordEquals(o) &&
                getEmail().equals(o.getEmail()) &&
                getUsername().equals(o.getUsername()) &&
                getAuthorities().equals(o.getAuthorities()) &&
                getSecurityAnswer1().equals(o.getSecurityAnswer1()) &&
                getSecurityAnswer2().equals(o.getSecurityAnswer2()) &&
                getSecurityQuestion1().equals(o.getSecurityQuestion1()) &&
                getSecurityQuestion2().equals(o.getSecurityQuestion2()) &&
                getIsPasswordEncoded().equals(o.getIsPasswordEncoded()) &&
                getIsSecurityAnswer1Encoded().equals(o.getIsSecurityAnswer1Encoded()) &&
                getIsSecurityAnswer2Encoded().equals(o.getIsSecurityAnswer2Encoded());
    }

}
