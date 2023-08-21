package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {

    @Query("SELECT p FROM PaymentCard p JOIN p.cardOwner c WHERE c.id = ?1")
    List<PaymentCard> findDistinctByCardCustomerWithId(Long customerId);

    @Query("SELECT p FROM PaymentCard p JOIN p.cardOwner.user u WHERE u.id = ?1")
    List<PaymentCard> findDistinctByCardUserWithId(Long userId);

    @Query("SELECT p.cardOwner.user.id FROM PaymentCard p WHERE p.id = ?1")
    Optional<Long> findUserIdByPaymentCardWithId(Long paymentCardId);

    @Query("SELECT CASE WHEN count(p) > 0 THEN true ELSE false END " +
            "FROM PaymentCard p WHERE p.id = ?1 AND p.cardOwner.id = ?2")
    boolean isPaymentCardOwnedByCustomer(Long paymentCardId, Long customerId);

    @Query("SELECT CASE WHEN count(p) > 0 THEN true ELSE false END " +
            "FROM PaymentCard p WHERE p.id = ?1 AND p.cardOwner.user.id = ?2")
    boolean isPaymentCardOwnedByUser(Long paymentCardId, Long userId);

}
