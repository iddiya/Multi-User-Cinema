package com.ecinema.app.domain.contracts;

import com.ecinema.app.util.UtilMethods;

import java.time.LocalDateTime;

/**
 * The interface Screening.
 */
public interface IScreening {

    /**
     * Gets showtime.
     *
     * @return the showtime
     */
    LocalDateTime getShowDateTime();

    /**
     * Sets showtime.
     *
     * @param showtime the showtime
     */
    void setShowDateTime(LocalDateTime showtime);

    /**
     * Showtime formatted string.
     *
     * @return the string
     */
    default String showtimeFormatted() {
       return UtilMethods.localDateTimeFormatted(getShowDateTime());
    }

}
