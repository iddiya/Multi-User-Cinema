package com.ecinema.app.services;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.domain.dtos.*;
import com.ecinema.app.domain.entities.*;
import com.ecinema.app.domain.enums.*;
import com.ecinema.app.domain.forms.ReviewForm;
import com.ecinema.app.domain.objects.Duration;
import com.ecinema.app.repositories.*;
import com.ecinema.app.validators.MovieValidator;
import com.ecinema.app.validators.ReviewValidator;
import com.ecinema.app.validators.SeatBookingValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    private UserService userService;
    private MovieService movieService;
    private ReviewService reviewService;
    private TicketService ticketService;
    private MovieValidator movieValidator;
    private ShowroomService showroomService;
    private CustomerService customerService;
    private ReviewValidator reviewValidator;
    private SecurityContext securityContext;
    private ScreeningService screeningService;
    private ReviewVoteService reviewVoteService;
    private PaymentCardService paymentCardService;
    private ShowroomSeatService showroomSeatService;
    private ScreeningSeatService screeningSeatService;
    private SeatBookingValidator seatBookingValidator;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private ShowroomRepository showroomRepository;
    @Mock
    private ReviewVoteRepository reviewVoteRepository;
    @Mock
    private PaymentCardRepository paymentCardRepository;
    @Mock
    private ShowroomSeatRepository showroomSeatRepository;
    @Mock
    private ScreeningSeatRepository screeningSeatRepository;
    @Mock
    private ScreeningRepository screeningRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        securityContext = new SecurityContext();
        reviewValidator = new ReviewValidator();
        movieValidator = new MovieValidator();
        seatBookingValidator = new SeatBookingValidator();
        reviewVoteService = new ReviewVoteService(reviewVoteRepository, reviewRepository, customerRepository);
        reviewService = new ReviewService(reviewRepository, movieRepository, customerRepository, reviewValidator,
                reviewVoteService);
        ticketService = new TicketService(ticketRepository, null, seatBookingValidator, customerRepository,
                paymentCardRepository, screeningSeatRepository);
        paymentCardService = new PaymentCardService(paymentCardRepository, null, customerRepository, null);
        screeningSeatService = new ScreeningSeatService(screeningSeatRepository, ticketService);
        screeningService = new ScreeningService(screeningRepository, movieRepository, ticketRepository,
                showroomRepository, screeningSeatService, null);
        customerService = new CustomerService(customerRepository, screeningSeatRepository, null, reviewService,
                ticketService, paymentCardService, reviewVoteService, securityContext);
        movieService = new MovieService(movieRepository, reviewService, screeningService, movieValidator);
        showroomSeatService = new ShowroomSeatService(showroomSeatRepository, screeningSeatService);
        showroomService = new ShowroomService(showroomRepository, showroomSeatService, screeningService, null,
                ticketRepository);
        userService = new UserService(userRepository, customerService, null, null, null, null, null);
    }

    @Test
    void deleteMovieCascade() {
        // given
        Movie movie = new Movie();
        movie.setId(1L);
        Customer customer = new Customer();
        Review review = new Review();
        review.setId(2L);
        review.setWriter(customer);
        customer.getReviews().add(review);
        review.setMovie(movie);
        movie.getReviews().add(review);
        Screening screening = new Screening();
        screening.setId(3L);
        screening.setMovie(movie);
        movie.getScreenings().add(screening);
        Customer voter = new Customer();
        ReviewVote reviewVote = new ReviewVote();
        reviewVote.setVoter(voter);
        voter.getReviewVotes().add(reviewVote);
        reviewVote.setReview(review);
        review.getReviewVotes().add(reviewVote);
        assertTrue(customer.getReviews().contains(review));
        assertEquals(movie, review.getMovie());
        assertTrue(movie.getScreenings().contains(screening));
        assertEquals(movie, screening.getMovie());
        assertTrue(review.getReviewVotes().contains(reviewVote));
        assertEquals(review, reviewVote.getReview());
        assertTrue(voter.getReviewVotes().contains(reviewVote));
        assertEquals(voter, reviewVote.getVoter());
        // when
        movieService.delete(movie);
        // then
        assertFalse(customer.getReviews().contains(review));
        assertNull(review.getMovie());
        assertFalse(movie.getScreenings().contains(screening));
        assertNull(screening.getMovie());
        assertFalse(voter.getReviewVotes().contains(reviewVote));
        assertNull(reviewVote.getVoter());
        assertFalse(review.getReviewVotes().contains(reviewVote));
        assertNull(reviewVote.getReview());
    }

    /**
     * Test movie dto. Mockito complains about unnecessary stubbings, hence LENIENT setting.
     * Mockito is unable to recognize chain service calls (another service being called within a service).
     */
    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void movieDto() {
        // given
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("title");
        movie.setSynopsis("synopsis");
        movie.setDuration(Duration.of(1, 30));
        movie.setReleaseDate(LocalDate.of(2022, Month.APRIL, 28));
        movie.setMsrbRating(MsrbRating.PG);
        movie.setCast(new HashSet<>() {{
            add("test1");
            add("test2");
        }});
        movie.setDirector("test3");
        movie.setMovieCategories(EnumSet.of(MovieCategory.ACTION, MovieCategory.DRAMA));
        given(movieRepository.findById(1L))
                .willReturn(Optional.of(movie));
        movieService.save(movie);
        User user = new User();
        user.setUsername("test username");
        userService.save(user);
        Customer customer = new Customer();
        customer.setUser(user);
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
        customerService.save(customer);
        Review review = new Review();
        review.setId(1L);
        review.setRating(7);
        review.setWriter(customer);
        customer.getReviews().add(review);
        review.setReview("meh, it's okay");
        review.setMovie(movie);
        movie.getReviews().add(review);
        given(reviewRepository.findById(1L))
                .willReturn(Optional.of(review));
        reviewService.save(review);
        Showroom showroom = new Showroom();
        showroom.setId(1L);
        showroom.setShowroomLetter(Letter.A);
        showroomService.save(showroom);
        ShowroomSeat showroomSeat = new ShowroomSeat();
        showroomSeat.setId(1L);
        showroomSeat.setShowroom(showroom);
        showroomSeat.setRowLetter(Letter.A);
        showroomSeat.setSeatNumber(1);
        showroom.getShowroomSeats().add(showroomSeat);
        showroomSeat.setShowroom(showroom);
        given(showroomSeatRepository.findById(1L))
                .willReturn(Optional.of(showroomSeat));
        showroomSeatService.save(showroomSeat);
        Screening screening = new Screening();
        screening.setId(1L);
        screening.setMovie(movie);
        screening.setShowroom(showroom);
        screening.setShowDateTime(LocalDateTime.of(2022, Month.MARCH,
                                                   28, 12, 0));
        screening.setMovie(movie);
        movie.getScreenings().add(screening);
        given(screeningRepository.findById(1L))
                .willReturn(Optional.of(screening));
        screeningService.save(screening);
        ScreeningSeat screeningSeat = new ScreeningSeat();
        screeningSeat.setId(1L);
        screeningSeat.setShowroomSeat(showroomSeat);
        screening.getScreeningSeats().add(screeningSeat);
        screeningSeat.setScreening(screening);
        showroomSeat.getScreeningSeats().add(screeningSeat);
        screeningSeat.setShowroomSeat(showroomSeat);
        given(screeningSeatRepository.findById(1L))
                .willReturn(Optional.of(screeningSeat));
        screeningSeatService.save(screeningSeat);
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setScreeningSeat(screeningSeat);
        screeningSeat.setTicket(ticket);
        given(ticketRepository.findById(1L))
                .willReturn(Optional.of(ticket));
        ticketService.save(ticket);
        // when
        MovieDto movieDto = movieService.convertToDto(1L);
        // then
        assertEquals(movie.getId(), movieDto.getId());
        assertEquals(movie.getTitle(), movieDto.getTitle());
        assertEquals(movie.getSynopsis(), movieDto.getSynopsis());
        assertEquals(movie.getDuration(), movieDto.getDuration());
        assertEquals(movie.getMsrbRating(), movieDto.getMsrbRating());
        assertEquals(movie.getCast(), movieDto.getCast());
        assertEquals(movie.getDirector(), movieDto.getDirector());
        assertEquals(movie.getMovieCategories(), movieDto.getMovieCategories());
        assertEquals(movie.getReleaseDate(), movieDto.getReleaseDate());
    }

    @Test
    void submitReviewForm() {
        // given
        Movie movie = new Movie();
        movie.setId(1L);
        given(movieRepository.findById(1L))
                .willReturn(Optional.of(movie));
        movieService.save(movie);
        User user = new User();
        user.setId(2L);
        userService.save(user);
        Customer customer = new Customer();
        customer.setId(3L);
        customer.setUser(user);
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
        given(customerRepository.findByUserWithId(2L))
                .willReturn(Optional.of(customer));
        customerService.save(customer);
        // when
        ReviewForm reviewForm = new ReviewForm();
        reviewForm.setMovieId(1L);
        reviewForm.setUserId(2L);
        reviewForm.setRating(4);
        reviewForm.setReview("wow, this movie sucks so much!");
        reviewService.submitReviewForm(reviewForm);
        Set<Review> reviews = movie.getReviews();
        // then
        assertEquals(1, reviews.size());
        Review review = reviews.stream().findFirst().orElse(null);
        assertNotNull(review);
        assertEquals(movie, review.getMovie());
        assertEquals(reviewForm.getReview(), review.getReview());
        assertEquals(reviewForm.getRating(), review.getRating());
        assertEquals(customer, review.getWriter());
        assertEquals(user, review.getWriter().getUser());
    }

}