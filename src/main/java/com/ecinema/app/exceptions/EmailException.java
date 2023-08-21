package com.ecinema.app.exceptions;

import java.util.Collection;

public class EmailException extends AbstractRuntimeException {

    public EmailException(Collection<String> errors) {
        super(errors);
    }

    public EmailException(String... errors) {
        super(errors);
    }

}
