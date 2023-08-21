package com.ecinema.app.services;

import com.ecinema.app.domain.dtos.ScreeningDto;
import com.ecinema.app.domain.entities.*;
import com.ecinema.app.domain.forms.ScreeningForm;
import com.ecinema.app.validators.ScreeningValidator;
import com.ecinema.app.exceptions.ClashException;
import com.ecinema.app.exceptions.InvalidArgumentException;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.repositories.MovieRepository;
import com.ecinema.app.repositories.ScreeningRepository;
import com.ecinema.app.repositories.ShowroomRepository;
import com.ecinema.app.repositories.TicketRepository;
import com.ecinema.app.util.UtilMethods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScreeningService extends AbstractEntityService<Screening, ScreeningRepository, ScreeningDto> {

    private final MovieRepository movieRepository;
    private final TicketRepository ticketRepository;
    private final ShowroomRepository showroomRepository;
    private final ScreeningSeatService screeningSeatService;
    private final ScreeningValidator screeningValidator;

    public ScreeningService(ScreeningRepository repository,
                            MovieRepository movieRepository,
                            TicketRepository ticketRepository,
                            ShowroomRepository showroomRepository,
                            ScreeningSeatService screeningSeatService,
                            ScreeningValidator screeningValidator) {
        super(repository);
        this.movieRepository = movieRepository;
        this.ticketRepository = ticketRepository;
        this.showroomRepository = showroomRepository;
        this.screeningSeatService = screeningSeatService;
        this.screeningValidator = screeningValidator;
    }

    @Override
    protected void onDelete(Screening screening) {
        logger.debug("Screening on delete");
        // detach Movie
        Movie movie = screening.getMovie();
        logger.debug("Detach movie: " + movie);
        if (movie != null) {
            movie.getScreenings().remove(screening);
            screening.setMovie(null);
        }
        // detach Showroom
        Showroom showroom = screening.getShowroom();
        logger.debug("Detach showroom: " + showroom);
        if (showroom != null) {
            showroom.getScreenings().remove(screening);
            screening.setShowroom(null);
        }
        // cascade delete ScreeningSeats
        logger.debug("Delete all associated screening seats");
        screeningSeatService.deleteAll(screening.getScreeningSeats());
    }

    @Override
    public ScreeningDto convertToDto(Screening screening) {
        ScreeningDto screeningDTO = new ScreeningDto();
        screeningDTO.setId(screening.getId());
        Movie movie = screening.getMovie();
        if (movie != null) {
            screeningDTO.setMovieId(screening.getMovie().getId());
            screeningDTO.setMovieTitle(screening.getMovie().getTitle());
        } else {
            screeningDTO.setMovieTitle("");
        }
        Showroom showroom = screening.getShowroom();
        if (showroom != null) {
            screeningDTO.setShowroomId(showroom.getId());
            screeningDTO.setShowroomLetter(showroom.getShowroomLetter());
            screeningDTO.setTotalSeatsInRoom(showroom.getShowroomSeats().size());
        } else {
            screeningDTO.setTotalSeatsInRoom(0);
        }
        screeningDTO.setShowDateTime(screening.getShowDateTime());
        screeningDTO.setEndDateTime(screening.getEndDateTime());
        Set<ScreeningSeat> screeningSeats =  screening.getScreeningSeats();
        if (!screeningSeats.isEmpty()) {
            long numberOfSeatsBooked = screeningSeats.stream().filter(
                    screeningSeat -> screeningSeat.getTicket() != null).count();
            screeningDTO.setSeatsBooked((int) numberOfSeatsBooked);
            screeningDTO.setSeatsAvailable(screeningDTO.getTotalSeatsInRoom() - (int) numberOfSeatsBooked);
        } else {
            screeningDTO.setSeatsBooked(0);
            screeningDTO.setSeatsAvailable(0);
        }
        logger.debug("Converted screening to DTO: " + screeningDTO);
        logger.debug("Screening: " + screening);
        return screeningDTO;
    }

    public List<String> onDeleteInfo(Long screeningId)
            throws NoEntityFoundException {
        Screening screening = repository.findById(screeningId).orElseThrow(
                () -> new NoEntityFoundException("screening", "id", screeningId));
        return onDeleteInfo(screening);
    }

    protected List<String> onDeleteInfo(Screening screening) {
        List<String> onDeleteInfo = new ArrayList<>();
        List<Ticket> ticketsInShowroom = ticketRepository.findAllByShowroomWithId(
                screening.getShowroom().getId());
        int costOfDeletingTickets = ticketsInShowroom.stream().mapToInt(
                ticket -> ticket.getTicketType().getPrice()).sum();
        onDeleteInfo.add("Number of seats to be deleted: " + screening.getScreeningSeats().size());
        onDeleteInfo.add("Number of tickets to be refunded and then deleted: " + ticketsInShowroom.size());
        onDeleteInfo.add("Cost of refunding all tickets for deleted screenings: $" + costOfDeletingTickets);
        return onDeleteInfo;
    }

    public Page<ScreeningDto> findAllByMovieWithTitleLike(String title, Pageable pageable) {
        return repository.findAllByMovieWithTitleLike(
                MovieService.convertTitleToSearchTitle(title), pageable)
                         .map(this::convertToDto);
    }

    public void submitScreeningForm(ScreeningForm screeningForm)
            throws NoEntityFoundException, InvalidArgumentException, ClashException {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Add new screening");
        List<String> errors = new ArrayList<>();
        screeningValidator.validate(screeningForm, errors);
        if (!errors.isEmpty()) {
            throw new InvalidArgumentException(errors);
        }
        Showroom showroom = showroomRepository
                .findById(screeningForm.getShowroomId())
                .orElseThrow(() -> new NoEntityFoundException(
                        "showroom", "showroom id", screeningForm.getShowroomId()));
        Movie movie = movieRepository
                .findById(screeningForm.getMovieId())
                .orElseThrow(() -> new NoEntityFoundException(
                        "movie", "movie id", screeningForm.getMovieId()));
        LocalDateTime endDateTime = screeningForm.getShowDateTime()
                                                 .plusHours(movie.getDuration().getHours())
                                                 .plusMinutes(movie.getDuration().getMinutes());
        Optional<ScreeningDto> optionalOverlap = findScreeningByShowroomAndInBetweenStartTimeAndEndTime(
                showroom, screeningForm.getShowDateTime(), endDateTime);
        if (optionalOverlap.isPresent()) {
            ScreeningDto overlap = optionalOverlap.get();
            throw new ClashException("Screening for " + movie.getTitle() +
                                             " in showroom " + showroom.getShowroomLetter() +
                                             " at " + UtilMethods.localDateTimeFormatted(
                    screeningForm.getShowDateTime()) +
                                             " cannot be created because it overlaps" +
                                             " screening for " + overlap.getMovieTitle() +
                                             " in showroom " + overlap.getShowroomLetter() +
                                             " at " + UtilMethods.localDateTimeFormatted(
                    overlap.getShowDateTime()));
        }
        Screening screening = new Screening();
        screening.setShowDateTime(screeningForm.getShowDateTime());
        screening.setEndDateTime(endDateTime);
        screening.setShowroom(showroom);
        showroom.getScreenings().add(screening);
        screening.setMovie(movie);
        movie.getScreenings().add(screening);
        repository.save(screening);
        for (ShowroomSeat showroomSeat : showroom.getShowroomSeats()) {
            ScreeningSeat screeningSeat = new ScreeningSeat();
            screeningSeat.setShowroomSeat(showroomSeat);
            showroomSeat.getScreeningSeats().add(screeningSeat);
            screeningSeat.setScreening(screening);
            screening.getScreeningSeats().add(screeningSeat);
            screeningSeat.setTicket(null);
            screeningSeatService.save(screeningSeat);
        }
        logger.debug("Saved and instantiated new screening: " + screening);
    }

    public Optional<ScreeningDto> findScreeningByShowroomAndInBetweenStartTimeAndEndTime(
            Showroom showroom, LocalDateTime startTime, LocalDateTime endTime) {
        return findScreeningByShowroomIdAndInBetweenStartTimeAndEndTime(
                showroom.getId(), startTime, endTime);
    }

    public Optional<ScreeningDto> findScreeningByShowroomIdAndInBetweenStartTimeAndEndTime(
            Long showroomId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Screening> screenings = repository.findAllByShowroomWithId(showroomId);
        for (Screening screening : screenings) {
            if (UtilMethods.localDateTimeOverlap(
                    startTime, endTime,
                    screening.getShowDateTime(), screening.getEndDateTime())) {
                return Optional.of(convertToDto(screening));
            }
        }
        return Optional.empty();
    }

    List<Long> findAllScreeningIdsByMovieId(Long movieId) {
        return repository.findAllScreeningIdsByMovieId(movieId);
    }

    public Page<ScreeningDto> findPageByMovieId(Long movieId, Pageable pageable) {
        return repository.findAllByMovieId(movieId, pageable).map(this::convertToDto);
    }

    public List<ScreeningDto> findAllByShowDateTimeLessThanEqual(LocalDateTime localDateTime) {
        return repository.findAllByShowDateTimeLessThanEqual(localDateTime)
                         .stream().map(this::convertToDto)
                         .collect(Collectors.toList());
    }

    public List<ScreeningDto> findAllByShowDateTimeGreaterThanEqual(LocalDateTime localDateTime) {
        return repository.findAllByShowDateTimeGreaterThanEqual(localDateTime)
                         .stream().map(this::convertToDto)
                         .collect(Collectors.toList());
    }

    public List<ScreeningDto> findAllByShowDateTimeBetween(LocalDateTime l1, LocalDateTime l2) {
        return repository.findAllByShowDateTimeBetween(l1, l2)
                         .stream().map(this::convertToDto)
                         .collect(Collectors.toList());
    }

    public List<ScreeningDto> findAllByMovie(Movie movie) {
        return repository.findAllByMovie(movie)
                         .stream().map(this::convertToDto)
                         .collect(Collectors.toList());
    }

    public List<ScreeningDto> findAllByMovieWithId(Long movieId) {
        return repository.findAllByMovieWithId(movieId)
                         .stream().map(this::convertToDto)
                         .collect(Collectors.toList());
    }

    public List<ScreeningDto> findAllByShowroom(Showroom showroom) {
        return repository.findAllByShowroom(showroom)
                         .stream().map(this::convertToDto)
                         .collect(Collectors.toList());
    }

    public List<ScreeningDto> findAllByShowroomWithId(Long showroomId) {
        return repository.findAllByShowroomWithId(showroomId)
                         .stream().map(this::convertToDto)
                         .collect(Collectors.toList());
    }

}

