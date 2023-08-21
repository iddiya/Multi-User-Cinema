package com.ecinema.app.validators;

import com.ecinema.app.domain.contracts.IScreening;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;

@Component
public class ScreeningValidator implements AbstractValidator<IScreening> {
    
    @Override
    public void validate(IScreening iScreening, Collection<String> errors) {
        if (iScreening.getShowDateTime().getYear() < LocalDate.now().getYear()) {
            errors.add("Showtime year cannot be before current year");
        }
        if (iScreening.getShowDateTime().getMonth() == null) {
            errors.add("Showtime month cannot be null");
        }
        if (iScreening.getShowDateTime().getDayOfMonth() > iScreening.getShowDateTime().getMonth().maxLength()) {
            errors.add("Showtime day cannot exceed max day value of month");
        }
    }
    
}
