package com.ecinema.app.exceptions;

import java.util.Collection;

/**
 * The type Invalid action exception.
 */
public class InvalidActionException extends AbstractRuntimeException {

    /**
     * Instantiates a new Invalid action exception.
     *
     * @param errors the errors
     */
    public InvalidActionException(String... errors) {
        super(errors);
    }

    /**
     * Instantiates a new Invalid action exception.
     *
     * @param errors the errors
     */
    public InvalidActionException(Collection<String> errors) {
        super(errors);
    }

}
