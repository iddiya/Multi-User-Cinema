package com.ecinema.app.validators;

import com.ecinema.app.domain.contracts.IAddress;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class AddressValidator implements AbstractValidator<IAddress> {

    @Override
    public void validate(IAddress address, Collection<String> errors) {
        if (address.getStreet().isBlank()) {
            errors.add("Street cannot be blank");
        }
        if (address.getUsState() == null) {
            errors.add("US State cannot be blank");
        }
        if (address.getCity().isBlank()) {
            errors.add("City cannot be blank");
        }
        if (address.getZipcode().isBlank()) {
            errors.add("Zipcode cannot be blank");
        }
        for (char c : address.getZipcode().toCharArray()) {
            if (!Character.isDigit(c)) {
                errors.add("Zipcode must be only digits");
                break;
            }
        }
    }

}
