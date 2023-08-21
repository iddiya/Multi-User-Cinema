package com.ecinema.app.services;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.domain.entities.*;
import com.ecinema.app.domain.enums.Letter;
import com.ecinema.app.domain.enums.UserAuthority;
import com.ecinema.app.domain.forms.SeatBookingForm;
import com.ecinema.app.repositories.*;
import com.ecinema.app.validators.SeatBookingValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * The type Customer role def service test.
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private TicketService ticketService;
    private ReviewService reviewService;
    private CustomerService customerService;
    private ReviewVoteService reviewVoteService;
    private PaymentCardService paymentCardService;
    private SeatBookingValidator seatBookingValidator;
    @Mock
    private EmailService emailService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ReviewVoteRepository reviewVoteRepository;
    @Mock
    private PaymentCardRepository paymentCardRepository;
    @Mock
    private ScreeningSeatRepository screeningSeatRepository;

    /*
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        seatBookingValidator = new SeatBookingValidator();
        ticketService = new TicketService(ticketRepository, emailService, seatBookingValidator, customerRepository,
                paymentCardRepository, screeningSeatRepository);
        reviewVoteService = new ReviewVoteService(reviewVoteRepository, reviewRepository, customerRepository);
        reviewService = new ReviewService(reviewRepository, null, customerRepository, null, reviewVoteService);
        paymentCardService = new PaymentCardService(paymentCardRepository, null, customerRepository, null);
        customerService = new CustomerService(customerRepository, screeningSeatRepository, emailService, reviewService,
                ticketService, paymentCardService, reviewVoteService, securityContext);
    }

    @Test
    void deleteCustomerRoleDefCascade() {
        // given
       Customer customer = new Customer();
       customer.setId(1L);
       customerService.save(customer);
       Review review = new Review();
       review.setId(2L);
       review.setWriter(customer);
       customer.getReviews().add(review);
       reviewService.save(review);
       Ticket ticket = new Ticket();
       ticket.setId(3L);
       ticket.setTicketOwner(customer);
       customer.getTickets().add(ticket);
       ticketService.save(ticket);
       PaymentCard paymentCard = new PaymentCard();
       paymentCard.setId(4L);
       paymentCard.setCardOwner(customer);
       customer.getPaymentCards().add(paymentCard);
       paymentCardService.save(paymentCard);
       assertEquals(customer, review.getWriter());
       assertTrue(customer.getReviews().contains(review));
       assertEquals(customer, ticket.getTicketOwner());
       assertTrue(customer.getTickets().contains(ticket));
       assertEquals(customer, paymentCard.getCardOwner());
       assertTrue(customer.getPaymentCards().contains(paymentCard));
       // when
        customerService.delete(customer);
        // then
        assertNotEquals(customer, review.getWriter());
        assertFalse(customer.getReviews().contains(review));
        assertNotEquals(customer, ticket.getTicketOwner());
        assertFalse(customer.getTickets().contains(ticket));
        assertNotEquals(customer, paymentCard.getCardOwner());
        assertFalse(customer.getPaymentCards().contains(paymentCard));
    }

    @Test
    void bookTickets() {

    }

}