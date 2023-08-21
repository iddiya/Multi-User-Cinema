package com.ecinema.app.exceptions;

import java.util.Collection;

public class InvalidAssociationException extends AbstractRuntimeException {

    public InvalidAssociationException(String... errors) {
        super(errors);
    }

    public InvalidAssociationException(Collection<String> errors) {
        super(errors);
    }

    public InvalidAssociationException(Object a, Object b) {
        super("There is no association between " + a.toString() + " and " + b.toString());
    }

}
