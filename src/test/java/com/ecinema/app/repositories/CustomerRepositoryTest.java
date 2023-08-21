package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.*;
import com.ecinema.app.domain.enums.UserAuthority;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
    }

    @Test
    void findByReviewsContains() {
        // given
        User user = new User();
        userRepository.save(user);
        Customer customer = new Customer();
        customer.setUser(user);
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
        customerRepository.save(customer);
        Review review = new Review();
        review.setWriter(customer);
        customer.getReviews().add(review);
        reviewRepository.save(review);
        // when
        Optional<Customer> customerAuthorityOptional = customerRepository
                .findByReviewsContains(review);
        // then
        assertTrue(customerAuthorityOptional.isPresent() &&
                customerAuthorityOptional.get().equals(customer) &&
                customerAuthorityOptional.get().getReviews().contains(review));
    }

    @Test
    void findByTicketsContains() {
        // given
        User user = new User();
        userRepository.save(user);
        Customer customer = new Customer();
        customer.setUser(user);
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
        customerRepository.save(customer);
        Ticket ticket = new Ticket();
        ticket.setTicketOwner(customer);
        customer.getTickets().add(ticket);
        ticketRepository.save(ticket);
        // when
        Optional<Customer> customerAuthorityOptional = customerRepository
                .findByTicketsContains(ticket);
        // then
        assertTrue(customerAuthorityOptional.isPresent() &&
                           customerAuthorityOptional.get().equals(customer) &&
                           customerAuthorityOptional.get().getTickets().contains(ticket));
    }

    @Test
    void findByPaymentCardsContains() {
        // given
        User user = new User();
        userRepository.save(user);
        Customer customer = new Customer();
        customer.setUser(user);
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
        customerRepository.save(customer);
        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardOwner(customer);
        customer.getPaymentCards().add(paymentCard);
        paymentCardRepository.save(paymentCard);
        // when
        Optional<Customer> customerAuthorityOptional = customerRepository
                .findByPaymentCardsContains(paymentCard);
        // then
        assertTrue(customerAuthorityOptional.isPresent() &&
                customerAuthorityOptional.get().equals(customer) &&
                customerAuthorityOptional.get().getPaymentCards().contains(paymentCard));
    }

    @Test
    void findIdByUserWithId() {
        // given
        User user = new User();
        userRepository.save(user);
        Customer customer = new Customer();
        customer.setUser(user);
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
        customerRepository.save(customer);
        // when
        Optional<Long> customerRoleDefIdOptional =
                customerRepository.findIdByUserWithId(user.getId());
        Optional<Customer> customerRoleDefOptional =
                customerRepository.findByUser(user);
        // then
        assertTrue(customerRoleDefIdOptional.isPresent());
        assertTrue(customerRoleDefOptional.isPresent());
        assertEquals(customerRoleDefOptional.get().getId(),
                     customerRoleDefIdOptional.get());
    }

}