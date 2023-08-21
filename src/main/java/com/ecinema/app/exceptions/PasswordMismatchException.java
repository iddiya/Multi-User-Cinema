package com.ecinema.app.exceptions;

public class PasswordMismatchException extends AbstractRuntimeException {

    public PasswordMismatchException(String username) {
        super("Could not authenticate. Bad password provided for " + username);
    }

}
