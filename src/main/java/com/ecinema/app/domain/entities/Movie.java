package com.ecinema.app.domain.entities;

import com.ecinema.app.domain.contracts.IMovie;
import com.ecinema.app.domain.objects.Duration;
import com.ecinema.app.domain.objects.DurationConverter;
import com.ecinema.app.domain.enums.MovieCategory;
import com.ecinema.app.domain.enums.MsrbRating;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * The type Movie.
 */
@Getter
@Setter
@Entity
@ToString
public class Movie extends AbstractEntity implements IMovie {

    @Column
    private String title;

    @Column
    private String searchTitle;

    @Column
    private String director;

    @Column
    private String image;

    @Column
    private String trailer;

    @Column(length = 2000)
    private String synopsis;

    @Column
    @Convert(converter = DurationConverter.class)
    private Duration duration;

    @Column
    private LocalDate releaseDate;

    @Column
    @Enumerated(EnumType.STRING)
    private MsrbRating msrbRating;

    @ElementCollection
    private Set<String> cast = new HashSet<>();

    @ElementCollection
    private Set<String> writers = new HashSet<>();

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<MovieCategory> movieCategories = EnumSet.noneOf(MovieCategory.class);

    @ToString.Exclude
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Review> reviews = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Screening> screenings = new HashSet<>();

    @Override
    public void setCast(Collection<String> cast) {
        this.cast.clear();
        this.cast.addAll(cast);
    }

    @Override
    public void setWriters(Collection<String> writers) {
        this.writers.clear();
        this.writers.addAll(writers);
    }

    @Override
    public void setMovieCategories(Collection<MovieCategory> movieCategories) {
        this.movieCategories.clear();
        this.movieCategories.addAll(movieCategories);
    }

}
