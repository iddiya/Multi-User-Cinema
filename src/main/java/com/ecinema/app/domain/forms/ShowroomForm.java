package com.ecinema.app.domain.forms;

import com.ecinema.app.domain.contracts.IShowroom;
import com.ecinema.app.domain.enums.Letter;
import lombok.Data;

import java.io.Serializable;

@Data
public class ShowroomForm implements IShowroom, Serializable {
    private Letter showroomLetter;
    private Integer numberOfRows;
    private Integer numberOfSeatsPerRow;
}
