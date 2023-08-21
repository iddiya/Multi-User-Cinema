package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.*;
import com.ecinema.app.domain.enums.Letter;
import com.ecinema.app.domain.enums.UserAuthority;
import com.ecinema.app.domain.objects.Duration;
import com.ecinema.app.util.UtilMethods;
import com.ecinema.app.domain.enums.TicketStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ShowroomRepository showroomRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private ShowroomSeatRepository showroomSeatRepository;

    @Autowired
    private ScreeningSeatRepository screeningSeatRepository;

    @AfterEach
    void tearDown() {
        ticketRepository.deleteAll();
    }

    @Test
    void findAllByCreationDateTimeLessThanEqual() {
        // given
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Ticket ticket = new Ticket();
            ticket.setCreationDateTime(UtilMethods.randomDateTime());
            ticketRepository.save(ticket);
            tickets.add(ticket);
        }
        LocalDateTime controlVar = UtilMethods.randomDateTime();
        List<Ticket> control = tickets.stream()
                .filter(ticket -> ticket.getCreationDateTime().isEqual(controlVar) ||
                        ticket.getCreationDateTime().isBefore(controlVar))
                .collect(Collectors.toList());
        // when
        List<Ticket> test = ticketRepository.findAllByCreationDateTimeLessThanEqual(controlVar);
        // then
        assertEquals(control, test, "both have size: " + control.size());
    }

    @Test
    void findAllByCreationDateTimeGreaterThanEqual() {
        // given
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Ticket ticket = new Ticket();
            ticket.setCreationDateTime(UtilMethods.randomDateTime());
            ticketRepository.save(ticket);
            tickets.add(ticket);
        }
        LocalDateTime controlVar = UtilMethods.randomDateTime();
        List<Ticket> control = tickets.stream()
                .filter(ticket -> ticket.getCreationDateTime().isEqual(controlVar) ||
                        ticket.getCreationDateTime().isAfter(controlVar))
                .collect(Collectors.toList());
        // when
        List<Ticket> test = ticketRepository.findAllByCreationDateTimeGreaterThanEqual(controlVar);
        // then
        assertEquals(control, test, "both have size: " + control.size());
    }

    @Test
    void findAllByTicketStatus() {
        // given
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Ticket ticket = new Ticket();
            ticket.setTicketStatus(TicketStatus.values()[i % 4]);
            ticketRepository.save(ticket);
            tickets.add(ticket);
        }
        List<Ticket> control = tickets.stream()
                .filter(ticket ->  ticket.getTicketStatus().equals(TicketStatus.VALID))
                .collect(Collectors.toList());
        // when
        List<Ticket> test = ticketRepository.findAllByTicketStatus(TicketStatus.VALID);
        // then
        assertEquals(control, test, "both have size: " + control.size());
    }

    @Test
    void queriesForBuildingDto() {
        // given
        User user = new User();
        user.setUsername("TestUser123");
        userRepository.save(user);
        Customer customer = new Customer();
        customer.setUser(user);
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
        customerRepository.save(customer);
        Showroom showroom = new Showroom();
        showroom.setShowroomLetter(Letter.C);
        showroomRepository.save(showroom);
        Movie movie = new Movie();
        movie.setTitle("Test Title");
        movie.setDuration(Duration.of(1, 30));
        movieRepository.save(movie);
        Screening screening = new Screening();
        screening.setShowroom(showroom);
        showroom.getScreenings().add(screening);
        screening.setMovie(movie);
        movie.getScreenings().add(screening);
        LocalDateTime localDateTime = LocalDateTime.now();
        screening.setShowDateTime(localDateTime);
        screening.setEndDateTime(
                localDateTime.plusHours(1).plusMinutes(30));
        screeningRepository.save(screening);
        ShowroomSeat showroomSeat = new ShowroomSeat();
        showroomSeat.setRowLetter(Letter.D);
        showroomSeat.setSeatNumber(9);
        showroomSeat.setShowroom(showroom);
        showroom.getShowroomSeats().add(showroomSeat);
        showroomSeatRepository.save(showroomSeat);
        ScreeningSeat screeningSeat = new ScreeningSeat();
        screeningSeat.setShowroomSeat(showroomSeat);
        showroomSeat.getScreeningSeats().add(screeningSeat);
        screeningSeat.setScreening(screening);
        screening.getScreeningSeats().add(screeningSeat);
        screeningSeatRepository.save(screeningSeat);
        Ticket ticket = new Ticket();
        ticket.setTicketOwner(customer);
        customer.getTickets().add(ticket);
        ticket.setScreeningSeat(screeningSeat);
        screeningSeat.setTicket(ticket);
        ticketRepository.save(ticket);
        // when
        String username = ticketRepository
                .findUsernameOfTicketUserOwner(ticket.getId())
                .orElseThrow(IllegalStateException::new);
        String movieTitle = ticketRepository
                .findMovieTitleAssociatedWithTicket(ticket.getId())
                .orElseThrow(IllegalStateException::new);
        Letter showroomLetter = ticketRepository
                .findShowroomLetterAssociatedWithTicket(ticket.getId())
                .orElseThrow(IllegalStateException::new);
        LocalDateTime showtime = ticketRepository
                .findShowDateTimeOfScreeningAssociatedWithTicket(ticket.getId())
                .orElseThrow(IllegalStateException::new);
        LocalDateTime endtime = ticketRepository
                .findEndDateTimeOfScreeningAssociatedWithTicket(ticket.getId())
                .orElseThrow(IllegalStateException::new);
        ShowroomSeat testShowroomSeat = ticketRepository
                .findShowroomSeatAssociatedWithTicket(ticket.getId())
                .orElseThrow(IllegalStateException::new);
        // then
        assertEquals("TestUser123", username);
        assertEquals("Test Title", movieTitle);
        assertEquals(Letter.C, showroomLetter);
        assertEquals(localDateTime, showtime);
        assertEquals(
                localDateTime.plusHours(1).plusMinutes(30), endtime);
        assertEquals(showroomSeat, testShowroomSeat);
    }

    @Test
    void findAllIdsByUserWithIdAndShowDateTime() {
        // given
        User user = new User();
        userRepository.save(user);
        Customer customer = new Customer();
        customer.setUser(user);
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
        customerRepository.save(customer);
        Screening screening1 = new Screening();
        screening1.setShowDateTime(LocalDateTime.now().minusHours(1));
        screeningRepository.save(screening1);
        ShowroomSeat showroomSeat1 = new ShowroomSeat();
        showroomSeat1.setRowLetter(Letter.A);
        showroomSeat1.setSeatNumber(1);
        showroomSeatRepository.save(showroomSeat1);
        ScreeningSeat screeningSeat1 = new ScreeningSeat();
        screeningSeat1.setShowroomSeat(showroomSeat1);
        showroomSeat1.getScreeningSeats().add(screeningSeat1);
        screeningSeat1.setScreening(screening1);
        screening1.getScreeningSeats().add(screeningSeat1);
        screeningSeatRepository.save(screeningSeat1);
        Screening screening2 = new Screening();
        screening2.setShowDateTime(LocalDateTime.now().plusHours(1));
        screeningRepository.save(screening2);
        ShowroomSeat showroomSeat2 = new ShowroomSeat();
        showroomSeat2.setRowLetter(Letter.A);
        showroomSeat2.setSeatNumber(1);
        showroomSeatRepository.save(showroomSeat2);
        ScreeningSeat screeningSeat2 = new ScreeningSeat();
        screeningSeat2.setShowroomSeat(showroomSeat2);
        showroomSeat2.getScreeningSeats().add(screeningSeat2);
        screeningSeat2.setScreening(screening2);
        screening2.getScreeningSeats().add(screeningSeat2);
        screeningSeatRepository.save(screeningSeat2);
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Ticket ticket = new Ticket();
            ticket.setScreeningSeat(i % 2 == 0 ? screeningSeat1 : screeningSeat2);
            ticket.setTicketOwner(customer);
            customer.getTickets().add(ticket);
            ticketRepository.save(ticket);
            tickets.add(ticket);
        }
        // when
        List<Long> idsOfPastTickets = ticketRepository
                .findAllIdsByUserWithIdAndShowDateTimeIsBefore(user.getId(), LocalDateTime.now());
        List<Long> idsOfCurrentTickets = ticketRepository
                .findAllIdsByUserWithIdAndShowDateTimeIsAfter(user.getId(), LocalDateTime.now());
        // then
        List<Long> controlTicketIdsOfPastTickets = tickets.stream().filter(ticket ->
                ticket.getScreeningSeat().getScreening().getShowDateTime().isBefore(LocalDateTime.now()))
                .map(Ticket::getId).collect(Collectors.toList());
        List<Long> controlTicketIdsOfCurrentTickets = tickets.stream().filter(ticket ->
                ticket.getScreeningSeat().getScreening().getShowDateTime().isAfter(LocalDateTime.now()))
                .map(Ticket::getId).collect(Collectors.toList());
        assertEquals(controlTicketIdsOfPastTickets, idsOfPastTickets);
        assertEquals(controlTicketIdsOfCurrentTickets, idsOfCurrentTickets);
    }

}