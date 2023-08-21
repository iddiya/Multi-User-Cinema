package com.ecinema.app.exceptions;

import com.ecinema.app.exceptions.AbstractRuntimeException;
import com.ecinema.app.services.PaymentCardService;

public class TooManyPaymentCardsException extends AbstractRuntimeException {

    public TooManyPaymentCardsException(int numberOfPaymentCards) {
        super("Customer cannot have more than " + PaymentCardService.MAX_PAYMENT_CARDS_PER_CUSTOMER +
                " payment cards. You currently have " + numberOfPaymentCards + " payment cards");
    }

}
