package com.ecinema.app.services;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.domain.entities.Customer;
import com.ecinema.app.domain.entities.PaymentCard;
import com.ecinema.app.domain.enums.PaymentCardType;
import com.ecinema.app.domain.enums.UsState;
import com.ecinema.app.domain.forms.PaymentCardForm;
import com.ecinema.app.validators.AddressValidator;
import com.ecinema.app.validators.PaymentCardValidator;
import com.ecinema.app.repositories.*;
import com.ecinema.app.validators.SeatBookingValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceTest {

    private ReviewService reviewService;
    private TicketService ticketService;
    private CustomerService customerService;
    private AddressValidator addressValidator;
    private PaymentCardService paymentCardService;
    private PaymentCardValidator paymentCardValidator;
    private SeatBookingValidator seatBookingValidator;
    private EncoderService encoderService;
    private SecurityContext securityContext;
    @Mock
    private ScreeningSeatRepository screeningSeatRepository;
    @Mock
    private PaymentCardRepository paymentCardRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        securityContext = new SecurityContext();
        addressValidator = new AddressValidator();
        seatBookingValidator = new SeatBookingValidator();
        encoderService = new EncoderService(new BCryptPasswordEncoder());
        paymentCardValidator = new PaymentCardValidator(addressValidator);
        paymentCardService = new PaymentCardService(paymentCardRepository, encoderService, customerRepository,
                paymentCardValidator);
        reviewService = new ReviewService(reviewRepository, null, customerRepository, null, null);
        ticketService = new TicketService(ticketRepository, null, seatBookingValidator, customerRepository,
                paymentCardRepository, screeningSeatRepository);
        customerService = new CustomerService(customerRepository, screeningSeatRepository, null, reviewService,
                ticketService, paymentCardService, null, securityContext);
    }

    @Test
    void onDeleteCascade() {
        // given
        Customer customer = new Customer();
        customerService.save(customer);
        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setId(2L);
        paymentCard.setCardOwner(customer);
        customer.getPaymentCards().add(paymentCard);
        paymentCardService.save(paymentCard);
        assertTrue(customer.getPaymentCards().contains(paymentCard));
        assertEquals(customer, paymentCard.getCardOwner());
        // when
        paymentCardService.delete(paymentCard);
        // then
        assertFalse(customer.getPaymentCards().contains(paymentCard));
        assertNotEquals(customer, paymentCard.getCardOwner());
    }

    @Test
    void submitPaymentCardForm1() {
        // given
        Customer customer = new Customer();
        given(customerRepository.findByUserWithId(1L)).willReturn(Optional.of(customer));
        // when
        LocalDate expirationDate = LocalDate.of(2030, Month.JANUARY, 1);
        PaymentCardForm paymentCardForm = new PaymentCardForm();
        paymentCardForm.setUserId(1L);
        paymentCardForm.setCardNumber("1234567812345678");
        paymentCardForm.setFirstName("Johnny");
        paymentCardForm.setLastName("Bravo");
        paymentCardForm.setExpirationDate(expirationDate);
        paymentCardForm.setStreet("555 Fifth St");
        paymentCardForm.setCity("Filthy City");
        paymentCardForm.setUsState(UsState.GEORGIA);
        paymentCardForm.setZipcode("55555");
        paymentCardForm.setPaymentCardType(PaymentCardType.CREDIT);
        paymentCardService.submitPaymentCardFormToAddNewPaymentCard(paymentCardForm);
        // then
        ArgumentCaptor<PaymentCard> paymentCardArgumentCaptor = ArgumentCaptor.forClass(PaymentCard.class);
        verify(paymentCardRepository).save(paymentCardArgumentCaptor.capture());
        PaymentCard paymentCard = paymentCardArgumentCaptor.getValue();
        assertEquals(1, customer.getPaymentCards().size());
        assertEquals(customer, paymentCard.getCardOwner());
        assertEquals("Johnny", paymentCard.getFirstName());
        assertEquals("Bravo", paymentCard.getLastName());
        assertTrue(encoderService.matches("1234567812345678", paymentCard.getCardNumber()));
        assertEquals(expirationDate, paymentCard.getExpirationDate());
        assertEquals("555 Fifth St", paymentCard.getBillingAddress().getStreet());
        assertEquals("Filthy City", paymentCard.getBillingAddress().getCity());
        assertEquals(UsState.GEORGIA, paymentCard.getBillingAddress().getUsState());
        assertEquals("55555", paymentCard.getBillingAddress().getZipcode());
    }

    @Test
    void submitPaymentCardForm2() {
        // given
        PaymentCard paymentCard = new PaymentCard();
        given(paymentCardRepository.findById(1L)).willReturn(Optional.of(paymentCard));
        // when
        LocalDate expirationDate = LocalDate.of(2030, Month.JANUARY, 1);
        PaymentCardForm paymentCardForm = new PaymentCardForm();
        paymentCardForm.setPaymentCardId(1L);
        paymentCardForm.setFirstName("Johnny");
        paymentCardForm.setLastName("Bravo");
        paymentCardForm.setExpirationDate(expirationDate);
        paymentCardForm.setStreet("555 Fifth St");
        paymentCardForm.setCity("Filthy City");
        paymentCardForm.setUsState(UsState.GEORGIA);
        paymentCardForm.setZipcode("55555");
        paymentCardForm.setPaymentCardType(PaymentCardType.CREDIT);
        paymentCardService.submitPaymentCardFormToEditPaymentCard(paymentCardForm);
        // then
        assertEquals("Johnny", paymentCard.getFirstName());
        assertEquals("Bravo", paymentCard.getLastName());
        assertEquals(expirationDate, paymentCard.getExpirationDate());
        assertEquals("555 Fifth St", paymentCard.getBillingAddress().getStreet());
        assertEquals("Filthy City", paymentCard.getBillingAddress().getCity());
        assertEquals(UsState.GEORGIA, paymentCard.getBillingAddress().getUsState());
        assertEquals("55555", paymentCard.getBillingAddress().getZipcode());
    }

}