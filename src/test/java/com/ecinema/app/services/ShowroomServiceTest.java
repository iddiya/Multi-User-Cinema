package com.ecinema.app.services;

import com.ecinema.app.domain.dtos.ShowroomDto;
import com.ecinema.app.domain.entities.*;
import com.ecinema.app.domain.forms.ShowroomForm;
import com.ecinema.app.validators.ScreeningValidator;
import com.ecinema.app.validators.SeatBookingValidator;
import com.ecinema.app.validators.ShowroomValidator;
import com.ecinema.app.repositories.*;
import com.ecinema.app.domain.enums.Letter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShowroomServiceTest {

    private ShowroomService showroomService;
    private ShowroomSeatService showroomSeatService;
    private ScreeningService screeningService;
    private ScreeningSeatService screeningSeatService;
    private TicketService ticketService;
    private ShowroomValidator showroomValidator;
    private ScreeningValidator screeningValidator;
    private SeatBookingValidator seatBookingValidator;
    @Mock
    private ShowroomRepository showroomRepository;
    @Mock
    private ShowroomSeatRepository showroomSeatRepository;
    @Mock
    private ScreeningRepository screeningRepository;
    @Mock
    private ScreeningSeatRepository screeningSeatRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private MovieRepository movieRepository;
    @Captor
    ArgumentCaptor<Showroom> showroomArgumentCaptor;
    @Captor
    ArgumentCaptor<List<ShowroomSeat>> showroomSeatsArgumentCaptor;

    @BeforeEach
    void setUp() {
        showroomValidator = new ShowroomValidator();
        screeningValidator = new ScreeningValidator();
        seatBookingValidator = new SeatBookingValidator();
        ticketService = new TicketService(ticketRepository, null, seatBookingValidator, null, null,
                screeningSeatRepository);
        screeningSeatService = new ScreeningSeatService(screeningSeatRepository, ticketService);
        screeningService = new ScreeningService(screeningRepository, movieRepository, null, showroomRepository,
                screeningSeatService, screeningValidator);
        showroomSeatService = new ShowroomSeatService(showroomSeatRepository, screeningSeatService);
        showroomService = new ShowroomService(showroomRepository, showroomSeatService, screeningService,
                showroomValidator, ticketRepository);
    }

    @Test
    void findByShowroomLetter() {
        // given
        Map<Letter, Showroom> showrooms = new EnumMap<>(Letter.class);
        for (int i = 0; i < Letter.values().length; i++) {
            Showroom showroom = new Showroom();
            Letter showroomLetter = Letter.values()[i];
            showroom.setShowroomLetter(showroomLetter);
            showroomService.save(showroom);
            showrooms.put(showroomLetter, showroom);
        }
        Showroom control = showrooms.get(Letter.A);
        given(showroomRepository.findByShowroomLetter(Letter.A))
                .willReturn(Optional.of(showrooms.get(Letter.A)));
        // when
        Optional<Showroom> test = showroomRepository.findByShowroomLetter(Letter.A);
        // then
        assertTrue(test.isPresent());
        assertEquals(control, test.get());
    }

    @Test
    void findByShowroomSeatsContains() {
        // given
        Showroom showroom = new Showroom();
        showroom.setId(1000L);
        List<ShowroomSeat> showroomSeats = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Letter rowLetter = Letter.values()[i];
            for (int j = 0; j < 20; j++) {
                ShowroomSeat showroomSeat = new ShowroomSeat();
                showroomSeat.setId((long) (20 * i) + j);
                showroomSeat.setRowLetter(rowLetter);
                showroomSeat.setSeatNumber(j);
                showroomSeat.setShowroom(showroom);
                showroom.getShowroomSeats().add(showroomSeat);
                showroomSeats.add(showroomSeat);
            }
        }
        given(showroomRepository.findByShowroomSeatsContains(any()))
                .willReturn(Optional.of(showroom));
        // when
        ShowroomDto test = showroomService.findByShowroomSeatsContains(showroomSeats.get(0));
        // then
        assertEquals(showroom.getId(), test.getId());
    }

    @Test
    void deleteShowroomCascade() {
        // given
        Showroom showroom = new Showroom();
        showroom.setId(1L);
        showroom.setShowroomLetter(Letter.A);
        showroomService.save(showroom);
        List<ShowroomSeat> showroomSeats = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ShowroomSeat showroomSeat = new ShowroomSeat();
            showroomSeat.setId(3L + i);
            showroomSeat.setRowLetter(Letter.A);
            showroomSeat.setSeatNumber(i);
            showroomSeat.setShowroom(showroom);
            showroom.getShowroomSeats().add(showroomSeat);
            showroomSeatService.save(showroomSeat);
            showroomSeats.add(showroomSeat);
        }
        List<Screening> screenings = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Screening screening = new Screening();
            screening.setId(6L + i);
            screening.setShowroom(showroom);
            showroom.getScreenings().add(screening);
            screeningService.save(screening);
            screenings.add(screening);
        }
        for (ShowroomSeat showroomSeat : showroomSeats) {
            assertEquals(showroom, showroomSeat.getShowroom());
        }
        for (Screening screening : screenings) {
            assertEquals(showroom, screening.getShowroom());
        }
        // when
        showroomService.delete(showroom);
        // then
        for (ShowroomSeat showroomSeat : showroomSeats) {
            assertNull(showroomSeat.getShowroom());
        }
        for (Screening screening : screenings) {
            assertNull(screening.getShowroom());
        }
    }

    @Test
    void submitShowroomForm() {
        // given
        ShowroomForm showroomForm = new ShowroomForm();
        showroomForm.setShowroomLetter(Letter.A);
        showroomForm.setNumberOfRows(1);
        showroomForm.setNumberOfSeatsPerRow(3);
        // when
        showroomService.submitShowroomForm(showroomForm);
        verify(showroomRepository).save(showroomArgumentCaptor.capture());
        verify(showroomSeatRepository).saveAll(showroomSeatsArgumentCaptor.capture());
        // then
        Showroom showroom = showroomArgumentCaptor.getValue();
        List<ShowroomSeat> showroomSeats = showroomSeatsArgumentCaptor.getValue();
        assertEquals(Letter.A, showroom.getShowroomLetter());
        assertEquals(1, showroom.getNumberOfRows());
        assertEquals(3, showroom.getNumberOfSeatsPerRow());
        assertEquals(3, showroom.getShowroomSeats().size());
        assertTrue(showroom.getShowroomSeats().containsAll(showroomSeats));
        assertEquals(3, showroomSeats.stream().filter(
                showroomSeat -> showroomSeat.getRowLetter().equals(Letter.A)).count());
    }

}