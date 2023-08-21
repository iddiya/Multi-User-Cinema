package com.ecinema.app.domain.forms;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class GenericListForm<T> implements Serializable {
    private List<T> list = new ArrayList<>();
}
