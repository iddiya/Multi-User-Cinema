package com.ecinema.app.exceptions;

import java.util.Collection;

public class NoEntityFoundException extends AbstractRuntimeException {

    public static final String ERROR = "No %s found associated with field %s with value = %s";

    public NoEntityFoundException(String entity, String fieldName, Object value) {
        super(String.format(ERROR, entity, fieldName, value));
    }

    public NoEntityFoundException(Collection<String> errors) {
        super(errors);
    }

    public NoEntityFoundException(String... errors) {
        super(errors);
    }

}
