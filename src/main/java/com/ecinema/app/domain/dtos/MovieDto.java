package com.ecinema.app.domain.dtos;

import com.ecinema.app.domain.contracts.AbstractDto;
import com.ecinema.app.domain.contracts.IMovie;
import com.ecinema.app.domain.objects.Duration;
import com.ecinema.app.domain.enums.MovieCategory;
import com.ecinema.app.domain.enums.MsrbRating;
import lombok.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * {@link AbstractDto} implementation for {@link com.ecinema.app.domain.entities.Movie}.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MovieDto extends AbstractDto implements IMovie {
    private String title = "";
    private String director = "";
    private String image = "";
    private String trailer = "";
    private String synopsis = "";
    private Duration duration = Duration.zero();
    private LocalDate releaseDate = LocalDate.now();
    private MsrbRating msrbRating = MsrbRating.G;
    private Set<String> cast = new HashSet<>();
    private Set<String> writers = new HashSet<>();
    private Set<MovieCategory> movieCategories =
            EnumSet.noneOf(MovieCategory.class);
}
