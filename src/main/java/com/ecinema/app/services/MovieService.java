package com.ecinema.app.services;

import com.ecinema.app.domain.dtos.MovieDto;
import com.ecinema.app.domain.entities.Movie;
import com.ecinema.app.domain.forms.MovieForm;
import com.ecinema.app.validators.MovieValidator;
import com.ecinema.app.exceptions.InvalidArgumentException;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.repositories.MovieRepository;
import com.ecinema.app.util.UtilMethods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MovieService extends AbstractEntityService<Movie, MovieRepository, MovieDto> {

    private final ScreeningService screeningService;
    private final ReviewService reviewService;
    private final MovieValidator movieValidator;

    /**
     * Instantiates a new Movie service.
     *
     * @param repository             the repository
     * @param reviewService          the review service
     * @param screeningService       the screening service
     * @param movieValidator         the movie validator
     */
    public MovieService(MovieRepository repository, ReviewService reviewService,
                        ScreeningService screeningService, MovieValidator movieValidator) {
        super(repository);
        this.reviewService = reviewService;
        this.screeningService = screeningService;
        this.movieValidator = movieValidator;
    }

    public static String convertTitleToSearchTitle(String title) {
        return UtilMethods.removeWhitespace(title).toUpperCase();
    }

    @Override
    protected void onDelete(Movie movie) {
        logger.debug("Movie on delete");
        // cascade delete Reviews
        logger.debug("Deleting all associated reviews");
        reviewService.deleteAll(movie.getReviews());
        // cascade delete Screenings
        logger.debug("Deleting all associated screenings");
        screeningService.deleteAll(movie.getScreenings());
    }

    @Override
    public MovieDto convertToDto(Movie movie) {
        MovieDto movieDTO = new MovieDto();
        movieDTO.setId(movie.getId());
        movieDTO.setToIMovie(movie);
        logger.debug("Converted movie to DTO: " + movieDTO);
        logger.debug("Movie: " + movie);
        return movieDTO;
    }

    public MovieForm fetchAsForm(Long movieId)
            throws NoEntityFoundException {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Fetching movie as form");
        Movie movie = repository.findById(movieId).orElseThrow(
                () -> new NoEntityFoundException("movie", "id", movieId));
        logger.debug("Found movie with id: " + movieId);
        MovieForm movieForm = new MovieForm();
        movieForm.setId(movieId);
        movieForm.setToIMovie(movie);
        logger.debug("Instantiated new movie form: " + movieForm);
        logger.debug("Movie: " + movie);
        return movieForm;
    }

    public Long submitMovieForm(MovieForm movieForm)
            throws InvalidArgumentException {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Submit movie form: " + movieForm);
        List<String> errors = new ArrayList<>();
        movieValidator.validate(movieForm, errors);
        if (!errors.isEmpty()) {
            throw new InvalidArgumentException(errors);
        }
        logger.debug("Movie form passed validation checks");
        Movie movie = movieForm.getId() != null ? repository.findById(movieForm.getId()).orElseThrow(
                () -> new NoEntityFoundException("movie", "id", movieForm.getId())) : new Movie();
        logger.debug("Movie before set to form: " + movie);
        String searchTitle = convertTitleToSearchTitle(movieForm.getTitle());
        movie.setSearchTitle(searchTitle);
        movie.setToIMovie(movieForm);
        logger.debug("Saved movie: " + movie);
        save(movie);
        return movie.getId();
    }

    public MovieDto findByTitle(String title) {
        String searchTitle = convertTitleToSearchTitle(title);
        Movie movie = repository.findBySearchTitle(searchTitle).orElseThrow(
                () -> new NoEntityFoundException("movie", "title", title));
        return convertToDto(movie);
    }

    public Page<MovieDto> findAllByLikeTitle(String title, Pageable pageable) {
        String searchTitle = convertTitleToSearchTitle(title);
        return repository.findBySearchTitleContaining(searchTitle, pageable)
                         .map(this::convertToDto);
    }

    public List<String> onDeleteInfo(Long movieId) {
        List<String> onDeleteInfo = new ArrayList<>();
        screeningService.findAllScreeningIdsByMovieId(movieId).forEach(
                screeningId -> onDeleteInfo.addAll(screeningService.onDeleteInfo(screeningId)));
        return onDeleteInfo;
    }

}
