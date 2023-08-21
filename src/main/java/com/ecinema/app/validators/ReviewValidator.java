package com.ecinema.app.validators;

import com.ecinema.app.domain.contracts.IReview;
import com.ecinema.app.util.UtilMethods;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ReviewValidator implements AbstractValidator<IReview> {

    public static final Integer MIN_NON_BLANK_CHARS = 25;
    public static final Integer MAX_CHARS = 2000;
    public static final Integer MAX_RATING = 10;
    public static final Integer MIN_RATING = 0;

    @Override
    public void validate(IReview iReview, Collection<String> errors) {
        if (iReview.getReview().isBlank()) {
            errors.add("review cannot be blank");
        }
        if (UtilMethods.removeWhitespace(iReview.getReview()).length() < MIN_NON_BLANK_CHARS) {
            errors.add("review must have at least 25 non-blank chars");
        }
        if (iReview.getReview().length() > MAX_CHARS) {
            errors.add("review cannot exceed 2000 chars");
        }
        if (iReview.getRating() > MAX_RATING) {
            errors.add("rating cannot exceed 10 stars");
        }
        if (iReview.getRating() < MIN_RATING) {
            errors.add("rating cannot be less than 0 stars");
        }
    }

}
