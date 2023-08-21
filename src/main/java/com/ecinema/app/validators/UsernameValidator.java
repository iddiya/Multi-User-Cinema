package com.ecinema.app.validators;

import com.ecinema.app.util.UtilMethods;
import com.ecinema.app.validators.criteria.UsernameCriteria;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class UsernameValidator implements AbstractValidator<String> {

    @Override
    public void validate(String username, Collection<String> errors) {
        if (!UtilMethods.isAlphaAndDigitsOnly(username)) {
            errors.add("Username must contain only letters and numbers");
        }
        if (username.length() < UsernameCriteria.MIN_LENGTH || username.length() > UsernameCriteria.MAX_LENGTH) {
            errors.add("Username must be between 6 and 64 characters long.");
        }
    }

}
