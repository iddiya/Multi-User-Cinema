package com.ecinema.app.services;

import com.ecinema.app.domain.dtos.ReviewVoteDto;
import com.ecinema.app.domain.entities.Customer;
import com.ecinema.app.domain.entities.Review;
import com.ecinema.app.domain.entities.ReviewVote;
import com.ecinema.app.domain.enums.Vote;
import com.ecinema.app.exceptions.InvalidAssociationException;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.repositories.CustomerRepository;
import com.ecinema.app.repositories.ReviewRepository;
import com.ecinema.app.repositories.ReviewVoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ReviewVoteService extends AbstractEntityService<ReviewVote, ReviewVoteRepository, ReviewVoteDto> {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;

    public ReviewVoteService(ReviewVoteRepository repository, ReviewRepository reviewRepository,
                             CustomerRepository customerRepository) {
        super(repository);
        this.reviewRepository = reviewRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public ReviewVoteDto convertToDto(ReviewVote reviewVote)
            throws NoEntityFoundException {
        ReviewVoteDto reviewVoteDto = new ReviewVoteDto();
        reviewVoteDto.setId(reviewVote.getId());
        reviewVoteDto.setVote(reviewVote.getVote());
        reviewVoteDto.setReviewId(
                repository.findReviewIdByReviewVoteWithId(reviewVote.getId())
                          .orElseThrow(() -> new NoEntityFoundException(
                                  "review id", "review vote id", reviewVote.getId())));
        reviewVoteDto.setUserId(
                repository.findUserIdByReviewVoteWithId(reviewVote.getId())
                          .orElseThrow(() -> new NoEntityFoundException(
                                  "user id", "review vote id", reviewVote.getId())));
        reviewVoteDto.setVoterId(
                repository.findVoterIdByReviewVoteWithId(reviewVote.getId())
                          .orElseThrow(() -> new NoEntityFoundException(
                                  "customer id", "review vote id", reviewVote.getId())));
        return reviewVoteDto;
    }

    @Override
    protected void onDelete(ReviewVote reviewVote) {
        logger.debug("Review Vote on delete");
        // Detach Review
        Review review = reviewVote.getReview();
        if (review != null) {
            logger.debug("Detaching Review: " + review);
            reviewVote.setReview(null);
            review.getReviewVotes().remove(reviewVote);
        }
        // Detach Customer
        Customer customer = reviewVote.getVoter();
        if (customer != null) {
            logger.debug("Detaching Customer: " + customer);
            reviewVote.setVoter(null);
            customer.getReviewVotes().remove(reviewVote);
        }
    }

    public void voteOnReview(Long userId, Long reviewId, Vote vote)
            throws InvalidAssociationException, NoEntityFoundException {
        if (reviewRepository.existsByUserWithIdAndReviewWithId(userId, reviewId)) {
            throw new InvalidAssociationException("User cannot vote for his/her own review");
        }
        if (!customerRepository.existsByUserWithId(userId)) {
            throw new NoEntityFoundException("customer", "user id", userId);
        }
        ReviewVote reviewVote = repository.findByUserWithIdAndReviewWithId(
                userId, reviewId).orElseGet(() -> {
            Customer customer = customerRepository.findByUserWithId(userId).orElseThrow(
                    () -> new NoEntityFoundException("customer", "user id", userId));
            Review review = reviewRepository.findById(reviewId).orElseThrow(
                    () -> new NoEntityFoundException("review", "id", reviewId));
            ReviewVote newReviewVote = new ReviewVote();
            newReviewVote.setReview(review);
            review.getReviewVotes().add(newReviewVote);
            newReviewVote.setVoter(customer);
            customer.getReviewVotes().add(newReviewVote);
            return newReviewVote;
        });
        reviewVote.setVote(vote);
        repository.save(reviewVote);
    }

    public Map<Vote, List<Long>> mapOfVoterUserIds(Long reviewId) {
        return new EnumMap<>(Vote.class) {{
           put(Vote.UPVOTE, repository.findAllUserIdsByReviewWithIdAndVote(reviewId, Vote.UPVOTE));
           put(Vote.DOWNVOTE, repository.findAllUserIdsByReviewWithIdAndVote(reviewId, Vote.DOWNVOTE));
        }};
    }

}
