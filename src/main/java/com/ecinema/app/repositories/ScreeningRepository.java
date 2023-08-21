package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.Movie;
import com.ecinema.app.domain.entities.Screening;
import com.ecinema.app.domain.entities.Showroom;
import com.ecinema.app.domain.enums.Letter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The jpa repository for {@link Screening}.
 */
@Repository
public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    /**
     * Find {@link Page} of movies where {@link Screening#getId()} equals movieId.
     *
     * @param movieId  the movie id
     * @param pageable the pageable
     * @return the page of screenings
     */
    @Query("SELECT s FROM Screening s WHERE s.movie.id = ?1")
    Page<Screening> findAllByMovieId(Long movieId, Pageable pageable);

    /**
     * Find all by show date time less than or equal to the provided {@link java.time.LocalDateTime}.
     *
     * @param localDateTime the local date time
     * @return the list of screenings
     */
    @Query("SELECT s FROM Screening s WHERE s.showDateTime <= ?1 ORDER BY s.showDateTime ASC")
    List<Screening> findAllByShowDateTimeLessThanEqual(LocalDateTime localDateTime);

    /**
     * Find all by show date time greater than or equal to the provided {@link LocalDateTime}.
     *
     * @param localDateTime the local date time
     * @return the list of screenings
     */
    @Query("SELECT s FROM Screening s WHERE s.showDateTime >= ?1 ORDER BY s.showDateTime ASC")
    List<Screening> findAllByShowDateTimeGreaterThanEqual(LocalDateTime localDateTime);

    /**
     * Find all by show date time between the two provided {@link LocalDateTime} values.
     *
     * @param l1 the lesser show date time
     * @param l2 the greater show date time
     * @return the list of screenings
     */
    @Query("SELECT s FROM Screening s WHERE s.showDateTime >= ?1 AND s.showDateTime <= ?2 ORDER BY s.showDateTime ASC")
    List<Screening> findAllByShowDateTimeBetween(LocalDateTime l1, LocalDateTime l2);

    /**
     * Find all where {@link Screening#getMovie()} equals the provided {@link Movie}.
     *
     * @param movie the movie
     * @return the list of screenings
     */
    List<Screening> findAllByMovie(Movie movie);

    /**
     * Find all where {@link Showroom#getShowroomLetter()} of {@link Screening#getShowroom()} is contained
     * in the provided list of {@link Letter}.
     *
     * @param showroomLetters the showroom letters
     * @param pageable        the {@link Pageable}
     * @return the page of screenings
     */
    @Query("SELECT s FROM Screening s WHERE s.showroom.showroomLetter IN (?1)")
    Page<Screening> findAllByShowroomLetters(List<Letter> showroomLetters, Pageable pageable);

    /**
     * Find all where {@link Movie#getSearchTitle()} of {@link Screening#getMovie()} is like the provided String.
     *
     * @param title    the title to compare to
     * @param pageable the {@link Pageable}
     * @return the page of screenings
     */
    @Query("SELECT s FROM Screening s WHERE TRIM(UPPER(s.movie.searchTitle)) LIKE TRIM(UPPER(CONCAT('%', ?1, '%')))")
    Page<Screening> findAllByMovieWithTitleLike(String title, Pageable pageable);

    /**
     * Find all where {@link Showroom#getShowroomLetter()} is contained in the provided list of {@link Letter}
     * and where {@link Movie#getTitle()} of {@link Screening#getMovie()} is like the provided String.
     *
     * @param showroomLetters the showroom letters
     * @param title           the title to compare to
     * @param pageable        the {@link Pageable}
     * @return the page of screenings
     */
    @Query("SELECT s FROM Screening s WHERE s.showroom.showroomLetter IN (?1) " +
            "AND TRIM(UPPER(s.movie.searchTitle)) LIKE TRIM(UPPER(CONCAT('%', ?2, '%')))")
    Page<Screening> findAllByShowroomLettersAndMovieWithTitleLike(
            List<Letter> showroomLetters, String title, Pageable pageable);

    /**
     * Find all where {@link Movie#getId()} of {@link Screening#getMovie()} equals the provided Long.
     *
     * @param movieId the movie id
     * @return the list of screenings
     */
    @Query("SELECT s FROM Screening s JOIN s.movie m WHERE m.id = ?1")
    List<Screening> findAllByMovieWithId(Long movieId);

    /**
     * Find all where {@link Screening#getShowroom()} equals the provided {@link Showroom}.
     *
     * @param showroom the showroom
     * @return the list of screenings
     */
    List<Screening> findAllByShowroom(Showroom showroom);

    /**
     * Find all where {@link Showroom#getId()} of {@link Screening#getShowroom()} equals the provided Long.
     *
     * @param showroomId the showroom id
     * @return the list of screenings
     */
    @Query("SELECT s FROM Screening s JOIN s.showroom sh WHERE sh.id = ?1")
    List<Screening> findAllByShowroomWithId(Long showroomId);

    /**
     * Find all screening ids by movie id list.
     *
     * @param movieId the movie id
     * @return the list
     */
    @Query("SELECT s.id FROM Screening s where s.movie.id = ?1")
    List<Long> findAllScreeningIdsByMovieId(Long movieId);

}
