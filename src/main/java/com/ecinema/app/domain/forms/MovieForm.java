package com.ecinema.app.domain.forms;

import com.ecinema.app.domain.contracts.IMovie;
import com.ecinema.app.domain.enums.MovieCategory;
import com.ecinema.app.domain.enums.MsrbRating;
import com.ecinema.app.domain.objects.Duration;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class MovieForm implements IMovie, Serializable {
    private Long id = null;
    private String title = "";
    private String director = "";
    private String image = "";
    private String trailer = "";
    private String synopsis = "";
    private Duration duration = Duration.zero();
    private MsrbRating msrbRating = MsrbRating.G;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate = LocalDate.now();
    private List<String> cast = new ArrayList<>();
    private List<String> writers = new ArrayList<>();
    private List<MovieCategory> movieCategories = new ArrayList<>();
}
