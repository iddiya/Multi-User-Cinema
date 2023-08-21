package com.ecinema.app.services;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.domain.dtos.ReviewDto;
import com.ecinema.app.domain.entities.*;
import com.ecinema.app.domain.enums.UserAuthority;
import com.ecinema.app.domain.enums.Vote;
import com.ecinema.app.domain.forms.ReviewForm;
import com.ecinema.app.validators.MovieValidator;
import com.ecinema.app.validators.ReviewValidator;
import com.ecinema.app.repositories.*;
import com.ecinema.app.validators.SeatBookingValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    private ScreeningSeatService screeningSeatService;
    private SeatBookingValidator seatBookingValidator;
    private PaymentCardService paymentCardService;
    private ReviewVoteService reviewVoteService;
    private ScreeningService screeningService;
    private CustomerService customerService;
    private ReviewValidator reviewValidator;
    private SecurityContext securityContext;
    private MovieValidator movieValidator;
    private ReviewService reviewService;
    private TicketService ticketService;
    private MovieService movieService;
    private UserService userService;
    @Mock
    private ScreeningSeatRepository screeningSeatRepository;
    @Mock
    private PaymentCardRepository paymentCardRepository;
    @Mock
    private ReviewVoteRepository reviewVoteRepository;
    @Mock
    private ScreeningRepository screeningRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ShowroomRepository showroomRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        securityContext = new SecurityContext();
        movieValidator = new MovieValidator();
        reviewValidator = new ReviewValidator();
        seatBookingValidator = new SeatBookingValidator();
        reviewVoteService = new ReviewVoteService(reviewVoteRepository, reviewRepository, customerRepository);
        paymentCardService = new PaymentCardService(paymentCardRepository, null, customerRepository, null);
        reviewService = new ReviewService(reviewRepository, movieRepository, customerRepository, reviewValidator,
                reviewVoteService);
        ticketService = new TicketService(ticketRepository, null, seatBookingValidator, customerRepository,
                paymentCardRepository, screeningSeatRepository);
        screeningSeatService = new ScreeningSeatService(screeningSeatRepository, ticketService);
        screeningService = new ScreeningService(screeningRepository, movieRepository, ticketRepository,
                showroomRepository, screeningSeatService, null);
        customerService = new CustomerService(customerRepository, screeningSeatRepository, null, reviewService,
                ticketService, paymentCardService, reviewVoteService, securityContext);
        movieService = new MovieService(movieRepository, reviewService, screeningService, movieValidator);
        userService = new UserService(userRepository, customerService, null, null, null, null, null);
    }

    @Test
    void deleteReviewCascade() {
        // given
        Customer customer = new Customer();
        Movie movie = new Movie();
        Review review = new Review();
        review.setId(1L);
        review.setWriter(customer);
        customer.getReviews().add(review);
        review.setMovie(movie);
        movie.getReviews().add(review);
        Customer voter = new Customer();
        ReviewVote reviewVote = new ReviewVote();
        reviewVote.setReview(review);
        review.getReviewVotes().add(reviewVote);
        reviewVote.setVoter(voter);
        voter.getReviewVotes().add(reviewVote);
        assertTrue(customer.getReviews().contains(review));
        assertEquals(customer, review.getWriter());
        assertTrue(movie.getReviews().contains(review));
        assertEquals(movie, review.getMovie());
        assertTrue(voter.getReviewVotes().contains(reviewVote));
        assertEquals(voter, reviewVote.getVoter());
        assertTrue(review.getReviewVotes().contains(reviewVote));
        assertEquals(review, reviewVote.getReview());
        // when
        reviewService.delete(review);
        // then
        assertFalse(customer.getReviews().contains(review));
        assertNotEquals(customer, review.getWriter());
        assertFalse(movie.getReviews().contains(review));
        assertNotEquals(movie, review.getMovie());
        assertFalse(voter.getReviewVotes().contains(reviewVote));
        assertNotEquals(voter, reviewVote.getVoter());
        assertFalse(review.getReviewVotes().contains(reviewVote));
        assertNotEquals(review, reviewVote.getReview());
    }

    @Test
    void convertToDto() {
        // given
        given(reviewRepository.findCustomerIdByReviewWithId(1L))
                .willReturn(Optional.of(2L));
        given(reviewRepository.findUserIdByReviewWithId(1L))
                .willReturn(Optional.of(3L));
        given(reviewRepository.findUsernameOfWriterForReviewWithId(1L))
                .willReturn(Optional.of("username"));
        Review review = new Review();
        review.setId(1L);
        review.setReview("test review");
        review.setRating(7);
        review.setIsCensored(false);
        review.setCreationDateTime(LocalDateTime.of(2022, Month.APRIL, 28, 22, 55));
        Map<Vote, List<Long>> mapOfVoterUserIds = new EnumMap<>(Vote.class) {{
           put(Vote.UPVOTE, new ArrayList<>());
           put(Vote.DOWNVOTE, new ArrayList<>());
        }};
        for (int i = 0; i < 10; i++) {
            Vote vote = i % 2 == 0 ? Vote.UPVOTE : Vote.DOWNVOTE;
            mapOfVoterUserIds.get(vote).add((long) i);
        }
        given(reviewVoteRepository.findAllUserIdsByReviewWithIdAndVote(1L, Vote.UPVOTE))
                .willReturn(mapOfVoterUserIds.get(Vote.UPVOTE));
        given(reviewVoteRepository.findAllUserIdsByReviewWithIdAndVote(1L, Vote.DOWNVOTE))
                .willReturn(mapOfVoterUserIds.get(Vote.DOWNVOTE));
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
        // when
        ReviewDto reviewDto = reviewService.convertToDto(1L);
        // then
        assertEquals(review.getId(), reviewDto.getId());
        assertEquals("username", reviewDto.getWriter());
        assertEquals(review.getReview(), reviewDto.getReview());
        assertEquals(7, reviewDto.getRating());
        assertEquals(3L, reviewDto.getUserId());
        assertEquals(2L, reviewDto.getCustomerId());
        assertEquals(review.getIsCensored(), reviewDto.getIsCensored());
        assertEquals(LocalDateTime.of(2022, Month.APRIL, 28, 22, 55),
                     reviewDto.getCreationDateTime());
        assertEquals(mapOfVoterUserIds.get(Vote.UPVOTE), reviewDto.getUpvoteUserIds());
        assertEquals(mapOfVoterUserIds.get(Vote.DOWNVOTE), reviewDto.getDownvoteUserIds());
    }

    @Test
    void submitReviewForm() {
        // given
        User user = new User();
        user.setId(1L);
        userService.save(user);
        Customer customer = new Customer();
        customer.setUser(user);
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
        given(customerRepository.findByUserWithId(1L))
                .willReturn(Optional.of(customer));
        customerService.save(customer);
        Movie movie = new Movie();
        movie.setId(2L);
        given(movieRepository.findById(2L)).willReturn(Optional.of(movie));
        movieService.save(movie);
        given(reviewRepository.existsByWriterAndMovie(customer, movie))
                .willReturn(false);
        // when
        String reviewStr = "This movie is absolute garbage, I don't even know why I paid to see it.";
        ReviewForm reviewForm = new ReviewForm();
        reviewForm.setUserId(1L);
        reviewForm.setMovieId(2L);
        reviewForm.setReview(reviewStr);
        reviewForm.setRating(10);
        reviewService.submitReviewForm(reviewForm);
        // then
        assertEquals(1, customer.getReviews().size());
        Review review = customer.getReviews().stream().findFirst()
                                .orElseThrow(IllegalStateException::new);
        assertEquals(movie, review.getMovie());
        assertEquals(customer, review.getWriter());
        assertEquals(10, review.getRating());
        assertEquals(reviewStr, review.getReview());
        assertFalse(review.getIsCensored());
    }

}