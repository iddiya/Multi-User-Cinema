package com.ecinema.app.validators;

import com.ecinema.app.domain.contracts.IPaymentCard;
import com.ecinema.app.util.UtilMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class PaymentCardValidator implements AbstractValidator<IPaymentCard> {

    private final AddressValidator addressValidator;

    @Override
    public void validate(IPaymentCard iPaymentCard, Collection<String> errors) {
        addressValidator.validate(iPaymentCard.getBillingAddress(), errors);
        if (iPaymentCard.getCardNumber().length() != 16) {
            errors.add("Payment card number must be 16 digits");
        }
        if (!UtilMethods.isDigitsOnly(iPaymentCard.getCardNumber())) {
            errors.add("Payment card number must be numbers only");
        }
        if (iPaymentCard.getFirstName().isBlank()) {
            errors.add("First name cannot be blank");
        }
        if (iPaymentCard.getLastName().isBlank()) {
            errors.add("Last name cannot be blank");
        }
        if (iPaymentCard.getExpirationDate().isBefore(LocalDate.now())) {
            errors.add("Cannot add already expired payment card");
        }
    }

}
