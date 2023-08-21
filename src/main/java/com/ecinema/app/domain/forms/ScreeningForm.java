package com.ecinema.app.domain.forms;

import com.ecinema.app.domain.contracts.IScreening;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class ScreeningForm implements IScreening, Serializable {

    private Long movieId = 0L;
    private Long showroomId = 0L;
    private Integer showtimeHour = 0;
    private Integer showtimeMinute = 0;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate showdate = LocalDate.now();

    @Override
    public LocalDateTime getShowDateTime() {
        return LocalDateTime.of(showdate, LocalTime.of(
                showtimeHour, showtimeMinute));
    }

    @Override
    public void setShowDateTime(LocalDateTime showDateTime) {
        setShowtimeHour(showDateTime.getHour());
        setShowtimeMinute(showDateTime.getMinute());
        setShowdate(showDateTime.toLocalDate());
    }

}
