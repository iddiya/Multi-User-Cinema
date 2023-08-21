package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.*;
import com.ecinema.app.domain.enums.Letter;
import com.ecinema.app.util.UtilMethods;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ScreeningRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private ShowroomRepository showroomRepository;

    @Test
    void findAllByShowDateTimeLessThanEqual() {
        // given
        List<Screening> screenings = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Screening screening = new Screening();
            screening.setShowDateTime(UtilMethods.randomDateTime());
            screeningRepository.save(screening);
            screenings.add(screening);
        }
        LocalDateTime controlVar = UtilMethods.randomDateTime();
        List<Screening> control = screenings
                .stream().filter(screening -> screening.getShowDateTime().isEqual(controlVar) ||
                                screening.getShowDateTime().isBefore(controlVar))
                .sorted(Comparator.comparing(Screening::getShowDateTime))
                .collect(Collectors.toList());
        // when
        List<Screening> test = screeningRepository.findAllByShowDateTimeLessThanEqual(controlVar);
        // then
        assertEquals(control, test);
    }

    @Test
    void findAllByShowDateTimeGreaterThanEqual() {
        // given
        List<Screening> screenings = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Screening screening = new Screening();
            screening.setShowDateTime(UtilMethods.randomDateTime());
            screeningRepository.save(screening);
            screenings.add(screening);
        }
        LocalDateTime controlVar = UtilMethods.randomDateTime();
        List<Screening> control = screenings.stream()
                .filter(screening -> screening.getShowDateTime().isEqual(controlVar) ||
                        screening.getShowDateTime().isAfter(controlVar))
                .sorted(Comparator.comparing(Screening::getShowDateTime))
                .collect(Collectors.toList());
        // when
        List<Screening> test = screeningRepository.findAllByShowDateTimeGreaterThanEqual(controlVar);
        // then
        assertEquals(control, test);
    }

    @Test
    void findAllByShowDateTimeBetween() {
        // given
        List<Screening> screenings = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Screening screening = new Screening();
            screening.setShowDateTime(UtilMethods.randomDateTime());
            screeningRepository.save(screening);
            screenings.add(screening);
        }
        LocalDateTime controlVar1 = UtilMethods.randomDateTime();
        LocalDateTime controlVar2 = UtilMethods.randomDateTime();
        List<LocalDateTime> controlVars = new ArrayList<>() {{
            add(controlVar1);
            add(controlVar2);
        }};
        controlVars.sort(LocalDateTime::compareTo);
        List<Screening> control = screenings.stream()
                .filter(screening -> (screening.getShowDateTime().isAfter(controlVars.get(0)) ||
                        screening.getShowDateTime().equals(controlVars.get(0))) &&
                        (screening.getShowDateTime().isBefore(controlVars.get(1)) ||
                        screening.getShowDateTime().equals(controlVars.get(1))))
                .sorted(Comparator.comparing(Screening::getShowDateTime))
                .collect(Collectors.toList());
        // when
        List<Screening> test = screeningRepository.findAllByShowDateTimeBetween(controlVars.get(0), controlVars.get(1));
        // then
        assertEquals(control, test);
    }

    @Test
    void findAllByMovie() {
        // given
        Movie movie1 = new Movie();
        Movie movie2 = new Movie();
        movieRepository.save(movie1);
        movieRepository.save(movie2);
        List<Screening> screenings = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Screening screening = new Screening();
            Movie movie = i % 2 == 0 ? movie1 : movie2;
            screening.setMovie(movie);
            movie.getScreenings().add(screening);
            screeningRepository.save(screening);
            screenings.add(screening);
        }
        List<Screening> control = screenings.stream()
                .filter(screening -> screening.getMovie().equals(movie1))
                .collect(Collectors.toList());
        // when
        List<Screening> test1 = screeningRepository.findAllByMovie(movie1);
        List<Screening> test2 = screeningRepository.findAllByMovieWithId(movie1.getId());
        // then
        assertEquals(control, test1);
        assertEquals(control, test2);
    }

    @Test
    void findAllByShowroom() {
        // given
        Showroom showroom = new Showroom();
        showroom.setShowroomLetter(Letter.A);
        showroomRepository.save(showroom);
        Screening screening = new Screening();
        screening.setShowroom(showroom);
        showroom.getScreenings().add(screening);
        screeningRepository.save(screening);
        // when
        List<Screening> test1 = screeningRepository
                .findAllByShowroom(showroom);
        List<Screening> test2 = screeningRepository
                .findAllByShowroomWithId(showroom.getId());
        // then
        assertEquals(1, test1.size());
        assertEquals(screening, test1.get(0));
        assertEquals(showroom, test1.get(0).getShowroom());
        assertEquals(1, test2.size());
        assertEquals(screening, test2.get(0));
        assertEquals(showroom, test2.get(0).getShowroom());
    }

    @Test
    void findAllByShowroomLetter() {
        // given
        Showroom showroom1 = new Showroom();
        showroom1.setShowroomLetter(Letter.A);
        showroomRepository.save(showroom1);
        Showroom showroom2 = new Showroom();
        showroom2.setShowroomLetter(Letter.B);
        showroomRepository.save(showroom2);
        Screening screening1 = new Screening();
        screening1.setShowroom(showroom1);
        showroom1.getScreenings().add(screening1);
        screeningRepository.save(screening1);
        Screening screening2 = new Screening();
        screening2.setShowroom(showroom2);
        showroom2.getScreenings().add(screening2);
        screeningRepository.save(screening2);
        // when
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<Screening> screenings = screeningRepository.findAllByShowroomLetters(
                List.of(Letter.A, Letter.B), pageRequest).getContent();
        // when
        assertEquals(2, screenings.size());
        assertTrue(screenings.containsAll(Arrays.asList(screening1, screening2)));
    }

    @Test
    void findAllByMovieWithTitleLike() {
        // given
        Movie movie1 = new Movie();
        movie1.setSearchTitle("teST1");
        movieRepository.save(movie1);
        Movie movie2 = new Movie();
        movie2.setSearchTitle("  tEsT2 ");
        movieRepository.save(movie2);
        Movie movie3 = new Movie();
        movie3.setSearchTitle("fAiL");
        movieRepository.save(movie3);
        Screening screening1 = new Screening();
        screening1.setMovie(movie1);
        movie1.getScreenings().add(screening1);
        screeningRepository.save(screening1);
        Screening screening2 = new Screening();
        screening2.setMovie(movie2);
        movie2.getScreenings().add(screening2);
        screeningRepository.save(screening2);
        Screening screening3 = new Screening();
        screening3.setMovie(movie3);
        movie3.getScreenings().add(screening3);
        screeningRepository.save(screening3);
        // when
        PageRequest pageRequest = PageRequest.of(0, 3);
        List<Screening> screenings = screeningRepository.findAllByMovieWithTitleLike(
                "TeSt", pageRequest).getContent();
        // then
        assertEquals(2, screenings.size());
        assertTrue(screenings.containsAll(Arrays.asList(screening1, screening2)));
    }

    @Test
    void findAllByShowroomLetterAndMovieWithTitleLike() {
        // given
        Showroom showroom1 = new Showroom();
        showroom1.setShowroomLetter(Letter.A);
        showroomRepository.save(showroom1);
        Showroom showroom2 = new Showroom();
        showroom2.setShowroomLetter(Letter.B);
        showroomRepository.save(showroom2);
        Showroom showroom3 = new Showroom();
        showroom3.setShowroomLetter(Letter.C);
        showroomRepository.save(showroom3);
        Movie movie1 = new Movie();
        movie1.setSearchTitle("teST1");
        movieRepository.save(movie1);
        Movie movie2 = new Movie();
        movie2.setSearchTitle("  tEsT2 ");
        movieRepository.save(movie2);
        Movie movie3 = new Movie();
        movie3.setSearchTitle("fAiL");
        movieRepository.save(movie3);
        Screening screening1 = new Screening();
        screening1.setShowroom(showroom1);
        showroom1.getScreenings().add(screening1);
        screening1.setMovie(movie1);
        movie1.getScreenings().add(screening1);
        screeningRepository.save(screening1);
        Screening screening2 = new Screening();
        screening2.setShowroom(showroom2);
        showroom2.getScreenings().add(screening2);
        screening2.setMovie(movie2);
        movie2.getScreenings().add(screening2);
        screeningRepository.save(screening2);
        Screening screening3 = new Screening();
        screening3.setShowroom(showroom3);
        showroom3.getScreenings().add(screening3);
        screening3.setMovie(movie3);
        movie3.getScreenings().add(screening3);
        screeningRepository.save(screening3);
        // when
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<Screening> screenings = screeningRepository.findAllByShowroomLettersAndMovieWithTitleLike(
                List.of(Letter.A, Letter.B), "TeSt", pageRequest).getContent();
        // then
        assertEquals(2, screenings.size());
        assertTrue(screenings.containsAll(Arrays.asList(screening1, screening2)));
    }

    @Test
    void findAllScreeningIdsByMovieId() {
        // given
        Movie movie = new Movie();
        movieRepository.save(movie);
        List<Long> screeningIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Screening screening = new Screening();
            screening.setMovie(movie);
            movie.getScreenings().add(screening);
            screeningRepository.save(screening);
            screeningIds.add(screening.getId());
        }
        // when
        List<Long> test = screeningRepository.findAllScreeningIdsByMovieId(movie.getId());
        // then
        assertEquals(screeningIds, test);
    }

}