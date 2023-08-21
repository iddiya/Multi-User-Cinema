package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.Customer;
import com.ecinema.app.domain.entities.Review;
import com.ecinema.app.domain.entities.ReviewVote;
import com.ecinema.app.domain.entities.User;
import com.ecinema.app.domain.enums.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The jpa repository for {@link ReviewVote}.
 */
@Repository
public interface ReviewVoteRepository extends JpaRepository<ReviewVote, Long> {

    /**
     * Find all {@link ReviewVote} where the value of {@link Review#getId()} from {@link ReviewVote#getReview()}
     * equal to the provided Long argument.
     *
     * @param reviewID the review id
     * @return the list of ReviewVote
     */
    @Query("SELECT rv FROM ReviewVote rv WHERE rv.review.id = ?1")
    List<ReviewVote> findAllByReviewId(Long reviewID);

    /**
     * Find all {@link ReviewVote} where the value of {@link Customer#getId()} from {@link ReviewVote#getVoter()}
     * equal to the provided Long voter id argument.
     *
     * @param voterId the voter id
     * @return the list of ReviewVote
     */
    @Query("SELECT rv FROM ReviewVote rv WHERE rv.voter.id = ?1")
    List<ReviewVote> findAllByVoterId(Long voterId);

    /**
     * Find all {@link ReviewVote} where the value of {@link User#getId()} from {@link Customer#getUser()} from
     * {@link ReviewVote#getVoter()} equal to the provided Long user id argument.
     *
     * @param userId the user id
     * @return the list of ReviewVote
     */
    @Query("SELECT rv FROM ReviewVote rv WHERE rv.voter.user.id = ?1")
    List<ReviewVote> findAllByUserId(Long userId);

    /**
     * Fina all values of {@link User#getId()} from {@link Customer#getUser()} from {@link ReviewVote#getVoter()}
     * where {@link ReviewVote#getId()} equals the provided Long review id argument and {@link ReviewVote#getVote()}
     * equals the provided {@link Vote} argument.
     *
     * @param reviewId the review id
     * @param vote the vote value
     * @return list of user ids
     */
    @Query("SELECT rv.voter.user.id FROM ReviewVote rv WHERE rv.review.id = ?1 AND rv.vote = ?2")
    List<Long> findAllUserIdsByReviewWithIdAndVote(Long reviewId, Vote vote);

    /**
     * Find {@link ReviewVote} where value of {@link ReviewVote#getVoter()} equals the {@link Customer} argument
     * and where the value of {@link ReviewVote#getReview()} equals the {@link Review} argument.
     *
     * @param customer the Customer
     * @param review the Review
     * @return the optional ReviewVote
     */
    Optional<ReviewVote> findByVoterAndReview(Customer customer, Review review);

    /**
     * Find optional {@link ReviewVote} where {@link User#getId()} from {@link Customer#getUser()} from
     * {@link ReviewVote#getVoter()} equals the provided Long user id argument and where {@link Review#getId()}
     * from {@link ReviewVote#getReview()} equals the provided Long review id argument.
     *
     * @param userId the user id
     * @param reviewId the review id
     * @return the optional ReviewVote
     */
    @Query("SELECT rv FROM ReviewVote rv WHERE rv.voter.user.id = ?1 AND rv.review.id = ?2")
    Optional<ReviewVote> findByUserWithIdAndReviewWithId(Long userId, Long reviewId);

    /**
     * Find the value of {@link User#getId()} from {@link Customer#getUser()} from {@link ReviewVote#getVoter()}
     * where {@link ReviewVote#getId()} equals the provided Long review vote id argument.
     *
     * @param reviewVoteId the ReviewVote id
     * @return the id of the {@link User} associated with the ReviewVote with id equal to the provided Long review
     * vote id argument
     */
    @Query("SELECT rv.voter.user.id FROM ReviewVote rv WHERE rv.id = ?1")
    Optional<Long> findUserIdByReviewVoteWithId(Long reviewVoteId);

    /**
     * Find the value of {@link Customer#getId()} from {@link ReviewVote#getVoter()} where {@link ReviewVote#getId()}
     * equals the provided Long review vote id argument.
     *
     * @param reviewVoteId the ReviewVote id
     * @return the id of the {@link Customer} associated with the ReviewVote with id equal to the provided Long review
     * vote id argument
     */
    @Query("SELECT rv.voter.id FROM ReviewVote rv WHERE rv.id = ?1")
    Optional<Long> findVoterIdByReviewVoteWithId(Long reviewVoteId);

    /**
     * Find the value of {@link Review#getId()} from {@link ReviewVote#getReview()} where {@link ReviewVote#getId()}
     * equals the provided Long review vote id argument.
     *
     * @param reviewVoteId the ReviewVote id
     * @return the id of the {@link Review} associated with the ReviewVote with id equal to the provided Long review
     * vote id argument
     */
    @Query("SELECT rv.review.id FROM ReviewVote rv WHERE rv.id = ?1")
    Optional<Long> findReviewIdByReviewVoteWithId(Long reviewVoteId);

    /**
     * Return if there exists a {@link ReviewVote} where {@link ReviewVote#getVoter()} equals the {@link Customer}
     * argument and {@link ReviewVote#getReview()} equals the {@link Review} argument.
     *
     * @param customer the Customer
     * @param review the Review
     * @return if there exists a ReviewVote matching the predicate
     */
    @Query("SELECT CASE WHEN count(rv) > 0 THEN true ELSE false END " +
            "FROM ReviewVote rv WHERE rv.voter = ?1 AND rv.review = ?2")
    boolean existsByCustomerAndReview(Customer customer, Review review);

    /**
     * Return if there exists a {@link ReviewVote} where {@link User#getId()} from {@link Customer#getUser()} from
     * {@link ReviewVote#getVoter()} equals the Long user id argument and where {@link Review#getId()} from
     * {@link ReviewVote#getReview()} equals the Long review id argument.
     *
     * @param userId the user id
     * @param reviewId the review id
     * @return if there exists a ReviewVote matching the predicate
     */
    @Query("SELECT CASE WHEN count(rv) > 0 THEN true ELSE false END " +
            "FROM ReviewVote rv WHERE rv.voter.user.id = ?1 AND rv.review.id = ?2")
    boolean existsByUserWithIdAndReviewWithId(Long userId, Long reviewId);

}
