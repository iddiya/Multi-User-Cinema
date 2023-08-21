package com.ecinema.app.exceptions;

public class NoFieldFoundException extends AbstractRuntimeException {

    public static final String ERROR = "No %s associated with %s";

    public NoFieldFoundException(String field, String object) {
        super(String.format(ERROR, field, object));
    }

}
