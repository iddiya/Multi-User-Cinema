package com.ecinema.app.controllers;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.domain.dtos.MovieDto;
import com.ecinema.app.domain.dtos.ReviewDto;
import com.ecinema.app.domain.dtos.UserDto;
import com.ecinema.app.domain.enums.UserAuthority;
import com.ecinema.app.domain.enums.Vote;
import com.ecinema.app.domain.forms.ReviewForm;
import com.ecinema.app.exceptions.ClashException;
import com.ecinema.app.exceptions.InvalidArgumentException;
import com.ecinema.app.exceptions.InvalidAssociationException;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.services.*;
import com.ecinema.app.util.UtilMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.ecinema.app.util.UtilMethods.addPageNumbersAttribute;

/**
 * Controls the view pages that predominantly regard {@link com.ecinema.app.domain.entities.Movie} and
 * {@link com.ecinema.app.domain.entities.Review}.
 */
@Controller
@RequiredArgsConstructor
public class MovieReviewController {

    private final UserService userService;
    private final MovieService movieService;
    private final ReviewService reviewService;
    private final CustomerService customerService;
    private final SecurityContext securityContext;
    private final ReviewVoteService reviewVoteService;
    private final Logger logger = LoggerFactory.getLogger(MovieReviewController.class);

    /**
     * Show movie reviews page.
     *
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @param page               the page
     * @param movieId            the movie id
     * @return the view name
     */
    @GetMapping("/movie-reviews")
    public String movieReviewsPage(
            final Model model, final RedirectAttributes redirectAttributes,
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer page,
            @RequestParam("id") final Long movieId) {
        try {
            logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
            Long userId = securityContext.findIdOfLoggedInUser();
            boolean canWriteReview = userId != null &&
                    !reviewService.existsByUserIdAndMovieId(userId, movieId);
            logger.debug("Can write review: " + canWriteReview);
            model.addAttribute("canWriteReview", canWriteReview);
            PageRequest pageRequest = PageRequest.of(page - 1, 6);
            Page<ReviewDto> pageOfDtos = reviewService
                    .findPageByMovieIdAndNotCensored(movieId, pageRequest);
            logger.debug("Review DTOs: " + pageOfDtos.getContent());
            model.addAttribute("reviews", pageOfDtos);
            addPageNumbersAttribute(model, pageOfDtos);
            logger.debug("Page: " + page);
            model.addAttribute("page", page);
            logger.debug("Movie id: " + movieId);
            model.addAttribute("movieId", movieId);
            return "movie-reviews";
        } catch (NoEntityFoundException e) {
            logger.debug("Errors: " + e);
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            return "redirect:/error";
        }
    }

    /**
     * Upvote or downvote review.
     *
     * @param redirectAttributes the redirect attributes
     * @param reviewId           the review id
     * @param movieId            the movie id
     * @param voteOrdinal        the vote ordinal
     * @param page               the page
     * @return the view name
     */
    @PostMapping("/vote-review/{id}/{vote}")
    public String likeReview(final RedirectAttributes redirectAttributes,
                             @PathVariable("id") final Long reviewId,
                             @RequestParam("movieId") final Long movieId,
                             @PathVariable("vote") final Integer voteOrdinal,
                             @RequestParam(value = "page", required = false, defaultValue = "1") final Integer page) {
        try {
            logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
            Long userId = securityContext.findIdOfLoggedInUser();
            logger.debug("User id: " + userId);
            logger.debug("Review id: " + reviewId);
            logger.debug("Movie id: " + movieId);
            logger.debug("Vote ordinal: " + voteOrdinal);
            if (voteOrdinal < 0 && voteOrdinal >= Vote.values().length) {
                throw new InvalidArgumentException(
                        "Vote ordinal must be within range of Vote array indices but was instead " + voteOrdinal);
            }
            Vote vote = Vote.values()[voteOrdinal];
            reviewVoteService.voteOnReview(userId, reviewId, vote);
            logger.debug("Successfully applied " + vote + " to review with id: " + reviewId);
            redirectAttributes.addFlashAttribute("success", "Successfully voted on review");
        } catch (InvalidAssociationException | NoEntityFoundException  | InvalidArgumentException e) {
            logger.debug("Errors: " + e);
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
        }
        return "redirect:/movie-reviews?id=" + movieId + "&page=" + page;
    }

    /**
     * Show write review page.
     *
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @param movieId            the movie id
     * @return the view name
     */
    @GetMapping("/write-review")
    public String showWriteReviewPage(final Model model, final RedirectAttributes redirectAttributes,
                                      @RequestParam("id") final Long movieId) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Write review get mapping");
        Long userId = securityContext.findIdOfLoggedInUser();
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "Fatal error: Forced to logout");
            return "redirect:/logout";
        }
        if (customerService.isCustomerCensored(userId)) {
            List<String> errors = List.of("You cannot write a review because you have been censored by a moderator");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/error";
        }
        if (reviewService.existsByUserIdAndMovieId(userId, movieId)) {
            redirectAttributes.addFlashAttribute(
                    "error", "Customer has already written a review for this movie");
            return "redirect:/movie-reviews?id=" + movieId;
        }
        model.addAttribute("error", "Cannot access write-review page");
        MovieDto movieDto = movieService.findById(movieId);
        model.addAttribute("movie", movieDto);
        logger.debug("Added movie dto to model: " + movieDto);
        ReviewForm reviewForm = new ReviewForm();
        reviewForm.setMovieId(movieId);
        model.addAttribute("reviewForm", reviewForm);
        logger.debug("Added review form to model: " + reviewForm);
        return "write-review";
    }

    /**
     * Write review.
     *
     * @param redirectAttributes the redirect attributes
     * @param reviewForm         the review form
     * @param movieId            the movie id
     * @return the view name
     */
    @PostMapping("/write-review/{id}")
    public String writeReview(final RedirectAttributes redirectAttributes,
                              @ModelAttribute("reviewForm") final ReviewForm reviewForm,
                              @PathVariable("id") final Long movieId) {
        try {
            logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
            Long userId = securityContext.findIdOfLoggedInUser();
            logger.debug("User id: " + userId);
            reviewForm.setUserId(userId);
            logger.debug("Write review post mapping");
            reviewForm.setMovieId(movieId);
            logger.debug("Review form: " + reviewForm);
            reviewService.submitReviewForm(reviewForm);
            redirectAttributes.addFlashAttribute(
                    "success", "Successfully created review for movie");
            logger.debug("Successfully created review for movie");
            return "redirect:/movie-reviews?id=" + movieId;
        } catch (NoEntityFoundException | ClashException e) {
            logger.debug("Errors: " + e);
            redirectAttributes.addAttribute("errors", e.getErrors());
            redirectAttributes.addFlashAttribute("reviewForm", reviewForm);
            return "redirect:/write-review?id=" + movieId;
        }
    }



}
