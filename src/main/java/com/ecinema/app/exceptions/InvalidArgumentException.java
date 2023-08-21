package com.ecinema.app.exceptions;

import java.util.Collection;

public class InvalidArgumentException extends AbstractRuntimeException {

    public InvalidArgumentException(String... errors) {
        super(errors);
    }

    public InvalidArgumentException(Collection<String> errors) {
        super(errors);
    }

}
