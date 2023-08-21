package com.ecinema.app.domain.forms;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class LongListForm implements Serializable {
    private List<Long> list = new ArrayList<>();
}
