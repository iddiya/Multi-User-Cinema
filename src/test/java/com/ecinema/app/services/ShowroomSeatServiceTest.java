package com.ecinema.app.services;

import com.ecinema.app.domain.entities.ScreeningSeat;
import com.ecinema.app.domain.entities.Showroom;
import com.ecinema.app.domain.entities.ShowroomSeat;
import com.ecinema.app.domain.entities.Ticket;
import com.ecinema.app.domain.enums.Letter;
import com.ecinema.app.repositories.ScreeningSeatRepository;
import com.ecinema.app.repositories.ShowroomRepository;
import com.ecinema.app.repositories.ShowroomSeatRepository;
import com.ecinema.app.repositories.TicketRepository;
import com.ecinema.app.validators.SeatBookingValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShowroomSeatServiceTest {

    private TicketService ticketService;
    private SeatBookingValidator seatBookingValidator;
    private ScreeningSeatService screeningSeatService;
    private ShowroomSeatService showroomSeatService;
    private ShowroomService showroomService;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private ScreeningSeatRepository screeningSeatRepository;
    @Mock
    private ShowroomSeatRepository showroomSeatRepository;
    @Mock
    private ShowroomRepository showroomRepository;

    @BeforeEach
    void setUp() {
        seatBookingValidator = new SeatBookingValidator();
        ticketService = new TicketService(ticketRepository, null, seatBookingValidator, null, null,
                screeningSeatRepository);
        screeningSeatService = new ScreeningSeatService(screeningSeatRepository, ticketService);
        showroomSeatService = new ShowroomSeatService(showroomSeatRepository,screeningSeatService);
        showroomService = new ShowroomService(showroomRepository, showroomSeatService, null, null, ticketRepository);
    }

    @Test
    void deleteShowroomSeatCascade() {
        // given
        Showroom showroom = new Showroom();
        showroom.setShowroomLetter(Letter.A);
        showroomService.save(showroom);
        ShowroomSeat showroomSeat = new ShowroomSeat();
        showroomSeat.setId(1L);
        showroomSeat.setRowLetter(Letter.A);
        showroomSeat.setSeatNumber(1);
        showroomSeat.setShowroom(showroom);
        showroom.getShowroomSeats().add(showroomSeat);
        showroomSeatService.save(showroomSeat);
        ScreeningSeat screeningSeat = new ScreeningSeat();
        screeningSeat.setId(2L);
        screeningSeat.setShowroomSeat(showroomSeat);
        showroomSeat.getScreeningSeats().add(screeningSeat);
        screeningSeatService.save(screeningSeat);
        Ticket ticket = new Ticket();
        ticket.setId(3L);
        ticket.setScreeningSeat(screeningSeat);
        screeningSeat.setTicket(ticket);
        ticketService.save(ticket);
        assertEquals(ticket, screeningSeat.getTicket());
        assertEquals(showroomSeat, screeningSeat.getShowroomSeat());
        // when
        showroomSeatService.delete(showroomSeat);
        // then
        assertNotEquals(ticket, screeningSeat.getTicket());
        assertNotEquals(showroomSeat, screeningSeat.getShowroomSeat());
        verify(showroomSeatRepository, times(1)).delete(showroomSeat);
        verify(screeningSeatRepository, times(1)).delete(screeningSeat);
        verify(ticketRepository, times(1)).delete(ticket);
    }

}