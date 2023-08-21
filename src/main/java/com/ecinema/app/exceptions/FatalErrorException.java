package com.ecinema.app.exceptions;

import java.util.Collection;

/**
 * The type Fatal error exception.
 */
public class FatalErrorException extends AbstractRuntimeException {

    /**
     * Instantiates a new Fatal error exception.
     *
     * @param errors the errors
     */
    public FatalErrorException(String... errors) {
        super(errors);
    }

    /**
     * Instantiates a new Fatal error exception.
     *
     * @param errors the errors
     */
    public FatalErrorException(Collection<String> errors) {
        super(errors);
    }

}
