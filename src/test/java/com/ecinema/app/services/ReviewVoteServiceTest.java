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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewVoteServiceTest {

    private ReviewVoteService reviewVoteService;
    @Mock
    private ReviewVoteRepository reviewVoteRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        reviewVoteService = new ReviewVoteService(
                reviewVoteRepository, reviewRepository, customerRepository);
    }

    @Test
    void convertToDto() {
        // given
        ReviewVote reviewVote = new ReviewVote();
        reviewVote.setId(1L);
        reviewVote.setVote(Vote.UPVOTE);
        given(reviewVoteRepository.findReviewIdByReviewVoteWithId(1L))
                .willReturn(Optional.of(2L));
        given(reviewVoteRepository.findUserIdByReviewVoteWithId(1L))
                .willReturn(Optional.of(3L));
        given(reviewVoteRepository.findVoterIdByReviewVoteWithId(1L))
                .willReturn(Optional.of(4L));
        // when
        ReviewVoteDto reviewVoteDto = reviewVoteService.convertToDto(reviewVote);
        // then
        assertEquals(Vote.UPVOTE, reviewVoteDto.getVote());
        assertEquals(2L, reviewVoteDto.getReviewId());
        assertEquals(4L, reviewVoteDto.getVoterId());
        assertEquals(3L, reviewVoteDto.getUserId());
    }

    @Test
    void cascadeOnDelete() {
        // given
        ReviewVote reviewVote = new ReviewVote();
        Review review = new Review();
        reviewVote.setReview(review);
        review.getReviewVotes().add(reviewVote);
        Customer customer = new Customer();
        reviewVote.setVoter(customer);
        customer.getReviewVotes().add(reviewVote);
        assertEquals(review, reviewVote.getReview());
        assertEquals(customer, reviewVote.getVoter());
        assertTrue(review.getReviewVotes().contains(reviewVote));
        assertTrue(customer.getReviewVotes().contains(reviewVote));
        // when
        reviewVoteService.onDelete(reviewVote);
        // then
        assertNull(reviewVote.getReview());
        assertNull(reviewVote.getVoter());
        assertFalse(review.getReviewVotes().contains(reviewVote));
        assertFalse(customer.getReviewVotes().contains(reviewVote));
    }

    @Test
    void voteOnReview1() {
        // given
        given(reviewVoteRepository.findByUserWithIdAndReviewWithId(
                1L, 2L)).willReturn(Optional.empty());
        Review review = new Review();
        review.setId(2L);
        given(reviewRepository.findById(2L)).willReturn(
                Optional.of(review));
        Customer customer = new Customer();
        customer.setId(3L);
        given(customerRepository.findByUserWithId(1L)).willReturn(
                Optional.of(customer));
        given(customerRepository.existsByUserWithId(1L)).willReturn(true);
        // when
        reviewVoteService.voteOnReview(1L, 2L, Vote.UPVOTE);
        // then
        ArgumentCaptor<ReviewVote> reviewVoteArgumentCaptor = ArgumentCaptor.forClass(ReviewVote.class);
        verify(reviewVoteRepository).save(reviewVoteArgumentCaptor.capture());
        ReviewVote reviewVote = reviewVoteArgumentCaptor.getValue();
        assertEquals(review, reviewVote.getReview());
        assertEquals(customer, reviewVote.getVoter());
        assertEquals(Vote.UPVOTE, reviewVote.getVote());
        assertTrue(review.getReviewVotes().contains(reviewVote));
        assertTrue(customer.getReviewVotes().contains(reviewVote));
    }

    @Test
    void voteOnReview2() {
        // given
        given(reviewRepository.existsByUserWithIdAndReviewWithId(1L, 2L))
                .willReturn(false);
        given(customerRepository.existsByUserWithId(1L)).willReturn(true);
        Customer customer = new Customer();
        given(customerRepository.findByUserWithId(1L)).willReturn(Optional.of(customer));
        Review review = new Review();
        given(reviewRepository.findById(2L)).willReturn(Optional.of(review));
        // when
        reviewVoteService.voteOnReview(1L, 2L, Vote.DOWNVOTE);
        // then
        ArgumentCaptor<ReviewVote> reviewVoteArgumentCaptor = ArgumentCaptor.forClass(ReviewVote.class);
        verify(reviewVoteRepository).save(reviewVoteArgumentCaptor.capture());
        ReviewVote reviewVote = reviewVoteArgumentCaptor.getValue();
        assertEquals(Vote.DOWNVOTE, reviewVote.getVote());
        assertEquals(review, reviewVote.getReview());
        assertTrue(review.getReviewVotes().contains(reviewVote));
        assertEquals(customer, reviewVote.getVoter());
        assertTrue(customer.getReviewVotes().contains(reviewVote));
    }

    @Test
    void failToVoteOnReview1() {
        // given
        given(reviewRepository.existsByUserWithIdAndReviewWithId(1L, 2L)).willReturn(true);
        // then
        assertThrows(InvalidAssociationException.class,
                     () -> reviewVoteService.voteOnReview(1L, 2L, Vote.UPVOTE));
    }

    @Test
    void failToVoteOnReview2() {
        // given
        given(reviewRepository.existsByUserWithIdAndReviewWithId(1L, 2L))
                .willReturn(false);
        given(customerRepository.existsByUserWithId(1L)).willReturn(false);
        // then
        assertThrows(NoEntityFoundException.class,
                     () -> reviewVoteService.voteOnReview(1L, 2L, Vote.UPVOTE));
    }

    @Test
    void failToVoteOnReview3() {
        // given
        given(reviewRepository.existsByUserWithIdAndReviewWithId(1L, 2L))
                .willReturn(false);
        given(customerRepository.existsByUserWithId(1L)).willReturn(true);
        given(customerRepository.findByUserWithId(1L)).willReturn(Optional.empty());
        // then
        assertThrows(NoEntityFoundException.class,
                     () -> reviewVoteService.voteOnReview(1L, 2L, Vote.UPVOTE));
    }

    @Test
    void failToVoteOnReview4() {
        // given
        given(reviewRepository.existsByUserWithIdAndReviewWithId(1L, 2L))
                .willReturn(false);
        given(customerRepository.existsByUserWithId(1L)).willReturn(true);
        Customer customer = new Customer();
        given(customerRepository.findByUserWithId(1L)).willReturn(Optional.of(customer));
        given(reviewRepository.findById(2L)).willReturn(Optional.empty());
        // then
        assertThrows(NoEntityFoundException.class,
                     () -> reviewVoteService.voteOnReview(1L, 2L, Vote.UPVOTE));
    }

}