package com.ecinema.app.exceptions;

import java.util.Collection;

/**
 * The type Null runtime var exception.
 */
public class BadRuntimeVarException extends AbstractRuntimeException {

    /**
     * Instantiates a new Null runtime var exception.
     *
     * @param errors the errors
     */
    public BadRuntimeVarException(String... errors) {
        super(errors);
    }

    /**
     * Instantiates a new Null runtime var exception.
     *
     * @param errors the errors
     */
    public BadRuntimeVarException(Collection<String> errors) {
        super(errors);
    }

}
