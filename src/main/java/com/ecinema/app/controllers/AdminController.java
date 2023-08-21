package com.ecinema.app.controllers;

import com.ecinema.app.domain.dtos.MovieDto;
import com.ecinema.app.domain.dtos.ScreeningDto;
import com.ecinema.app.domain.dtos.ShowroomDto;
import com.ecinema.app.domain.enums.Letter;
import com.ecinema.app.domain.forms.*;
import com.ecinema.app.exceptions.ClashException;
import com.ecinema.app.exceptions.InvalidArgumentException;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Admin controller.
 */
@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;
    private final MovieService movieService;
    private final ShowroomService showroomService;
    private final ScreeningService screeningService;
    private final RegistrationService registrationService;
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    /**
     * Show add movie page string.
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/add-movie")
    public String showAddMoviePage(final Model model) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: add movie page");
        model.addAttribute("action", "/add-movie");
        model.addAttribute("movieForm", new MovieForm());
        model.addAttribute("maxDate", LocalDate.now());
        model.addAttribute("minDate", LocalDate.now().minusYears(120));
        return "admin-movie";
    }

    /**
     * Add movie string.
     *
     * @param redirectAttributes the redirect attributes
     * @param movieForm          the movie form
     * @return the string
     */
    @PostMapping("/add-movie")
    public String addMovie(final RedirectAttributes redirectAttributes,
                           @ModelAttribute("movieForm") final MovieForm movieForm) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Post mapping: add movie");
        logger.debug("Movie form: " + movieForm);
        try {
            Long id = movieService.submitMovieForm(movieForm);
            redirectAttributes.addFlashAttribute("success", "Successfully added movie!\n " +
                            "Please verify that the information displayed on this page is correct.");
            return "redirect:/movie-info?id=" + id;
        } catch (ClashException | InvalidArgumentException e) {
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            redirectAttributes.addFlashAttribute("movieForm", movieForm);
            return "redirect:/admin-movie";
        }
    }

    /**
     * Show edit movie search page string.
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/edit-movie-search")
    public String showEditMovieSearchPage(final Model model) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: edit movie search");
        model.addAttribute("href", "/edit-movie");
        List<MovieDto> movies = movieService.findAll();
        logger.debug("Movies: " + movies);
        model.addAttribute("movies", movies);
        return "admin-movie-choose";
    }

    /**
     * Show edit movie page string.
     *
     * @param model   the model
     * @param movieId the id of the movie to be edited
     * @return the string
     */
    @GetMapping("/edit-movie")
    public String showEditMoviePage(final Model model, @RequestParam("id") final Long movieId) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: edit movie with id " + movieId);
        model.addAttribute("action", "/edit-movie");
        MovieForm movieForm = movieService.fetchAsForm(movieId);
        logger.debug("Movie form: " + movieForm);
        model.addAttribute("movieForm", movieForm);
        model.addAttribute("maxDate", LocalDateTime.now());
        model.addAttribute("minDate", LocalDateTime.now().minusYears(120));
        return "admin-movie";
    }

    /**
     * Edit movie string.
     *
     * @param redirectAttributes the redirect attributes
     * @param movieForm          the movie form
     * @param movieId            the movie id
     * @return the string
     */
    @PostMapping("/edit-movie/{id}")
    public String editMovie(final RedirectAttributes redirectAttributes,
                            @ModelAttribute("movieForm") final MovieForm movieForm,
                            @PathVariable("id") final Long movieId) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Post mapping: edit movie with id " + movieId);
        try {
            movieForm.setId(movieId);
            logger.debug("Movie form: " + movieForm);
            movieService.submitMovieForm(movieForm);
            redirectAttributes.addFlashAttribute("success", "Successfully edited movie!\n" +
                    "Please verify that the information displayed on this page is correct.");
            return "redirect:/movie-info?id=" + movieId;
        } catch (InvalidArgumentException e) {
            redirectAttributes.addAttribute("errors", e.getErrors());
            return "redirect:/edit-movie?id=" + movieId;
        }
    }

    /**
     * Show delete movie search page string.
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/delete-movie-search")
    public String showDeleteMovieSearchPage(final Model model) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: delete movie search");
        model.addAttribute("href", "/delete-movie");
        List<MovieDto> movies = movieService.findAll();
        logger.debug("Movies: " + movies);
        model.addAttribute("movies", movies);
        return "admin-movie-choose";
    }

    /**
     * Show delete movie page string.
     *
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @param id                 the id
     * @return the string
     */
    @GetMapping("/delete-movie")
    public String showDeleteMoviePage(final Model model, final RedirectAttributes redirectAttributes,
                                      @RequestParam("id") final Long id) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        try {
            MovieDto movieDto = movieService.findById(id);
            model.addAttribute("movie", movieDto);
            model.addAttribute("movieId", id);
            List<String> onDeleteInfo = movieService.onDeleteInfo(id);
            model.addAttribute("onDeleteInfo", onDeleteInfo);
            return "delete-movie";
        } catch (NoEntityFoundException e) {
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            return "redirect:/error";
        }
    }

    /**
     * Delete movie string.
     *
     * @param redirectAttributes the redirect attributes
     * @param movieId            the movie id
     * @return the string
     */
    @PostMapping("/delete-movie/{id}")
    public String deleteMovie(final RedirectAttributes redirectAttributes,
                              @PathVariable("id") final Long movieId) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Post mapping: delete movie");
        movieService.delete(movieId);
        redirectAttributes.addFlashAttribute("success", "Successfully deleted movie");
        return "redirect:/delete-movie-search";
    }

    /**
     * Show add screening search page string.
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/add-screening-search")
    public String showAddScreeningSearchPage(final Model model) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: add screening search");
        model.addAttribute("href", "/add-screening");
        model.addAttribute("movies", movieService.findAll());
        return "admin-movie-choose";
    }

    /**
     * Show add screening page string.
     *
     * @param model   the model
     * @param movieId the movie id
     * @return the string
     */
    @GetMapping("/add-screening")
    public String showAddScreeningPage(final Model model, @RequestParam("id") final Long movieId) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: add screening");
        MovieDto movieDto = movieService.findById(movieId);
        logger.debug("Movie DTO: " + movieDto);
        model.addAttribute("movie", movieDto);
        List<ShowroomDto> showrooms = showroomService.findAll();
        logger.debug("Showrooms: " + showrooms);
        model.addAttribute("showrooms", showrooms);
        model.addAttribute("action", "/add-screening/{id}");
        model.addAttribute("screeningForm", new ScreeningForm());
        String minDate = LocalDateTime.now().toLocalDate().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String maxDate = LocalDateTime.now().plusYears(2).format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        logger.debug("Min date: " + minDate);
        logger.debug("Max date: " + maxDate);
        model.addAttribute("minDate", minDate);
        model.addAttribute("maxDate", maxDate);
        return "add-screening";
    }

    /**
     * Add screening string.
     *
     * @param redirectAttributes the redirect attributes
     * @param screeningForm      the screening form
     * @param movieId            the movie id
     * @return the string
     */
    @PostMapping("/add-screening/{id}")
    public String addScreening(final RedirectAttributes redirectAttributes,
                               @ModelAttribute("screeningForm") final ScreeningForm screeningForm,
                               @PathVariable("id") final Long movieId) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Post mapping: add screening");
        try {
            screeningForm.setMovieId(movieId);
            logger.debug("Screening form: " + screeningForm);
            screeningService.submitScreeningForm(screeningForm);
            redirectAttributes.addFlashAttribute("success", "Successfully added screening");
            logger.debug("Success!");
            return "redirect:/add-screening-search";
        } catch (NoEntityFoundException | InvalidArgumentException | ClashException e) {
            logger.debug("ERROR!");
            redirectAttributes.addAttribute("errors", e.getErrors());
            return "redirect:/add-screening?id=" + movieId;
        }
    }

    /**
     * Show add showroom page string.
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/add-showroom")
    public String showAddShowroomPage(final Model model) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: add showroom");
        model.addAttribute("showroomForm", new ShowroomForm());
        List<Letter> showroomLettersAlreadyInUse = showroomService.findAllShowroomLetters();
        List<Letter> availableShowroomLetters = Stream.of(Letter.values()).filter(
                letter -> !showroomLettersAlreadyInUse.contains(letter)).collect(
                Collectors.toCollection(ArrayList::new));
        logger.debug("Showroom letters already in use: " + showroomLettersAlreadyInUse);
        logger.debug("Available showroom letters: " + availableShowroomLetters);
        model.addAttribute("availableShowroomLetters", availableShowroomLetters);
        return "add-showroom";
    }

    /**
     * Add showroom string.
     *
     * @param redirectAttributes the redirect attributes
     * @param showroomForm       the showroom form
     * @return the string
     */
    @PostMapping("/add-showroom")
    public String addShowroom(final RedirectAttributes redirectAttributes,
                              @ModelAttribute("showroomForm") final ShowroomForm showroomForm) {
        try {
            logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
            logger.debug("Post mapping: add showroom");
            logger.debug("Showroom form: " + showroomForm);
            showroomService.submitShowroomForm(showroomForm);
            logger.debug("Successfully added new showroom");
            redirectAttributes.addFlashAttribute(
                    "success", "Successfully added new Showroom " +
                            showroomForm.getShowroomLetter());
            return "redirect:/management";
        } catch (ClashException | InvalidArgumentException e) {
            logger.debug("Errors: " + e.getErrors());
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            redirectAttributes.addFlashAttribute("showroomForm", showroomForm);
            return "redirect:/add-showroom";
        }
    }

    /**
     * Show choose screening to delete page string.
     *
     * @param model  the model
     * @param page   the page
     * @param search the search
     * @return the string
     */
    @GetMapping("/choose-screening-to-delete")
    public String showChooseScreeningToDeletePage(
            final Model model,
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer page,
            @RequestParam(value = "search", required = false, defaultValue = "") final String search) {
        List<String> showroomLettersInUse = showroomService.findAllShowroomLetters()
                                                                   .stream().map(Letter::name)
                                                                   .collect(Collectors.toList());
        model.addAttribute("showroomLettersInUse", showroomLettersInUse);
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<ScreeningDto> screeningDtos = search.isBlank() ? screeningService.findAll(pageRequest) :
                screeningService.findAllByMovieWithTitleLike(search, pageRequest);
        UtilMethods.addPageNumbersAttribute(model, screeningDtos);
        model.addAttribute("screenings", screeningDtos.getContent());
        model.addAttribute("page", page);
        model.addAttribute("search", search);
        return "choose-screening-to-delete";
    }

    /**
     * Show delete screening page string.
     *
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @param screeningId        the screening id
     * @return the string
     */
    @GetMapping("/delete-screening")
    public String showDeleteScreeningPage(final Model model, final RedirectAttributes redirectAttributes,
                                          @RequestParam("id") final Long screeningId) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: delete screening");
        logger.debug("Screening id: " + screeningId);
        try {
            ScreeningDto screeningDto = screeningService.findById(screeningId);
            logger.debug("Screening DTO: " + screeningDto);
            model.addAttribute("screening", screeningDto);
            List<String> onDeleteInfo = screeningService.onDeleteInfo(screeningId);
            logger.debug("On delete info: " + onDeleteInfo);
            model.addAttribute("onDeleteInfo", onDeleteInfo);
            return "delete-screening";
        } catch (NoEntityFoundException e) {
            logger.debug("Errors: " + e.getErrors());
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            logger.debug("Redirecting to choose-screening-to-delete");
            return "redirect:/choose-screening-to-delete";
        }
    }

    /**
     * Delete screening string.
     *
     * @param redirectAttributes the redirect attributes
     * @param screeningId        the screening id
     * @return the string
     */
    @PostMapping("/delete-screening/{id}")
    public String deleteScreening(final RedirectAttributes redirectAttributes,
                                  @PathVariable("id") final Long screeningId) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Post mapping: delete screening");
        logger.debug("Screening id: " + screeningId);
        try {
            screeningService.delete(screeningId);
            logger.debug("Successfully deleted screening");
            redirectAttributes.addFlashAttribute("success", "Successfully deleted screening");
        } catch (NoEntityFoundException e) {
            logger.debug("Errors: " + e.getErrors());
            e.getErrors().add("ERROR: Forced to abort action");
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
        }
        return "redirect:/management";
    }

    /**
     * Show delete showroom page string.
     *
     * @param model              the model
     * @param showroomLetterForm the showroom letter form
     * @return the string
     */
    @GetMapping("/choose-showroom-to-delete")
    public String showDeleteShowroomPage(final Model model,
                                         @ModelAttribute("showroomLetterForm") final StringForm showroomLetterForm) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: delete showroom");
        model.addAttribute("showroomLetterForm", showroomLetterForm);
        List<String> showroomLettersAlreadyInUse = showroomService.findAllShowroomLetters()
                .stream().map(Letter::name).collect(Collectors.toList());
        logger.debug("Showroom letters already in use: " + showroomLettersAlreadyInUse);
        model.addAttribute("showroomLetters", showroomLettersAlreadyInUse);
        return "choose-showroom-to-delete";
    }

    /**
     * Show delete showroom page string.
     *
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @param showroomLetterForm the showroom letter form
     * @return the string
     */
    @GetMapping("/delete-showroom")
    public String showDeleteShowroomPage(final Model model, final RedirectAttributes redirectAttributes,
                                         @ModelAttribute("showroomLetterForm")
                                         final StringForm showroomLetterForm) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: delete showroom");
        logger.debug("Showroom letter form: " + showroomLetterForm);
        try {
            if (showroomLetterForm.getS().isBlank()) {
                throw new InvalidArgumentException("Showroom letter cannot be blank");
            }
            Letter showroomLetter = Letter.valueOf(showroomLetterForm.getS());
            if (!showroomService.existsByShowroomLetter(showroomLetter)) {
                throw new NoEntityFoundException("showroom", "showroom letter", showroomLetterForm.getS());
            }
            List<String> onDeleteInfo = showroomService.onDeleteInfo(showroomLetter);
            logger.debug("On delete info: " + onDeleteInfo);
            model.addAttribute("onDeleteInfo", onDeleteInfo);
            model.addAttribute("showroomLetterForm", showroomLetterForm);
            return "delete-showroom";
        } catch (InvalidArgumentException | NoEntityFoundException e) {
            logger.debug("Errors: " + e.getErrors());
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            redirectAttributes.addFlashAttribute("showroomLetterForm", showroomLetterForm);
            logger.debug("Redirecting to choose-showroom-to-delete");
            return "redirect:/choose-showroom-to-delete";
        }
    }

    /**
     * Delete showroom string.
     *
     * @param redirectAttributes the redirect attributes
     * @param showroomLetterStr  the showroom letter str
     * @return the string
     */
    @PostMapping("/delete-showroom/{showroomLetter}")
    public String deleteShowroom(final RedirectAttributes redirectAttributes,
                                 @PathVariable("showroomLetter") final String showroomLetterStr) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Post mapping: delete showroom");
        Letter showroomLetter = Letter.valueOf(showroomLetterStr);
        logger.debug("Showroom letter: " + showroomLetter);
        try {
            showroomService.delete(showroomLetter);
            redirectAttributes.addFlashAttribute(
                    "success", "Successfully deleted showroom " + showroomLetter);
            logger.debug("Successfully deleted showroom " + showroomLetter);
        } catch (NoEntityFoundException e) {
            e.getErrors().add("Forced to abort action");
            logger.debug("Errors: " + e.getErrors());
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
        }
        return "redirect:/management";
    }

    /**
     * Show admin create new account page string.
     *
     * @param model            the model
     * @param registrationForm the registration form
     * @return the string
     */
    @GetMapping("/admin-create-new-account")
    public String showAdminCreateNewAccountPage(
            final Model model, @ModelAttribute("registrationForm") final RegistrationForm registrationForm) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: admin create new account");
        UtilMethods.addRegistrationPageAttributes(model, registrationForm);
        model.addAttribute("registrationForm", registrationForm);
        return "admin-create-new-account";
    }

    /**
     * Admin create new account string.
     *
     * @param redirectAttributes the redirect attributes
     * @param registrationForm   the registration form
     * @return the string
     */
    @PostMapping("/admin-create-new-account")
    public String adminCreateNewAccount(final RedirectAttributes redirectAttributes,
                                        @ModelAttribute("registrationForm") final RegistrationForm registrationForm) {
        try {
            logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
            logger.debug("Post mapping: admin create new account");
            logger.debug("Registration form: " + registrationForm);
            registrationService.submitRegistrationForm(registrationForm);
            redirectAttributes.addFlashAttribute(
                    "success", "Successfully submitted registration form. Check email " +
                    "and click on link to finalize registration");
            return "redirect:/management";
        } catch (InvalidArgumentException | ClashException e) {
            logger.debug("Errors: " + e);
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            redirectAttributes.addFlashAttribute("registrationForm", registrationForm);
            return "redirect:/admin-create-new-account";
        }
    }

    /**
     * Show admin change user password page string.
     *
     * @param model the model
     * @param form  the form
     * @return the string
     */
    @GetMapping("/admin-change-user-password")
    public String showAdminChangeUserPasswordPage(
            final Model model, @ModelAttribute("form") final AdminChangeUserPasswordForm form) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: admin change user password");
        logger.debug("Admin change user password form: " + form);
        model.addAttribute("form", form);
        return "admin-change-user-password";
    }

    /**
     * Admin change user password string.
     *
     * @param redirectAttributes the redirect attributes
     * @param form               the form
     * @return the string
     */
    @PostMapping("/admin-change-user-password")
    public String adminChangeUserPassword(final RedirectAttributes redirectAttributes,
                                          @ModelAttribute("form") final AdminChangeUserPasswordForm form) {
        try {
            logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
            logger.debug("Post mapping: admin change user password");
            logger.debug("Admin change user password form: " + form);
            adminService.changeUserPassword(form);
            redirectAttributes.addFlashAttribute(
                    "success", "Successfully changed user password");
            return "redirect:/management";
        } catch (NoEntityFoundException | InvalidArgumentException e) {
            logger.debug("Errors: " + e);
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            redirectAttributes.addFlashAttribute("form", form);
            return "redirect:/admin-change-user-password";
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    private String handleIllegalArgumentException(final IllegalArgumentException e,
                                                  final RedirectAttributes redirectAttributes) {
        List<String> errors = List.of(e.getMessage(), "ERROR: Failed to convert from String " +
                "to Enum type letter. Forced to abort action.");
        logger.debug("Errors: " + errors);
        redirectAttributes.addFlashAttribute("errors", errors);
        logger.debug("Redirecting to management");
        return "redirect:/management";
    }

}
