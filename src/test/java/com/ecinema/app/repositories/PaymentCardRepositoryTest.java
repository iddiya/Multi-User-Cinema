package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.Customer;
import com.ecinema.app.domain.entities.PaymentCard;
import com.ecinema.app.domain.entities.User;
import com.ecinema.app.domain.enums.UserAuthority;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PaymentCardRepositoryTest {

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        for (PaymentCard paymentCard : paymentCardRepository.findAll()) {
            paymentCard.getCardOwner().getPaymentCards().remove(paymentCard);
            paymentCard.setCardOwner(null);
        }
        paymentCardRepository.deleteAll();
    }

    @Test
    void findAllByCustomer() {
        // given
        User user = new User();
        userRepository.save(user);
        Customer customer = new Customer();
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
        customer.setUser(user);
        customerRepository.save(customer);
        PaymentCard paymentCard = new PaymentCard();
        customer.getPaymentCards().add(paymentCard);
        paymentCard.setCardOwner(customer);
        paymentCardRepository.save(paymentCard);
        // when
        List<PaymentCard> paymentCards = paymentCardRepository
                .findDistinctByCardUserWithId(user.getId());
        // then
        assertEquals(1, paymentCards.size());
        assertEquals(paymentCard, paymentCards.get(0));
    }

    @Test
    void findAllByCustomerWithId() {
        // given
        Customer customer = new Customer();
        customerRepository.save(customer);
        PaymentCard paymentCard = new PaymentCard();
        customer.getPaymentCards().add(paymentCard);
        paymentCard.setCardOwner(customer);
        paymentCardRepository.save(paymentCard);
        // when
        List<PaymentCard> paymentCards = paymentCardRepository
                .findDistinctByCardCustomerWithId(customer.getId());
        // then
        assertEquals(1, paymentCards.size());
        assertEquals(paymentCard, paymentCards.get(0));
    }

    @Test
    void isPaymentCardOwned() {
        // given
        User user1 = new User();
        userRepository.save(user1);
        Customer customer1 = new Customer();
        customer1.setUser(user1);
        user1.getUserAuthorities().put(UserAuthority.CUSTOMER, customer1);
        customerRepository.save(customer1);
        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardOwner(customer1);
        customer1.getPaymentCards().add(paymentCard);
        paymentCardRepository.save(paymentCard);
        User user2 = new User();
        userRepository.save(user2);
        Customer customer2 = new Customer();
        customer2.setUser(user2);
        user2.getUserAuthorities().put(UserAuthority.CUSTOMER, customer2);
        customerRepository.save(customer2);
        // when
        boolean isTrue1 = paymentCardRepository.isPaymentCardOwnedByCustomer(
                paymentCard.getId(), customer1.getId());
        boolean isTrue2 = paymentCardRepository.isPaymentCardOwnedByUser(
                paymentCard.getId(), user1.getId());
        boolean isFalse1 = paymentCardRepository.isPaymentCardOwnedByCustomer(
                paymentCard.getId(), customer2.getId());
        boolean isFalse2 = paymentCardRepository.isPaymentCardOwnedByUser(
                paymentCard.getId(), user2.getId());
        // then
        assertTrue(isTrue1);
        assertTrue(isTrue2);
        assertFalse(isFalse1);
        assertFalse(isFalse2);
    }

}