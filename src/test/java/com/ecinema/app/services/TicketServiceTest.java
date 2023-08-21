package com.ecinema.app.services;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.domain.entities.*;
import com.ecinema.app.domain.enums.*;
import com.ecinema.app.domain.forms.SeatBookingForm;
import com.ecinema.app.exceptions.InvalidActionException;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.repositories.*;
import com.ecinema.app.validators.SeatBookingValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    private TicketService ticketService;
    private ScreeningService screeningService;
    private ShowroomService showroomService;
    private ShowroomSeatService showroomSeatService;
    private ScreeningSeatService screeningSeatService;
    private CustomerService customerService;
    private ReviewService reviewService;
    private PaymentCardService paymentCardService;
    private SecurityContext securityContext;
    private SeatBookingValidator seatBookingValidator;
    @Mock
    private EmailService emailService;
    @Mock
    private ShowroomRepository showroomRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private ScreeningRepository screeningRepository;
    @Mock
    private ShowroomSeatRepository showroomSeatRepository;
    @Mock
    private ScreeningSeatRepository screeningSeatRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private PaymentCardRepository paymentCardRepository;
    @Mock
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        securityContext = new SecurityContext();
        seatBookingValidator = new SeatBookingValidator();
        ticketService = new TicketService(ticketRepository, emailService, seatBookingValidator, customerRepository,
                paymentCardRepository, screeningSeatRepository);
        screeningSeatService = new ScreeningSeatService(screeningSeatRepository, ticketService);
        showroomSeatService = new ShowroomSeatService(showroomSeatRepository, screeningSeatService);
        screeningService = new ScreeningService(screeningRepository, movieRepository, null, showroomRepository,
                screeningSeatService, null);
        reviewService = new ReviewService(reviewRepository, movieRepository, null, null, null);
        paymentCardService = new PaymentCardService(paymentCardRepository, null, null, null);
        customerService = new CustomerService(customerRepository, screeningSeatRepository, null, reviewService,
                ticketService, paymentCardService, null, securityContext);
        showroomService = new ShowroomService(showroomRepository, showroomSeatService, screeningService, null,
                ticketRepository);
    }

    @Test
    void deleteTicketCascade() {
        // given
        Showroom showroom = new Showroom();
        showroom.setShowroomLetter(Letter.A);
        showroomService.save(showroom);
        for (int i = 0; i < 30; i++) {
            ShowroomSeat showroomSeat = new ShowroomSeat();
            showroomSeat.setId((long) i);
            showroomSeat.setRowLetter(Letter.A);
            showroomSeat.setSeatNumber(i);
            given(showroomSeatRepository.findById((long) i))
                    .willReturn(Optional.of(showroomSeat));
            showroomSeatService.save(showroomSeat);
        }
        Screening screening = new Screening();
        screening.setId(0L);
        screeningService.save(screening);
        List<ScreeningSeat> screeningSeats = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            final Integer j = i;
            ShowroomSeat showroomSeat = showroomSeatRepository.findById((long) i).orElseThrow(
                    () -> new NoEntityFoundException("showroom seat", "id", j));
            ScreeningSeat screeningSeat = new ScreeningSeat();
            screeningSeat.setId((long) i);
            screeningSeat.setShowroomSeat(showroomSeat);
            showroomSeat.getScreeningSeats().add(screeningSeat);
            screeningSeat.setScreening(screening);
            screening.getScreeningSeats().add(screeningSeat);
            screeningSeatService.save(screeningSeat);
            screeningSeats.add(screeningSeat);
        }
        Customer customer = new Customer();
        customer.setId(31L);
        customerService.save(customer);
        Ticket ticket = new Ticket();
        ticket.setId(32L);
        ticket.setTicketOwner(customer);
        customer.getTickets().add(ticket);
        ticket.setScreeningSeat(screeningSeats.get(0));
        screeningSeats.get(0).setTicket(ticket);
        ticketService.save(ticket);
        assertFalse(customer.getTickets().isEmpty());
        assertNotNull(ticket.getTicketOwner());
        assertEquals(screeningSeats.get(0), ticket.getScreeningSeat());
        assertEquals(ticket, screeningSeats.get(0).getTicket());
        for (ScreeningSeat screeningSeat : screeningSeats) {
            assertEquals(screening, screeningSeat.getScreening());
        }
        // when
        ticketService.delete(ticket);
        // then
        assertTrue(customer.getTickets().isEmpty());
        assertNull(ticket.getTicketOwner());
        assertNotEquals(screeningSeats.get(0), ticket.getScreeningSeat());
        for (ScreeningSeat screeningSeat : screeningSeats) {
            assertEquals(screening, screeningSeat.getScreening());
        }
    }

    @Test
    void bookTicket() {
        // given
        Customer customer = new Customer();
        customer.setIsAuthorityValid(true);
        customer.setTokens(5);
        given(customerRepository.findByUserWithId(1L)).willReturn(Optional.of(customer));
        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setExpirationDate(LocalDate.now().plusYears(1));
        given(paymentCardRepository.findById(2L)).willReturn(Optional.of(paymentCard));
        Screening screening = new Screening();
        screening.setShowDateTime(LocalDateTime.now().plusHours(1));
        ShowroomSeat showroomSeat = new ShowroomSeat();
        showroomSeat.setRowLetter(Letter.A);
        showroomSeat.setSeatNumber(1);
        given(ticketRepository.findShowroomSeatAssociatedWithTicket(any()))
                .willReturn(Optional.of(showroomSeat));
        ScreeningSeat screeningSeat = new ScreeningSeat();
        screeningSeat.setShowroomSeat(showroomSeat);
        showroomSeat.getScreeningSeats().add(screeningSeat);
        screeningSeat.setScreening(screening);
        given(screeningSeatRepository.findById(3L)).willReturn(Optional.of(screeningSeat));
        doNothing().when(emailService).sendFromBusinessEmail(anyString(), anyString(), anyString());
        given(ticketRepository.findUserIdOfTicket(any())).willReturn(Optional.of(1L));
        given(ticketRepository.findShowDateTimeOfScreeningAssociatedWithTicket(any()))
                .willReturn(Optional.of(LocalDateTime.now().plusDays(4)));
        given(ticketRepository.findEmailOfTicketUserOwner(any()))
                .willReturn(Optional.of("user@gmail.com"));
        given(ticketRepository.findUsernameOfTicketUserOwner(any()))
                .willReturn(Optional.of("user"));
        given(ticketRepository.findMovieTitleAssociatedWithTicket(any()))
                .willReturn(Optional.of("Movie Title"));
        given(ticketRepository.findShowroomLetterAssociatedWithTicket(any()))
                .willReturn(Optional.of(Letter.A));
        given(ticketRepository.findShowDateTimeOfScreeningAssociatedWithTicket(any()))
                .willReturn(Optional.of(LocalDateTime.now().plusDays(5)));
        given(ticketRepository.findEndDateTimeOfScreeningAssociatedWithTicket(any()))
                .willReturn(Optional.of(LocalDateTime.now().plusDays(5)));
        // when
        SeatBookingForm seatBookingForm = new SeatBookingForm();
        seatBookingForm.setUserId(1L);
        seatBookingForm.setTokensToApply(3);
        seatBookingForm.setPaymentCardId(2L);
        seatBookingForm.setScreeningSeatId(3L);
        seatBookingForm.setTicketType(TicketType.ADULT);
        ticketService.bookTicket(seatBookingForm);
        // then
        ArgumentCaptor<Ticket> ticketArgumentCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(ticketArgumentCaptor.capture());
        Ticket ticket = ticketArgumentCaptor.getValue();
        assertEquals(2, customer.getTokens());
        assertEquals(customer, ticket.getTicketOwner());
        assertTrue(customer.getTickets().contains(ticket));
        assertEquals(paymentCard, ticket.getPaymentCard());
        assertTrue(paymentCard.getPurchasedTickets().contains(ticket));
        assertEquals(screeningSeat, ticket.getScreeningSeat());
        assertEquals(TicketType.ADULT, ticket.getTicketType());
        assertEquals(TicketStatus.VALID, ticket.getTicketStatus());
    }

    @Test
    void ticketIsRefundable1() {
        // given
        given(ticketRepository.findShowDateTimeOfScreeningAssociatedWithTicket(1L))
                .willReturn(Optional.of(LocalDateTime.now().plusHours(8)));
        // when
        boolean ticketIsRefundable = ticketService.ticketIsRefundable(1L);
        // then
        assertFalse(ticketIsRefundable);
    }

    @Test
    void ticketIsRefundable2() {
        // given
        given(ticketRepository.findShowDateTimeOfScreeningAssociatedWithTicket(1L))
                .willReturn(Optional.of(LocalDateTime.now().plusDays(4)));
        // when
        boolean ticketIsRefundable = ticketService.ticketIsRefundable(1L);
        // then
        assertTrue(ticketIsRefundable);
    }

    @Test
    void refundTicket() {
        // given
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketType(TicketType.ADULT);
        Customer customer = new Customer();
        given(customerRepository.findByUserWithId(1L)).willReturn(Optional.of(customer));
        given(ticketRepository.findUserIdOfTicket(1L)).willReturn(Optional.of(1L));
        given(ticketRepository.findShowDateTimeOfScreeningAssociatedWithTicket(1L))
                .willReturn(Optional.of(LocalDateTime.now().plusDays(4)));
        given(ticketRepository.findEmailOfTicketUserOwner(1L))
                .willReturn(Optional.of("user@gmail.com"));
        given(ticketRepository.findUsernameOfTicketUserOwner(1L))
                .willReturn(Optional.of("user"));
        given(ticketRepository.findMovieTitleAssociatedWithTicket(1L))
                .willReturn(Optional.of("Movie Title"));
        given(ticketRepository.findShowroomLetterAssociatedWithTicket(1L))
                .willReturn(Optional.of(Letter.A));
        given(ticketRepository.findShowDateTimeOfScreeningAssociatedWithTicket(1L))
                .willReturn(Optional.of(LocalDateTime.now().plusDays(5)));
        given(ticketRepository.findEndDateTimeOfScreeningAssociatedWithTicket(1L))
                .willReturn(Optional.of(LocalDateTime.now().plusDays(5)));
        ShowroomSeat showroomSeat = new ShowroomSeat();
        showroomSeat.setRowLetter(Letter.A);
        showroomSeat.setSeatNumber(1);
        given(ticketRepository.findShowroomSeatAssociatedWithTicket(1L))
                .willReturn(Optional.of(showroomSeat));
        given(ticketRepository.existsById(1L)).willReturn(true);
        given(ticketRepository.findById(1L)).willReturn(Optional.of(ticket));
        doNothing().when(emailService).sendFromBusinessEmail(anyString(), anyString(), anyString());
        // when
        ticketService.refundTicket(1L);
        // then
        verify(ticketRepository, times(1)).delete(ticket);
    }

    @Test
    void failToRefundTicket1() {
        // given
        given(ticketRepository.existsById(1L)).willReturn(false);
        // then
        assertThrows(NoEntityFoundException.class, () -> ticketService.refundTicket(1L));
    }

    @Test
    void failToRefundTicket2() {
        // given
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        given(ticketRepository.existsById(1L)).willReturn(true);
        given(ticketRepository.findShowDateTimeOfScreeningAssociatedWithTicket(1L))
                .willReturn(Optional.of(LocalDateTime.now().plusDays(1)));
        // then
        assertThrows(InvalidActionException.class, () -> ticketService.refundTicket(1L));
    }

}