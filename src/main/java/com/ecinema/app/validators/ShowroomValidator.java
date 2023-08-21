package com.ecinema.app.validators;

import com.ecinema.app.domain.contracts.IShowroom;
import com.ecinema.app.domain.enums.Letter;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ShowroomValidator implements AbstractValidator<IShowroom> {

    @Override
    public void validate(IShowroom showroom, Collection<String> errors) {
        if (showroom.getShowroomLetter() == null) {
            errors.add("showroom letter cannot be null");
        }
        if (showroom.getNumberOfRows() <= 0) {
            errors.add("number of rows cannot be zero");
        }
        if (showroom.getNumberOfRows() > Letter.values().length) {
            errors.add("number of rows cannot exceed 26");
        }
        if (showroom.getNumberOfSeatsPerRow() <= 0) {
            errors.add("number of seats per row cannot be zero");
        }
        if (showroom.getNumberOfSeatsPerRow() > 50) {
            errors.add("number of seats per row cannot exceed 50");
        }
    }

}
