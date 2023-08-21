package com.ecinema.app.services;

import com.ecinema.app.domain.dtos.ReviewDto;
import com.ecinema.app.domain.entities.Customer;
import com.ecinema.app.domain.entities.Movie;
import com.ecinema.app.domain.entities.Review;
import com.ecinema.app.domain.enums.Vote;
import com.ecinema.app.domain.forms.ReviewForm;
import com.ecinema.app.validators.ReviewValidator;
import com.ecinema.app.exceptions.*;
import com.ecinema.app.repositories.CustomerRepository;
import com.ecinema.app.repositories.MovieRepository;
import com.ecinema.app.repositories.ReviewRepository;
import com.ecinema.app.util.UtilMethods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReviewService extends AbstractEntityService<Review, ReviewRepository, ReviewDto> {

    private final CustomerRepository customerRepository;
    private final MovieRepository movieRepository;
    private final ReviewValidator reviewValidator;
    private final ReviewVoteService reviewVoteService;

    public ReviewService(ReviewRepository repository, MovieRepository movieRepository,
                         CustomerRepository customerRepository, ReviewValidator reviewValidator,
                         ReviewVoteService reviewVoteService) {
        super(repository);
        this.movieRepository = movieRepository;
        this.reviewValidator = reviewValidator;
        this.reviewVoteService = reviewVoteService;
        this.customerRepository = customerRepository;
    }

    @Override
    protected void onDelete(Review review) {
        logger.debug("Review on delete");
        // detach Customer
        Customer writer = review.getWriter();
        if (writer != null) {
            logger.debug("Detach customer authority: " + writer);
            writer.getReviews().remove(review);
            review.setWriter(null);
        }
        // detach Movie
        Movie movie = review.getMovie();
        if (movie != null) {
            logger.debug("Detach movie: " + movie);
            movie.getReviews().remove(review);
            review.setMovie(null);
        }
        // delete Review Votes
        reviewVoteService.deleteAll(review.getReviewVotes());
    }

    @Override
    public ReviewDto convertToDto(Review review) {
        ReviewDto reviewDTO = new ReviewDto();
        reviewDTO.setToIReview(review);
        reviewDTO.setId(review.getId());
        reviewDTO.setIsCensored(review.getIsCensored());
        reviewDTO.setCreationDateTime(review.getCreationDateTime());
        reviewDTO.setCustomerId(
                repository.findCustomerIdByReviewWithId(review.getId())
                          .orElseThrow(() -> new NoEntityFoundException(
                                  "customer id", "review id", review.getId())));
        reviewDTO.setUserId(
                repository.findUserIdByReviewWithId(review.getId())
                        .orElseThrow(() -> new NoEntityFoundException(
                                "user id", "review id", review.getId())));
        reviewDTO.setWriter(
                repository.findUsernameOfWriterForReviewWithId(review.getId())
                        .orElseThrow(() -> new NoEntityFoundException(
                                "username", "review id", review.getId())));
        Map<Vote, List<Long>> mapOfVoterUserIds = reviewVoteService.mapOfVoterUserIds(review.getId());
        reviewDTO.setUpvoteUserIds(mapOfVoterUserIds.get(Vote.UPVOTE));
        reviewDTO.setDownvoteUserIds(mapOfVoterUserIds.get(Vote.DOWNVOTE));
        logger.debug("convert review to DTO: " + reviewDTO);
        return reviewDTO;
    }

    public void submitReviewForm(ReviewForm reviewForm)
            throws NoEntityFoundException, InvalidArgumentException, ClashException {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Submit review form");
        Customer customer = customerRepository
                .findByUserWithId(reviewForm.getUserId())
                .orElseThrow(() -> new NoEntityFoundException(
                        "customer authority", "user id", reviewForm.getUserId()));
        Movie movie = movieRepository.findById(reviewForm.getMovieId()).orElseThrow(
                () -> new NoEntityFoundException("movie", "id", reviewForm.getMovieId()));
        if (repository.existsByWriterAndMovie(customer, movie)) {
            throw new ClashException(customer.getUser().getUsername() + " has already written " +
                                             "a review for " + movie.getTitle());
        }
        List<String> errors = new ArrayList<>();
        reviewValidator.validate(reviewForm, errors);
        if (!errors.isEmpty()) {
            throw new InvalidArgumentException(errors);
        }
        logger.debug("Submit Review Form: passed validation checks");
        Review review = new Review();
        review.setMovie(movie);
        movie.getReviews().add(review);
        review.setWriter(customer);
        customer.getReviews().add(review);
        review.setRating(reviewForm.getRating());
        review.setReview(reviewForm.getReview());
        review.setCreationDateTime(LocalDateTime.now());
        review.setIsCensored(false);
        repository.save(review);
        logger.debug("Instantiated and saved review for " + movie.getTitle() +
                             " by " + customer.getUser().getUsername());
    }

    public boolean existsByUserIdAndMovieId(Long userId, Long movieId)
            throws NoEntityFoundException {
        return repository.existsByUserWithIdAndMovieWithId(userId, movieId);
    }

    public Page<ReviewDto> findPageByMovieIdAndNotCensored(Long movieId, Pageable pageable) {
        return repository.findAllByMovieWithIdAndNotCensored(movieId, pageable)
                         .map(this::convertToDto);
    }

    public Integer findAverageRatingOfMovieWithId(Long movieId) {
        Integer avgRating = repository.findAverageOfReviewRatingsForMovieWithId(movieId);
        return avgRating == null ? 0 : avgRating;
    }

}

