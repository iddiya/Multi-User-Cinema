package com.ecinema.app.domain.objects;

import com.ecinema.app.domain.contracts.ISeat;
import com.ecinema.app.domain.enums.Letter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatDesignation implements Serializable {

    private Letter rowLetter;
    private Integer seatNumber;

    public static SeatDesignation of(ISeat seat) {
        return new SeatDesignation(seat.getRowLetter(), seat.getSeatNumber());
    }

    public static SeatDesignation of(Letter rowLetter, Integer seatNumber) {
        return new SeatDesignation(rowLetter, seatNumber);
    }

    @Override
    public String toString() {
        return rowLetter + "" + seatNumber;
    }

}
