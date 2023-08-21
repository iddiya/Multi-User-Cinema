package com.ecinema.app.exceptions;

import java.util.Collection;

public class PermissionDeniedException extends AbstractRuntimeException {

    public PermissionDeniedException(String... errors) {
        super(errors);
    }

    public PermissionDeniedException(Collection<String> errors) {
        super(errors);
    }

}
