package com.ecinema.app.exceptions;

/**
 * The type Duplicate action exception.
 */
public class DuplicateActionException extends AbstractRuntimeException {

    /**
     * Instantiates a new Duplicate action exception.
     *
     * @param action the action
     */
    public DuplicateActionException(String action) {
        super(action + " is a duplicated action");
    }

}
