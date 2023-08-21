package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.Customer;
import com.ecinema.app.domain.entities.Movie;
import com.ecinema.app.domain.entities.Review;
import com.ecinema.app.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The jpa repository for {@link Review}.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Returns if a {@link Review} exists with {@link User#getId()} from {@link Customer#getUser()} from
     * {@link Review#getWriter()} equal to the provided Long user id argument and with {@link Movie#getId()}
     * from {@link Review#getMovie()} equal to the provided Long movie id argument.
     *
     * @param userId  the user id
     * @param movieId the movie id
     * @return if there exists a Review matching the predicate
     */
    @Query("SELECT CASE WHEN count(r) > 0 THEN true ELSE false END " +
            "FROM Review r WHERE r.writer.user.id = ?1 AND r.movie.id = ?2")
    boolean existsByUserWithIdAndMovieWithId(Long userId, Long movieId);

    /**
     * Returns if a {@link Review} exists with {@link User#getId()} from {@link Customer#getUser()} from
     * {@link Review#getWriter()} equal to the provided Long user id argument and with {@link Review#getId()}
     * equal to the provided Long review id argument.
     *
     * @param userId the user id
     * @param reviewId the review id
     * @return true if there exists a Review matching the predicate
     */
    @Query("SELECT CASE WHEN count(r) > 0 THEN true ELSE false END " +
            "FROM Review r WHERE r.writer.user.id = ?1 AND r.id = ?2")
    boolean existsByUserWithIdAndReviewWithId(Long userId, Long reviewId);

    /**
     * Returns if a {@link Review} exists with {@link Review#getWriter()} equals to the provided {@link Customer}
     * argument and with {@link Review#getMovie()} equal to the provided {@link Movie} argument.
     * to the provided Long writer
     *
     * @param writer the writer
     * @param movie  the movie
     * @return if there exists a Review matching the predicate
     */
    boolean existsByWriterAndMovie(Customer writer, Movie movie);

    /**
     * Find all {@link Review} by {@link Review#getMovie()} equal to the provided {@link Movie} argument.
     *
     * @param movie the movie
     * @return the list of reviews
     */
    List<Review> findAllByMovie(Movie movie);

    /**
     * Find all {@link Review} by {@link Movie#getId()} from {@link Review#getMovie()} equal to the provided
     * Long movie id argument.
     *
     * @param movieId the movie id
     * @return the list of reviews
     */
    @Query("SELECT r FROM Review r JOIN r.movie m WHERE m.id = ?1")
    List<Review> findAllByMovieWithId(Long movieId);

    /**
     * Find page of {@link Review} by {@link Movie#getId()} from {@link Review#getMovie()} equal to the provided
     * Long movie id argument and by {@link Review#getIsCensored()} equal to false.
     *
     * @param movieId the movie id
     * @param pageable the pageable
     * @return page of reviews
     */
    @Query("SELECT r FROM Review r JOIN r.movie m WHERE m.id = ?1 AND r.isCensored = false")
    Page<Review> findAllByMovieWithIdAndNotCensored(Long movieId, Pageable pageable);

    /**
     * Find the average of {@link Review#getRating()} for all {@link Review} where {@link Review#getMovie()} equals
     * the provided {@link Movie} argument.
     *
     * @param movie the movie
     * @return the average review rating
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.movie = ?1")
    Integer findAverageOfReviewRatingsForMovie(Movie movie);

    /**
     * Find the average of {@link Review#getRating()} for all {@link Review} where {@link Movie#getId()} from
     * {@link Review#getMovie()} equals the provided Long movie id argument.
     *
     * @param movieId the movie id
     * @return the average review rating
     */
    @Query("SELECT AVG (r.rating) FROM Review r WHERE r.movie.id = ?1")
    Integer findAverageOfReviewRatingsForMovieWithId(Long movieId);

    /**
     * Finds the value of {@link Customer#getId()} from {@link Review#getWriter()} where {@link Review#getId()}
     * equals the provided Long review id argument.
     *
     * @param reviewId the review id
     * @return the customer id
     */
    @Query("SELECT r.writer.id FROM Review r WHERE r.id = ?1")
    Optional<Long> findCustomerIdByReviewWithId(Long reviewId);

    /**
     * Finds the value of {@link User#getId()} from {@link Customer#getUser()} from {@link Review#getWriter()}
     * where {@link Review#getId()} equals the provided Long review id argument.
     *
     * @param reviewId the review id
     * @return the user id
     */
    @Query("SELECT r.writer.user.id FROM Review r WHERE r.id = ?1")
    Optional<Long> findUserIdByReviewWithId(Long reviewId);

    /**
     * Finds the value of {@link User#getUsername()} from {@link Customer#getUser()} from {@link Review#getWriter()}
     * where {@link Review#getId()} equals the provided Long review id argument.
     *
     * @param reviewId the review id
     * @return the username
     */
    @Query("SELECT r.writer.user.username FROM Review r WHERE r.id = ?1")
    Optional<String> findUsernameOfWriterForReviewWithId(Long reviewId);

}
