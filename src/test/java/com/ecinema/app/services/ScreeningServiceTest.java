package com.ecinema.app.services;

import com.ecinema.app.domain.dtos.ScreeningDto;
import com.ecinema.app.domain.entities.*;
import com.ecinema.app.domain.forms.ScreeningForm;
import com.ecinema.app.repositories.*;
import com.ecinema.app.domain.objects.Duration;
import com.ecinema.app.domain.enums.Letter;
import com.ecinema.app.validators.ScreeningValidator;
import com.ecinema.app.validators.SeatBookingValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ScreeningServiceTest {

    private ScreeningService screeningService;
    private ScreeningSeatService screeningSeatService;
    private TicketService ticketService;
    private MovieService movieService;
    private ShowroomSeatService showroomSeatService;
    private ShowroomService showroomService;
    private ReviewService reviewService;
    private ScreeningValidator screeningValidator;
    private SeatBookingValidator seatBookingValidator;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private ScreeningRepository screeningRepository;
    @Mock
    private ScreeningSeatRepository screeningSeatRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private ShowroomSeatRepository showroomSeatRepository;
    @Mock
    private ShowroomRepository showroomRepository;

    @BeforeEach
    void setUp() {
        screeningValidator = new ScreeningValidator();
        seatBookingValidator = new SeatBookingValidator();
        ticketService = new TicketService(ticketRepository, null, seatBookingValidator, null, null,
                screeningSeatRepository);
        screeningSeatService = new ScreeningSeatService(screeningSeatRepository, ticketService);
        screeningService = new ScreeningService(screeningRepository, movieRepository, null, showroomRepository,
                screeningSeatService, screeningValidator);
        showroomSeatService = new ShowroomSeatService(showroomSeatRepository, screeningSeatService);
        showroomService = new ShowroomService(showroomRepository, showroomSeatService, screeningService, null,
                ticketRepository);
        reviewService = new ReviewService(reviewRepository, movieRepository, null, null, null);
        movieService = new MovieService(movieRepository, reviewService, screeningService, null);
    }

    @Test
    void deleteScreeningCascade() {
        // given
        Screening screening = new Screening();
        screening.setId(1L);
        Movie movie = new Movie();
        movie.getScreenings().add(screening);
        screening.setMovie(movie);
        movieService.save(movie);
        Showroom showroom = new Showroom();
        showroom.setId(1L);
        showroom.setShowroomLetter(Letter.A);
        showroom.getScreenings().add(screening);
        screening.setShowroom(showroom);
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
        screeningSeat.setScreening(screening);
        screeningSeat.setShowroomSeat(showroomSeat);
        showroomSeat.getScreeningSeats().add(screeningSeat);
        screening.getScreeningSeats().add(screeningSeat);
        screeningSeatService.save(screeningSeat);
        Ticket ticket = new Ticket();
        ticket.setId(3L);
        ticket.setScreeningSeat(screeningSeat);
        screeningSeat.setTicket(ticket);
        ticketService.save(ticket);
        assertEquals(movie, screening.getMovie());
        assertTrue(movie.getScreenings().contains(screening));
        assertEquals(showroom, screening.getShowroom());
        assertTrue(showroom.getScreenings().contains(screening));
        assertEquals(showroomSeat, screeningSeat.getShowroomSeat());
        assertTrue(showroomSeat.getScreeningSeats().contains(screeningSeat));
        assertEquals(showroom, showroomSeat.getShowroom());
        assertTrue(showroom.getShowroomSeats().contains(showroomSeat));
        assertEquals(screeningSeat, ticket.getScreeningSeat());
        // when
        screeningService.delete(screening);
        // then
        assertNotEquals(movie, screening.getMovie());
        assertNull(screening.getMovie());
        assertFalse(movie.getScreenings().contains(screening));
        assertNotEquals(showroom, screening.getShowroom());
        assertNull(screening.getShowroom());
        assertFalse(showroom.getScreenings().contains(screening));
        assertNotEquals(showroomSeat, screeningSeat.getShowroomSeat());
        assertNull(screeningSeat.getShowroomSeat());
        assertFalse(showroomSeat.getScreeningSeats().contains(screeningSeat));
        assertNull(screeningSeat.getShowroomSeat());
        assertNotEquals(screeningSeat, ticket.getScreeningSeat());
        assertNull(ticket.getScreeningSeat());
        assertNull(screeningSeat.getTicket());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void screeningDto() {
        // given
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("test");
        movieService.save(movie);
        Showroom showroom = new Showroom();
        showroom.setId(2L);
        showroom.setShowroomLetter(Letter.A);
        given(showroomRepository.save(showroom))
                .willReturn(showroom);
        showroomService.save(showroom);
        ShowroomSeat showroomSeat = new ShowroomSeat();
        showroomSeat.setId(3L);
        showroomSeat.setRowLetter(Letter.C);
        showroomSeat.setSeatNumber(14);
        showroomSeat.setShowroom(showroom);
        showroom.getShowroomSeats().add(showroomSeat);
        given(showroomSeatRepository.findById(3L))
                .willReturn(Optional.of(showroomSeat));
        Screening screening = new Screening();
        screening.setId(4L);
        screening.setShowDateTime(LocalDateTime.of(2022, Month.MAY, 1, 12, 0));
        screening.setShowroom(showroom);
        showroom.getScreenings().add(screening);
        screening.setMovie(movie);
        movie.getScreenings().add(screening);
        given(screeningRepository.findById(4L))
                .willReturn(Optional.of(screening));
        ScreeningSeat screeningSeat = new ScreeningSeat();
        screeningSeat.setId(5L);
        screeningSeat.setShowroomSeat(showroomSeat);
        showroomSeat.getScreeningSeats().add(screeningSeat);
        screeningSeat.setScreening(screening);
        screening.getScreeningSeats().add(screeningSeat);
        given(screeningSeatRepository.findById(5L))
                .willReturn(Optional.of(screeningSeat));
        given(screeningSeatRepository.save(screeningSeat))
                .willReturn(screeningSeat);
        screeningSeatService.save(screeningSeat);
        // when
        ScreeningDto screeningDto = screeningService.convertToDto(screening.getId());
        // then
        assertEquals(screening.getId(), screeningDto.getId());
        assertEquals("test", screeningDto.getMovieTitle());
        assertEquals(Letter.A, screeningDto.getShowroomLetter());
        assertEquals(LocalDateTime.of(2022, Month.MAY, 1, 12, 0),
                     screeningDto.getShowDateTime());
        assertNotNull(screeningDto);
    }

    @Test
    void submitScreeningForm() {
        // given
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setDuration(Duration.of(1, 30));
        given(movieRepository.findById(1L)).willReturn(Optional.of(movie));
        Showroom showroom = new Showroom();
        showroom.setShowroomLetter(Letter.A);
        showroom.setId(2L);
        given(showroomRepository.findById(2L)).willReturn(Optional.of(showroom));
        for (int i = 3; i < 6; i++) {
            ShowroomSeat showroomSeat = new ShowroomSeat();
            showroomSeat.setRowLetter(Letter.A);
            showroomSeat.setSeatNumber(i);
            showroomSeat.setShowroom(showroom);
            showroom.getShowroomSeats().add(showroomSeat);
        }
        // when
        ScreeningForm screeningForm = new ScreeningForm();
        screeningForm.setMovieId(1L);
        screeningForm.setShowroomId(2L);
        screeningForm.setShowtimeHour(1);
        screeningForm.setShowtimeMinute(0);
        screeningForm.setShowdate(LocalDate.of(2023, Month.JANUARY, 1));
        screeningService.submitScreeningForm(screeningForm);
        // then
        ArgumentCaptor<Screening> screeningArgumentCaptor = ArgumentCaptor.forClass(Screening.class);
        verify(screeningRepository).save(screeningArgumentCaptor.capture());
        Screening screening = screeningArgumentCaptor.getValue();
        assertEquals(movie, screening.getMovie());
        assertEquals(showroom, screening.getShowroom());
        assertTrue(screening.getScreeningSeats().stream().allMatch(
                screeningSeat -> screeningSeat != null &&
                        showroom.getShowroomSeats().contains(screeningSeat.getShowroomSeat())));
    }

}