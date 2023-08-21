package com.ecinema.app.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class AbstractRuntimeException extends RuntimeException {

    private final List<String> errors = new ArrayList<>();

    public AbstractRuntimeException(Collection<String> errors) {
        this.errors.addAll(errors);
    }

    public AbstractRuntimeException(String... errors) {
        this.errors.addAll(Arrays.asList(errors));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String error : errors) {
            sb.append(error).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String getMessage() {
        return toString();
    }

}
