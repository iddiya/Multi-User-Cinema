package com.ecinema.app.validators;

import com.ecinema.app.domain.forms.SeatBookingForm;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SeatBookingValidator implements AbstractValidator<SeatBookingForm> {
    @Override
    public void validate(SeatBookingForm seatBookingForm, Collection<String> errors) {
        if (seatBookingForm.getPaymentCardId() == null) {
            errors.add("No payment card selected");
        }
        if (seatBookingForm.getScreeningSeatId() == null) {
            errors.add("No screening seat selected");
        }
        if (seatBookingForm.getTokensToApply() < 0) {
            errors.add("Invalid number of tokens; cannot be less than zero");
        }
        if (seatBookingForm.getUserId() == null) {
            errors.add("Unable to fetch id of user");
        }
        if (seatBookingForm.getTicketType() == null) {
            errors.add("No ticket type selected");
        }
    }
}
