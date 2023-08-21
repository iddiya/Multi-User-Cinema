package com.ecinema.app.domain.dtos;

import com.ecinema.app.domain.enums.Letter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The type Showroom seat dto.
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ShowroomSeatDto extends SeatDto {
    private Long showroomId;
    private Letter showroomLetter;
}
