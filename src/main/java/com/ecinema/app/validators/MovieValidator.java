package com.ecinema.app.validators;

import com.ecinema.app.domain.contracts.IMovie;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * The type Movie validator.
 */
@Component
public class MovieValidator implements AbstractValidator<IMovie> {

    @Override
    public void validate(IMovie iMovie, Collection<String> errors) {
        if (iMovie.getTitle().isBlank()) {
            errors.add("title cannot be blank");
        }
        if (iMovie.getDirector().isBlank()) {
            errors.add("director cannot be blank");
        }
        if (iMovie.getImage().isBlank()) {
            errors.add("image cannot be blank");
        }
        if (iMovie.getTrailer().isBlank()) {
            errors.add("trailer cannot be blank");
        }
        if (iMovie.getSynopsis().isBlank()) {
            errors.add("synopsis cannot be blank");
        }
        if (iMovie.getMsrbRating() == null) {
            errors.add("msrb rating cannot be null");
        }
        if (iMovie.getCast().isEmpty()) {
            errors.add("cast cannot be empty");
        }
        if (iMovie.getWriters().isEmpty()) {
            errors.add("writers cannot be empty");
        }
        if (iMovie.getMovieCategories().isEmpty()) {
            errors.add("movie categories cannot be empty");
        }
    }

}
