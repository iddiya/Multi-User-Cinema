package com.ecinema.app.domain.objects;

import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;

/**
 * Converts {@link Duration} to String representation and vice versa.
 */
@Component
public class DurationConverter implements AttributeConverter<Duration, String> {

    @Override
    public String convertToDatabaseColumn(Duration duration) {
        return duration == null ? null : duration.toString();
    }

    @Override
    public Duration convertToEntityAttribute(String s) {
        return s == null ? null : Duration.strToDuration(s);
    }

}
