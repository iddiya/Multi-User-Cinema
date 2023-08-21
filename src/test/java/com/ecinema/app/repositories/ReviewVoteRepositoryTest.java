package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.Customer;
import com.ecinema.app.domain.entities.Review;
import com.ecinema.app.domain.entities.ReviewVote;
import com.ecinema.app.domain.entities.User;
import com.ecinema.app.domain.enums.UserAuthority;
import com.ecinema.app.domain.enums.Vote;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReviewVoteRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ReviewVoteRepository reviewVoteRepository;

    @Test
    void findAllByReviewId() {
        // given
        Review review = new Review();
        reviewRepository.save(review);
        ReviewVote reviewVote = new ReviewVote();
        reviewVote.setReview(review);
        review.getReviewVotes().add(reviewVote);
        reviewVoteRepository.save(reviewVote);
        // when
        List<ReviewVote> reviewVotes = reviewVoteRepository.findAllByReviewId(review.getId());
        // then
        assertEquals(1, reviewVotes.size());
        assertTrue(reviewVotes.contains(reviewVote));
    }

    @Test
    void findAllByVoterId() {
        // given
        Customer customer = new Customer();
        customerRepository.save(customer);
        ReviewVote reviewVote = new ReviewVote();
        reviewVote.setVoter(customer);
        customer.getReviewVotes().add(reviewVote);
        reviewVoteRepository.save(reviewVote);
        // when
        List<ReviewVote> reviewVotes = reviewVoteRepository.findAllByVoterId(customer.getId());
        // then
        assertEquals(1, reviewVotes.size());
        assertTrue(reviewVotes.contains(reviewVote));
    }

    @Test
    void findAllByUserId() {
        // given
        User user = new User();
        userRepository.save(user);
        Customer customer = new Customer();
        customer.setUser(user);
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
        customerRepository.save(customer);
        ReviewVote reviewVote = new ReviewVote();
        reviewVote.setVoter(customer);
        customer.getReviewVotes().add(reviewVote);
        reviewVoteRepository.save(reviewVote);
        // when
        List<ReviewVote> reviewVotes = reviewVoteRepository.findAllByUserId(user.getId());
        // then
        assertEquals(1, reviewVotes.size());
        assertTrue(reviewVotes.contains(reviewVote));
    }

    @Test
    void findUserIdByReviewVoteWithId() {
        // given
        User user = new User();
        userRepository.save(user);
        Customer customer = new Customer();
        customer.setUser(user);
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
        customerRepository.save(customer);
        ReviewVote reviewVote = new ReviewVote();
        reviewVote.setVoter(customer);
        customer.getReviewVotes().add(reviewVote);
        reviewVoteRepository.save(reviewVote);
        // when
        Optional<Long> userIdOptional = reviewVoteRepository
                .findUserIdByReviewVoteWithId(reviewVote.getId());
        // then
        assertTrue(userIdOptional.isPresent());
        assertEquals(user.getId(), userIdOptional.get());
    }

    @Test
    void findVoterIdByReviewVoteWithId() {
        // given
        Customer customer = new Customer();
        customerRepository.save(customer);
        ReviewVote reviewVote = new ReviewVote();
        reviewVote.setVoter(customer);
        customer.getReviewVotes().add(reviewVote);
        reviewVoteRepository.save(reviewVote);
        // when
        Optional<Long> customerIdOptional = reviewVoteRepository
                .findVoterIdByReviewVoteWithId(reviewVote.getId());
        // then
        assertTrue(customerIdOptional.isPresent());
        assertEquals(customer.getId(), customerIdOptional.get());
    }

    @Test
    void findReviewIdByReviewVoteWithId() {
        // given
        Review review = new Review();
        reviewRepository.save(review);
        ReviewVote reviewVote = new ReviewVote();
        reviewVote.setReview(review);
        review.getReviewVotes().add(reviewVote);
        reviewVoteRepository.save(reviewVote);
        // when
        Optional<Long> reviewIdOptional = reviewVoteRepository
                .findReviewIdByReviewVoteWithId(reviewVote.getId());
        // then
        assertTrue(reviewIdOptional.isPresent());
        assertEquals(review.getId(), reviewIdOptional.get());
    }

    @Test
    void existsByCustomerAndReview() {
        // given
        Customer customer = new Customer();
        customerRepository.save(customer);
        Review review = new Review();
        reviewRepository.save(review);
        ReviewVote reviewVote = new ReviewVote();
        reviewVote.setReview(review);
        review.getReviewVotes().add(reviewVote);
        reviewVote.setVoter(customer);
        reviewVoteRepository.save(reviewVote);
        // when
        boolean existsByCustomerAndReview = reviewVoteRepository
                .existsByCustomerAndReview(customer, review);
        // then
        assertTrue(existsByCustomerAndReview);
    }

    @Test
    void existsByUserWithIdAndReviewWithId() {
        // given
        Review review = new Review();
        reviewRepository.save(review);
        User user = new User();
        userRepository.save(user);
        Customer customer = new Customer();
        customer.setUser(user);
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
        customerRepository.save(customer);
        ReviewVote reviewVote = new ReviewVote();
        reviewVote.setVoter(customer);
        customer.getReviewVotes().add(reviewVote);
        reviewVote.setReview(review);
        review.getReviewVotes().add(reviewVote);
        reviewVoteRepository.save(reviewVote);
        // when
        boolean existsByUserWithIdAndReviewWithid = reviewVoteRepository
                .existsByUserWithIdAndReviewWithId(user.getId(), review.getId());
        // then
        assertTrue(existsByUserWithIdAndReviewWithid);
    }

    @Test
    void findByUserWithIdAndReviewWithId() {
        // given
        Review review = new Review();
        reviewRepository.save(review);
        User user = new User();
        userRepository.save(user);
        Customer customer = new Customer();
        customer.setUser(user);
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
        customerRepository.save(customer);
        ReviewVote reviewVote = new ReviewVote();
        reviewVote.setVoter(customer);
        customer.getReviewVotes().add(reviewVote);
        reviewVote.setReview(review);
        review.getReviewVotes().add(reviewVote);
        reviewVoteRepository.save(reviewVote);
        // when
        Optional<ReviewVote> reviewVoteOptional = reviewVoteRepository
                .findByUserWithIdAndReviewWithId(user.getId(), review.getId());
        // then
        assertTrue(reviewVoteOptional.isPresent());
        assertEquals(reviewVote, reviewVoteOptional.get());
    }

    @Test
    void findByVoterAndReview() {
        // given
        Review review = new Review();
        reviewRepository.save(review);
        Customer customer = new Customer();
        customerRepository.save(customer);
        ReviewVote reviewVote = new ReviewVote();
        reviewVote.setVoter(customer);
        customer.getReviewVotes().add(reviewVote);
        reviewVote.setReview(review);
        review.getReviewVotes().add(reviewVote);
        reviewVoteRepository.save(reviewVote);
        // when
        Optional<ReviewVote> reviewVoteOptional = reviewVoteRepository
                .findByVoterAndReview(customer, review);
        // then
        assertTrue(reviewVoteOptional.isPresent());
        assertEquals(reviewVote, reviewVoteOptional.get());
    }

    @Test
    void findAllUserIdsByReviewWithIdAndVote() {
        // given
        Map<Vote, List<Long>> mapOfUserIds = new EnumMap<>(Vote.class) {{
            put(Vote.UPVOTE, new ArrayList<>());
            put(Vote.DOWNVOTE, new ArrayList<>());
        }};
        Review review = new Review();
        reviewRepository.save(review);
        for (int i = 0; i < 10; i++) {
            User user = new User();
            userRepository.save(user);
            Customer customer = new Customer();
            customer.setUser(user);
            user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
            customerRepository.save(customer);
            Vote vote = i % 2 == 0 ? Vote.UPVOTE : Vote.DOWNVOTE;
            ReviewVote reviewVote = new ReviewVote();
            reviewVote.setVote(vote);
            reviewVote.setReview(review);
            review.getReviewVotes().add(reviewVote);
            reviewVote.setVoter(customer);
            customer.getReviewVotes().add(reviewVote);
            reviewVoteRepository.save(reviewVote);
            mapOfUserIds.get(vote).add(user.getId());
        }
        // when
        List<Long> upvoteUserIds = reviewVoteRepository
                .findAllUserIdsByReviewWithIdAndVote(review.getId(), Vote.UPVOTE);
        List<Long> downvoteUserIds = reviewVoteRepository
                .findAllUserIdsByReviewWithIdAndVote(review.getId(), Vote.DOWNVOTE);
        // then
        assertEquals(mapOfUserIds.get(Vote.UPVOTE), upvoteUserIds);
        assertEquals(mapOfUserIds.get(Vote.DOWNVOTE), downvoteUserIds);
    }

}