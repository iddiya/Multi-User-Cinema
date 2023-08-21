package com.ecinema.app.exceptions;

import java.util.Collection;

public class ExpirationException extends AbstractRuntimeException {

    public ExpirationException(String... errors) {
        super(errors);
    }

    public ExpirationException(Collection<String> errors) {
        super(errors);
    }

}
