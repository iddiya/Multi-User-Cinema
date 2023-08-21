package com.ecinema.app.exceptions;

import java.util.Collection;

public class InvalidDurationException extends AbstractRuntimeException {

    public InvalidDurationException(String... errors) {
        super(errors);
    }

    public InvalidDurationException(Collection<String> errors) {
        super(errors);
    }

}
