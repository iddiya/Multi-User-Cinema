package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The interface Customer role def repository.
 */
@Repository
public interface CustomerRepository extends UserAuthorityRepository<Customer> {

    /**
     * Find all by payment cards contains optional.
     *
     * @param paymentCard the payment card
     * @return the optional
     */
    @Query("SELECT c FROM Customer c JOIN c.paymentCards p WHERE p = ?1")
    Optional<Customer> findByPaymentCardsContains(PaymentCard paymentCard);

    /**
     * Find all by payment cards contains with id optional.
     *
     * @param paymentCardId the payment card id
     * @return the optional
     */
    @Query("SELECT c FROM Customer c JOIN c.paymentCards p WHERE p.id = ?1")
    Optional<Customer> findByPaymentCardsContainsWithId(Long paymentCardId);

    /**
     * Find all by tickets contains optional.
     *
     * @param ticket the ticket
     * @return the optional
     */
    @Query("SELECT c FROM Customer c JOIN c.tickets t WHERE t = ?1")
    Optional<Customer> findByTicketsContains(Ticket ticket);

    /**
     * Find all by tickets contains with id optional.
     *
     * @param ticketId the ticket id
     * @return the optional
     */
    @Query("SELECT c FROM Customer c JOIN c.tickets t WHERE t.id = ?1")
    Optional<Customer> findByTicketsContainsWithId(Long ticketId);

    /**
     * Find all by reviews contains optional.
     *
     * @param review the review
     * @return the optional
     */
    @Query("SELECT c FROM Customer c JOIN c.reviews r WHERE r = ?1")
    Optional<Customer> findByReviewsContains(Review review);

    /**
     * Find all by reviews contains with id optional.
     *
     * @param reviewId the review id
     * @return the optional
     */
    @Query("SELECT c FROM Customer c JOIN c.reviews r WHERE r.id = ?1")
    Optional<Customer> findByReviewsContainsWithId(Long reviewId);

    /**
     * Find all by censored by not null list.
     *
     * @return the list
     */
    List<Customer> findAllByCensoredByNotNull();

    /**
     * Find all by censored by null list.
     *
     * @return the list
     */
    List<Customer> findAllByCensoredByNull();

    /**
     * Number of tokens owned by customer optional.
     *
     * @param userId the user id
     * @return the optional
     */
    @Query("SELECT c.tokens FROM Customer c WHERE c.user.id = ?1")
    Optional<Integer> numberOfTokensOwnedByUserId(Long userId);

    /**
     * Exists by user id boolean.
     *
     * @param userId the user id
     * @return the boolean
     */
    @Query("SELECT CASE WHEN count(c) > 0 THEN true ELSE false END " +
            "FROM Customer c WHERE c.user.id = ?1")
    boolean existsByUserWithId(Long userId);

}
