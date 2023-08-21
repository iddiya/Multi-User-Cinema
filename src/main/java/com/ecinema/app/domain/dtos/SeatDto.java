package com.ecinema.app.domain.dtos;

import com.ecinema.app.domain.contracts.AbstractDto;
import com.ecinema.app.domain.contracts.ISeat;
import com.ecinema.app.domain.enums.Letter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The type Seat dto.
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SeatDto extends AbstractDto implements ISeat {

    private Letter rowLetter;
    private Integer seatNumber;

    public String seatDesignation() {
        return rowLetter + "" + seatNumber;
    }

}
