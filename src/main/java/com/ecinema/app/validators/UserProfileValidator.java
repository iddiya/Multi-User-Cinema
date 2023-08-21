package com.ecinema.app.validators;

import com.ecinema.app.domain.contracts.IProfile;
import com.ecinema.app.util.UtilMethods;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;

/**
 * The type Profile validator.
 */
@Component
public class UserProfileValidator implements AbstractValidator<IProfile> {

    @Override
    public void validate(IProfile iProfile, Collection<String> errors) {
        if (iProfile.getFirstName().isBlank()) {
            errors.add("First name is blank");
        } else if (!UtilMethods.isAlphabeticalOnly(iProfile.getFirstName())) {
            errors.add("First name must contain only alphabetical characters");
        }
        if (iProfile.getLastName().isBlank()) {
            errors.add("Last name is blank");
        } else if (!UtilMethods.isAlphabeticalOnly(iProfile.getLastName())) {
            errors.add("Last name must contain only alphabetical characters");
        }
        if (iProfile.getBirthDate().isAfter(LocalDate.now().minusYears(18))) {
            errors.add("Must be 18 years or older to be able to register");
        }
    }

}
