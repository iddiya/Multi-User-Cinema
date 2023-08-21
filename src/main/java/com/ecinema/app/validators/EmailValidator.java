package com.ecinema.app.validators;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.regex.Pattern;

@Component
public class EmailValidator implements AbstractValidator<String> {

    private static final String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9" +
            "-]+(\\" +
            ".[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    @Override
    public void validate(String email, Collection<String> errors) {
        if (!isValidEmail(email)) {
            errors.add("Email fails regex pattern test");
        }
    }

    public boolean isValidEmail(String email) {
        return Pattern.compile(regexPattern).matcher(email).matches();
    }

}
