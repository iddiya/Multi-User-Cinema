package com.ecinema.app.controllers;

import com.ecinema.app.domain.dtos.ScreeningDto;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.services.ScreeningService;
import com.ecinema.app.util.UtilMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.ecinema.app.util.UtilMethods.addPageNumbersAttribute;

/**
 * The type Movie screening controller.
 */
@Controller
@RequiredArgsConstructor
public class MovieScreeningController {

    private final ScreeningService screeningService;
    private final Logger logger = LoggerFactory.getLogger(MovieScreeningController.class);

    /**
     * Movie screenings page string.
     *
     * @param model the model
     * @param id    the id
     * @param page  the page
     * @return the string
     */
    @GetMapping("/movie-screenings")
    public String movieScreeningsPage(
            final Model model, @RequestParam("id") final Long id,
            @RequestParam(value = "page", required = false, defaultValue = "1") final Integer page) {
        model.addAttribute("movieId", id);
        PageRequest pageRequest = PageRequest.of(page - 1, 6);
        Page<ScreeningDto> pageOfDtos = screeningService.findPageByMovieId(id, pageRequest);
        model.addAttribute("screenings", pageOfDtos.getContent().toArray());
        addPageNumbersAttribute(model, pageOfDtos);
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Screening DTOs: " + pageOfDtos);
        return "movie-screenings";
    }

    /**
     * Handle no entity found exception string.
     *
     * @param model the model
     * @param e     the e
     * @return the string
     */
    @ExceptionHandler(NoEntityFoundException.class)
    public String handleNoEntityFoundException(final Model model, final NoEntityFoundException e) {
        model.addAttribute("errors", e.getErrors());
        return "error";
    }

}
