package com.ecinema.app.controllers;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.configs.InitializationConfig;
import com.ecinema.app.domain.dtos.*;
import com.ecinema.app.domain.enums.Letter;
import com.ecinema.app.domain.enums.UserAuthority;
import com.ecinema.app.domain.forms.LongListForm;
import com.ecinema.app.domain.forms.SeatBookingForm;
import com.ecinema.app.exceptions.InvalidActionException;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TicketControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserService userService;

    @MockBean
    private TicketService ticketService;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private ScreeningService screeningService;

    @MockBean
    private PaymentCardService paymentCardService;

    @MockBean
    private ScreeningSeatService screeningSeatService;

    @MockBean
    private SecurityContext securityContext;

    @MockBean
    private InitializationConfig config;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void showViewSeatsPage()
        throws Exception {
        setUpCustomer();
        ScreeningDto screeningDto = new ScreeningDto();
        given(screeningService.findById(1L)).willReturn(screeningDto);
        Map<Letter, Set<ScreeningSeatDto>> mapOfScreeningSeats = new EnumMap<>(Letter.class);
        given(screeningSeatService.findScreeningSeatMapByScreeningWithId(1L))
                .willReturn(mapOfScreeningSeats);
        mockMvc.perform(get("/view-seats")
                                .param("id", String.valueOf(1L)))
                .andExpect(status().isOk())
                .andExpect(result -> model().attribute("screening", screeningDto))
                .andExpect(result -> model().attribute("mapOfScreeningSeats", mapOfScreeningSeats))
                .andExpect(result -> model().attribute("seatIdsForm", new LongListForm()));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void showBookSeatPage()
        throws Exception {
        setUpCustomer();
        given(screeningService.existsById(1L)).willReturn(true);
        given(screeningSeatService.screeningSeatIsBooked(2L)).willReturn(false);
        given(customerService.numberOfTokensOwnedByUser(1L)).willReturn(6);
        ScreeningDto screening = new ScreeningDto();
        given(screeningService.findById(1L)).willReturn(screening);
        ScreeningSeatDto screeningSeat = new ScreeningSeatDto();
        given(screeningSeatService.findById(2L)).willReturn(screeningSeat);
        SeatBookingForm seatBookingForm = new SeatBookingForm();
        seatBookingForm.setUserId(1L);
        seatBookingForm.setScreeningSeatId(2L);
        given(screeningSeatService.fetchSeatBookingForm(2L)).willReturn(seatBookingForm);
        List<PaymentCardDto> paymentCards = new ArrayList<>();
        given(paymentCardService.findAllByCardUserWithId(1L)).willReturn(paymentCards);
        mockMvc.perform(get("/book-seat")
                                .param("screeningId", String.valueOf(1L))
                                .param("seatId", String.valueOf(2L)))
               .andExpect(status().isOk())
               .andExpect(result -> model().attribute("screeningId", 1L))
               .andExpect(result -> model().attribute("screening", screening))
               .andExpect(result -> model().attribute("seatId", 2L))
               .andExpect(result -> model().attribute("screeningSeat", screeningSeat))
               .andExpect(result -> model().attribute("tokens", 6))
               .andExpect(result -> model().attribute("paymentCards", paymentCards))
               .andExpect(result -> model().attribute("seatBookingForm", seatBookingForm))
               .andExpect(result -> model().attribute("seatBookingForm", hasProperty(
                       "userId", is(1L))))
               .andExpect(result -> model().attribute("seatBookingForm", hasProperty(
                       "screeningSeatId", is(2L))));
    }

    @Test
    @WithAnonymousUser
    void failToShowBookSeatsPage1()
        throws Exception {
        mockMvc.perform(get("/book-seat")
                                .param("screeningId",String.valueOf(1L))
                                .param("seatId", String.valueOf(2L)))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"MODERATOR", "ADMIN"})
    void failToShowBookSeatsPage2()
            throws Exception {
        mockMvc.perform(get("/book-seat")
                                .param("screeningId",String.valueOf(1L))
                                .param("seatId", String.valueOf(2L)))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void failToShowBookSeatsPage3()
        throws Exception {
        setUpCustomer();
        given(screeningService.existsById(1L)).willReturn(false);
        mockMvc.perform(get("/book-seat")
                                .param("screeningId",String.valueOf(1L))
                                .param("seatId", String.valueOf(2L)))
               .andExpect(redirectedUrlPattern("/view-seats**"))
               .andExpect(result -> model().attributeExists("errors"));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void failToShowBookSeatsPage4()
            throws Exception {
        setUpCustomer();
        given(screeningService.existsById(1L)).willReturn(true);
        given(screeningSeatService.screeningSeatIsBooked(2L)).willReturn(true);
        mockMvc.perform(get("/book-seat")
                                .param("screeningId",String.valueOf(1L))
                                .param("seatId", String.valueOf(2L)))
               .andExpect(redirectedUrlPattern("/view-seats**"))
               .andExpect(result -> model().attributeExists("errors"));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void bookSeats()
        throws Exception {
        SeatBookingForm seatBookingForm = new SeatBookingForm();
        doNothing().when(ticketService).bookTicket(eq(seatBookingForm));
        mockMvc.perform(post("/book-seat")
                                .flashAttr("seatBookingForm", seatBookingForm))
                .andExpect(redirectedUrlPattern("/view-seats**"))
                .andExpect(result -> model().attributeExists("success"));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void failToBookSeats()
        throws Exception {
        SeatBookingForm seatBookingForm = new SeatBookingForm();
        InvalidActionException e = new InvalidActionException("Invalid action");
        doThrow(e).when(ticketService).bookTicket(seatBookingForm);
        mockMvc.perform(post("/book-seat").flashAttr("seatBookingForm", seatBookingForm))
                .andExpect(redirectedUrlPattern("/book-seat**"))
                .andExpect(result -> model().attributeExists("errors"));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void showCurrentTicketsPage()
        throws Exception {
        setUpCustomer();
        List<TicketDto> tickets = new ArrayList<>();
        given(ticketService.findAllByUserWithIdAndShowDateTimeIsAfter(
                eq(1L), ArgumentMatchers.any(LocalDateTime.class)))
                .willReturn(tickets);
        mockMvc.perform(get("/current-tickets"))
                .andExpect(status().isOk())
                .andExpect(result -> model().attribute("tickets", tickets));
    }

    @Test
    @WithAnonymousUser
    void failToShowCurrentTicketsPage1()
        throws Exception {
        mockMvc.perform(get("/current-tickets"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ADMIN", "MODERATOR"})
    void failToShowCurrentTicketsPage2()
        throws Exception {
        mockMvc.perform(get("/current-tickets"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void showPastTicketsPage()
        throws Exception {
        setUpCustomer();
        List<TicketDto> tickets = new ArrayList<>();
        given(ticketService.findAllByUserWithIdAndShowDateTimeIsBefore(
                eq(1L), ArgumentMatchers.any(LocalDateTime.class)))
                .willReturn(tickets);
        mockMvc.perform(get("/past-tickets"))
                .andExpect(status().isOk())
                .andExpect(result -> model().attribute("tickets", tickets));
    }

    @Test
    @WithAnonymousUser
    void failToShowPastTicketsPage1()
        throws Exception {
        mockMvc.perform(get("/past-tickets"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ADMIN", "MODERATOR"})
    void failToShowPastTicketsPage2()
        throws Exception {
        mockMvc.perform(get("/past-tickets"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void showRefundTicketPage()
        throws Exception {
        setUpCustomer();
        TicketDto ticketDto = new TicketDto();
        given(ticketService.findById(1L)).willReturn(ticketDto);
        mockMvc.perform(get("/refund-ticket")
                                .param("id", String.valueOf(1L)))
                .andExpect(status().isOk())
                .andExpect(result -> model().attribute("ticket", ticketDto));
    }

    @Test
    @WithAnonymousUser
    void failToShowRefundTicketPage1()
        throws Exception {
        mockMvc.perform(get("/refund-ticket/" + 1L))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ADMIN", "MODERATOR"})
    void failToShowRefundTicketPage2()
        throws Exception {
        mockMvc.perform(get("/refund-ticket/" + 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void refundTicket()
        throws Exception {
        setUpCustomer();
        doNothing().when(ticketService).refundTicket(1L);
        mockMvc.perform(post("/refund-ticket/" + 1L))
                .andExpect(redirectedUrlPattern("/current-tickets**"))
                .andExpect(result -> model().attributeExists("success"));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    void failToRefundTicket()
        throws Exception {
        setUpCustomer();
        NoEntityFoundException e = new NoEntityFoundException("ticket", "id", 1L);
        doThrow(e).when(ticketService).refundTicket(1L);
        mockMvc.perform(post("/refund-ticket/" + 1L))
                .andExpect(redirectedUrlPattern("/refund-ticket**"))
                .andExpect(result -> model().attribute("errors", e.getErrors()));
    }

    void setUpCustomer() {
        given(securityContext.findIdOfLoggedInUser()).willReturn(1L);
        UserDto userDto = new UserDto();
        userDto.getUserAuthorities().add(UserAuthority.CUSTOMER);
        given(userService.findById(1L)).willReturn(userDto);
    }

}