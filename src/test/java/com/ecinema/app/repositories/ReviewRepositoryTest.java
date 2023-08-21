package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.Customer;
import com.ecinema.app.domain.entities.Movie;
import com.ecinema.app.domain.entities.Review;
import com.ecinema.app.domain.entities.User;
import com.ecinema.app.domain.enums.UserAuthority;
import com.ecinema.app.util.UtilMethods;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll();
        movieRepository.deleteAll();
    }

    @Test
    void findAllByMovie() {
        // given
        Movie movie1 = new Movie();
        Movie movie2 = new Movie();
        movieRepository.save(movie1);
        movieRepository.save(movie2);
        List<Review> reviews = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Review review = new Review();
            Movie movie = i % 2 == 0 ? movie1 : movie2;
            review.setMovie(movie);
            movie.getReviews().add(review);
            reviewRepository.save(review);
            reviews.add(review);
        }
        List<Review> control = reviews.stream()
                .filter(review -> review.getMovie().equals(movie1))
                .collect(Collectors.toList());
        // when
        List<Review> test = reviewRepository.findAllByMovie(movie1);
        // then
        assertEquals(control, test);
    }

    @Test
    void findAllByMovieWithId() {
        // given
        Movie movie1 = new Movie();
        Movie movie2 = new Movie();
        movieRepository.save(movie1);
        movieRepository.save(movie2);
        List<Review> reviews = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Review review = new Review();
            Movie movie = i % 2 == 0 ? movie1 : movie2;
            review.setMovie(movie);
            movie.getReviews().add(review);
            reviewRepository.save(review);
            reviews.add(review);
        }
        List<Review> control = reviews.stream()
                .filter(review -> review.getMovie().getId().equals(movie1.getId()))
                .collect(Collectors.toList());
        // when
        List<Review> test = reviewRepository.findAllByMovieWithId(movie1.getId());
        // then
        assertEquals(control, test);
    }

    @Test
    void findAverageOfReviews() {
        // given
        Movie movie = new Movie();
        movieRepository.save(movie);
        List<Integer> ratings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Review review = new Review();
            review.setMovie(movie);
            movie.getReviews().add(review);
            Integer rating = UtilMethods.randomIntBetween(0, 10);
            ratings.add(rating);
            review.setRating(rating);
            reviewRepository.save(review);
        }
        Double avgRating = 0D;
        for (Integer rating : ratings) {
            avgRating += rating;
        }
        avgRating /= ratings.size();
        Integer controlAvgRating = (int) Math.floor(avgRating);
        // when
        Integer testAvgRating = reviewRepository.findAverageOfReviewRatingsForMovie(movie);
        // then
        assertEquals(controlAvgRating, testAvgRating);
    }

    @Test
    void findByUserIdAndMovieId() {
        // given
        User user = new User();
        userRepository.save(user);
        Customer customer = new Customer();
        customer.setUser(user);
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
        customerRepository.save(customer);
        Movie movie = new Movie();
        movieRepository.save(movie);
        Review review = new Review();
        review.setMovie(movie);
        movie.getReviews().add(review);
        review.setWriter(customer);
        customer.getReviews().add(review);
        reviewRepository.save(review);
        // when
        boolean test = reviewRepository.existsByUserWithIdAndMovieWithId(user.getId(), movie.getId());
        // then
        assertTrue(test);
    }

    @Test
    void findCustomerIdByReviewWithId() {
        // given
        Customer customer = new Customer();
        customerRepository.save(customer);
        Review review = new Review();
        review.setWriter(customer);
        customer.getReviews().add(review);
        reviewRepository.save(review);
        // when
        Optional<Long> customerIdOptional = reviewRepository.findCustomerIdByReviewWithId(review.getId());
        // then
        assertTrue(customerIdOptional.isPresent());
        assertEquals(customer.getId(), customerIdOptional.get());
    }

    @Test
    void findUserIdByReviewWithId() {
        // given
        User user = new User();
        userRepository.save(user);
        Customer customer = new Customer();
        customer.setUser(user);
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
        customerRepository.save(customer);
        Review review = new Review();
        review.setWriter(customer);
        customer.getReviews().add(review);
        reviewRepository.save(review);
        // when
        Optional<Long> userIdOptional = reviewRepository.findUserIdByReviewWithId(review.getId());
        // then
        assertTrue(userIdOptional.isPresent());
        assertEquals(user.getId(), userIdOptional.get());
    }

    @Test
    void findUsernameOfWriterByReviewWithId() {
        // given
        User user = new User();
        user.setUsername("TestUser123");
        userRepository.save(user);
        Customer customer = new Customer();
        customer.setUser(user);
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
        customerRepository.save(customer);
        Review review = new Review();
        review.setWriter(customer);
        customer.getReviews().add(review);
        reviewRepository.save(review);
        // when
        Optional<String> usernameOptional = reviewRepository
                .findUsernameOfWriterForReviewWithId(review.getId());
        // then
        assertTrue(usernameOptional.isPresent());
        assertEquals(user.getUsername(), usernameOptional.get());
    }

}