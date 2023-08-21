package com.ecinema.app.domain.contracts;

import com.ecinema.app.domain.enums.PaymentCardType;

import java.time.LocalDate;

/**
 * The contract for the Payment card.
 */
public interface IPaymentCard {

    /**
     * Gets payment card type.
     *
     * @return the payment card type
     */
    PaymentCardType getPaymentCardType();

    /**
     * Sets payment card type.
     *
     * @param paymentCardType the payment card type
     */
    void setPaymentCardType(PaymentCardType paymentCardType);

    /**
     * Gets card number.
     *
     * @return the card number
     */
    String getCardNumber();

    /**
     * Sets card number.
     *
     * @param cardNumber the card number
     */
    void setCardNumber(String cardNumber);

    /**
     * Gets first name.
     *
     * @return the first name
     */
    String getFirstName();

    /**
     * Sets first name.
     *
     * @param firstName the first name
     */
    void setFirstName(String firstName);

    /**
     * Gets last name.
     *
     * @return the last name
     */
    String getLastName();

    /**
     * Sets last name.
     *
     * @param lastName the last name
     */
    void setLastName(String lastName);

    /**
     * Gets expiration date.
     *
     * @return the expiration date
     */
    LocalDate getExpirationDate();

    /**
     * Sets expiration date.
     *
     * @param expirationDate the expiration date
     */
    void setExpirationDate(LocalDate expirationDate);

    /**
     * Gets billing address.
     *
     * @return the billing address
     */
    IAddress getBillingAddress();

    /**
     * Sets billing address.
     *
     * @param billingAddress the billing address
     */
    void setBillingAddress(IAddress billingAddress);

    /**
     * Sets to i payment card.
     *
     * @param o             the o
     * @param setCardNumber the set card number
     */
    default void setToIPaymentCard(IPaymentCard o, boolean setCardNumber) {
        setPaymentCardType(o.getPaymentCardType());
        setCardNumber(setCardNumber ? o.getCardNumber() : "");
        setFirstName(o.getFirstName());
        setLastName(o.getLastName());
        setExpirationDate(o.getExpirationDate());
        setBillingAddress(o.getBillingAddress());
    }

}
