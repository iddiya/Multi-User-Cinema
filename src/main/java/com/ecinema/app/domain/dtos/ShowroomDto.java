package com.ecinema.app.domain.dtos;

import com.ecinema.app.domain.contracts.AbstractDto;
import com.ecinema.app.domain.contracts.IShowroom;
import com.ecinema.app.domain.enums.Letter;
import lombok.*;

/**
 * The type Showroom dto.
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ShowroomDto extends AbstractDto implements IShowroom {
    private Letter showroomLetter;
    private Integer numberOfRows;
    private Integer numberOfSeatsPerRow;
}
