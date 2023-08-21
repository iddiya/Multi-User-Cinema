package com.ecinema.app.domain.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The type Screening seat dto.
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ScreeningSeatDto extends SeatDto {
    private Long screeningId = 0L;
    private Boolean isBooked = false;
}
