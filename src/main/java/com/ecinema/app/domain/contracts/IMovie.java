package com.ecinema.app.domain.contracts;

import com.ecinema.app.domain.enums.MovieCategory;
import com.ecinema.app.domain.enums.MsrbRating;
import com.ecinema.app.domain.objects.Duration;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.List;

/**
 * The interface Movie.
 */
public interface IMovie {

    /**
     * Gets title.
     *
     * @return the title
     */
    String getTitle();

    /**
     * Sets title.
     *
     * @param title the title
     */
    void setTitle(String title);

    /**
     * Gets director.
     *
     * @return the director
     */
    String getDirector();

    /**
     * Sets director.
     *
     * @param director the director
     */
    void setDirector(String director);

    /**
     * Gets image.
     *
     * @return the image
     */
    String getImage();

    /**
     * Sets image.
     *
     * @param image the image
     */
    void setImage(String image);

    /**
     * Gets trailer.
     *
     * @return the trailer
     */
    String getTrailer();

    /**
     * Sets trailer.
     *
     * @param trailer the trailer
     */
    void setTrailer(String trailer);

    /**
     * Gets synopsis.
     *
     * @return the synopsis
     */
    String getSynopsis();

    /**
     * Sets synopsis.
     *
     * @param synopsis the synopsis
     */
    void setSynopsis(String synopsis);

    /**
     * Gets duration.
     *
     * @return the duration
     */
    Duration getDuration();

    /**
     * Sets duration.
     *
     * @param duration the duration
     */
    default void setDuration(Duration duration) {
        getDuration().set(duration);
    }

    /**
     * Gets release date.
     *
     * @return the release date
     */
    LocalDate getReleaseDate();

    /**
     * Sets release date.
     *
     * @param releaseDate the release date
     */
    void setReleaseDate(LocalDate releaseDate);

    /**
     * Gets msrb rating.
     *
     * @return the msrb rating
     */
    MsrbRating getMsrbRating();

    /**
     * Sets msrb rating.
     *
     * @param msrbRating the msrb rating
     */
    void setMsrbRating(MsrbRating msrbRating);

    /**
     * Gets cast.
     *
     * @return the cast
     */
    Collection<String> getCast();

    /**
     * Sets cast. Default implementation provided.
     *
     * @param cast the cast
     */
    default void setCast(Collection<String> cast) {
        getCast().clear();
        getCast().addAll(cast);
    }

    /**
     * Gets writers.
     *
     * @return the writers
     */
    Collection<String> getWriters();

    /**
     * Sets writers. Default implementation provided.
     *
     * @param writers the writers
     */
    default void setWriters(Collection<String> writers) {
        getWriters().clear();
        getWriters().addAll(writers);
    }

    /**
     * Gets the movie categories.
     *
     * @return the movie categories
     */
    Collection<MovieCategory> getMovieCategories();

    /**
     * Sets movie categories. Default implementation provided.
     *
     * @param movieCategories the movie categories
     */
    default void setMovieCategories(Collection<MovieCategory> movieCategories) {
        getMovieCategories().clear();
        getMovieCategories().addAll(movieCategories);
    }

    /**
     * Sets the values of this IMovie to those of the provided IMovie.
     *
     * @param movie the movie whose values are to be copied
     */
    default void setToIMovie(IMovie movie) {
        setTitle(movie.getTitle());
        setDirector(movie.getDirector());
        setImage(movie.getImage());
        setTrailer(movie.getTrailer());
        setSynopsis(movie.getSynopsis());
        setDuration(movie.getDuration());
        setMsrbRating(movie.getMsrbRating());
        setReleaseDate(movie.getReleaseDate());
        setCast(movie.getCast());
        setWriters(movie.getWriters());
        setMovieCategories(movie.getMovieCategories());
    }

}
