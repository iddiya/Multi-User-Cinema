package com.ecinema.app.validators;

import com.ecinema.app.domain.contracts.IPassword;
import com.ecinema.app.util.UtilMethods;
import com.ecinema.app.validators.criteria.PasswordCriteria;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class PasswordValidator implements AbstractValidator<IPassword> {

    @Override
    public void validate(IPassword iPassword, Collection<String> errors) {
        if (iPassword.getPassword().length() < PasswordCriteria.MIN_LENGTH) {
            errors.add("Password length must be at least " + PasswordCriteria.MIN_LENGTH);
        }
        if (UtilMethods.numSpecialChars(iPassword.getPassword()) < PasswordCriteria.MIN_SPECIAL_CHARS) {
            errors.add("Password must contain at least " + PasswordCriteria.MIN_SPECIAL_CHARS +
                               " non-alphanumeric chars (!, ?, &, etc.)");
        }
        if (!iPassword.getPassword().equals(iPassword.getConfirmPassword())) {
            errors.add("Password must equal password confirmation");
        }
    }

}
