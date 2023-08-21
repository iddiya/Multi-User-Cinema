package com.ecinema.app.validators;

import com.ecinema.app.domain.contracts.IRegistration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class RegistrationValidator implements AbstractValidator<IRegistration> {

    private final EmailValidator emailValidator;
    private final UserProfileValidator userProfileValidator;
    private final UsernameValidator usernameValidator;
    private final PasswordValidator passwordValidator;

    @Override
    public void validate(IRegistration registration, Collection<String> errors) {
        usernameValidator.validate(registration.getUsername(), errors);
        emailValidator.validate(registration.getEmail(), errors);
        passwordValidator.validate(registration, errors);
        userProfileValidator.validate(registration, errors);
        if (registration.getSecurityQuestion1().isBlank() || registration.getSecurityAnswer1().isBlank() ||
                registration.getSecurityQuestion2().isBlank() || registration.getSecurityAnswer2().isBlank()) {
            errors.add("No security question or answer can be empty");
        }
    }

}
