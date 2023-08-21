package com.ecinema.app.domain.forms;

import com.ecinema.app.domain.enums.TicketType;
import lombok.Data;

import java.io.Serializable;

@Data
public class SeatBookingForm implements Serializable {
    private Long userId;
    private Long paymentCardId;
    private Long screeningSeatId;
    private TicketType ticketType;
    private Integer tokensToApply;
}
