package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.Movie;
import com.ecinema.app.domain.enums.MovieCategory;
import com.ecinema.app.domain.enums.MsrbRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * The interface Movie repository.
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * Find by title optional.
     *
     * @param title the title
     * @return the optional
     */
    Optional<Movie> findBySearchTitle(String title);

    /**
     * Exists by title boolean.
     *
     * @param title the title
     * @return the boolean
     */
    boolean existsBySearchTitle(String title);

    /**
     * Find all by like title list.
     *
     * @param title the title
     * @return the list
     */
    List<Movie> findBySearchTitleContaining(String title);

    /**
     * Find all by like title page.
     *
     * @param title    the title
     * @param pageable the pageable
     * @return the page
     */
    Page<Movie> findBySearchTitleContaining(String title, Pageable pageable);

    /**
     * Find all by msrb rating list.
     *
     * @param msrbRating the msrb rating
     * @return the list
     */
    List<Movie> findAllByMsrbRating(MsrbRating msrbRating);

    /**
     * Find all by movies categories contains list.
     *
     * @param movieCategory the movie category
     * @return the list
     */
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.movieCategories mc WHERE mc = ?1")
    List<Movie> findAllByMoviesCategoriesContains(MovieCategory movieCategory);

    /**
     * Find all by movie categories contains list.
     *
     * @param movieCategories the movie categories
     * @return the list
     */
    @Query("SELECT DISTINCT m FROM Movie m JOIN m.movieCategories mc WHERE mc IN ?1")
    List<Movie> findAllByMovieCategoriesContainsSet(Set<MovieCategory> movieCategories);

    /**
     * Find all order by release date ascending list.
     *
     * @return the list
     */
    @Query("SELECT m FROM Movie m ORDER BY m.releaseDate ASC")
    List<Movie> findAllOrderByReleaseDateAscending();

    /**
     * Find all order by release date descending list.
     *
     * @return the list
     */
    @Query("SELECT m FROM Movie m ORDER BY m.releaseDate DESC")
    List<Movie> findAllOrderByReleaseDateDescending();

    /**
     * Find all order by duration ascending list.
     *
     * @return the list
     */
    @Query("SELECT m FROM Movie m ORDER BY m.duration ASC")
    List<Movie> findAllOrderByDurationAscending();

    /**
     * Find all order by duration descending list.
     *
     * @return the list
     */
    @Query("SELECT m FROM Movie m ORDER BY m.duration DESC")
    List<Movie> findAllOrderByDurationDescending();

}
