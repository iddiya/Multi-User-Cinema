package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.ScreeningSeat;
import com.ecinema.app.domain.entities.Showroom;
import com.ecinema.app.domain.entities.ShowroomSeat;
import com.ecinema.app.domain.enums.Letter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@DataJpaTest
class ShowroomSeatRepositoryTest {

    @Autowired
    ShowroomRepository showroomRepository;

    @Autowired
    ShowroomSeatRepository showroomSeatRepository;

    @Autowired
    ScreeningSeatRepository screeningSeatRepository;

    @Test
    void findAllByShowroom() {
        // given
        Showroom showroom = new Showroom();
        showroom.setShowroomLetter(Letter.A);
        showroomRepository.save(showroom);
        ShowroomSeat showroomSeat = new ShowroomSeat();
        showroomSeat.setRowLetter(Letter.A);
        showroomSeat.setSeatNumber(1);
        showroomSeat.setShowroom(showroom);
        showroom.getShowroomSeats().add(showroomSeat);
        showroomSeatRepository.save(showroomSeat);
        // when
        List<ShowroomSeat> showroomSeats1 = showroomSeatRepository
                .findAllByShowroom(showroom);
        List<ShowroomSeat> showroomSeats2 = showroomSeatRepository
                .findAllByShowroomWithId(showroom.getId());
        // then
        assertEquals(1, showroomSeats1.size());
        assertEquals(1, showroomSeats2.size());
        assertEquals(showroomSeat, showroomSeats1.get(0));
        assertEquals(showroomSeat, showroomSeats2.get(0));
    }

    @Test
    void findAllByShowroomAndRowLetter() {
        // given
        Showroom showroom = new Showroom();
        showroom.setShowroomLetter(Letter.A);
        showroomRepository.save(showroom);
        List<ShowroomSeat> showroomSeats = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ShowroomSeat showroomSeat = new ShowroomSeat();
            Letter rowLetter = i % 2 == 0 ? Letter.A : Letter.B;
            showroomSeat.setRowLetter(rowLetter);
            showroomSeat.setSeatNumber(i);
            showroomSeat.setShowroom(showroom);
            showroom.getShowroomSeats().add(showroomSeat);
            showroomSeatRepository.save(showroomSeat);
            showroomSeats.add(showroomSeat);
        }
        List<ShowroomSeat> control1 = showroomSeats.stream()
                .filter(showroomSeat -> showroomSeat.getRowLetter().equals(Letter.A))
                .collect(Collectors.toList());
        List<ShowroomSeat> control2 = showroomSeats.stream()
                .filter(showroomSeat -> showroomSeat.getRowLetter().equals(Letter.B))
                .collect(Collectors.toList());
        // when
        List<ShowroomSeat> test1 = showroomSeatRepository.findAllByShowroomAndRowLetter(
                showroom, Letter.A);
        List<ShowroomSeat> test2 = showroomSeatRepository.findAllByShowroomWithIdAndRowLetter(
                showroom.getId(), Letter.B);
        // then
        assertEquals(5, test1.size());
        assertEquals(5, test2.size());
        assertEquals(control1, test1);
        assertEquals(control2, test2);
    }

    @Test
    void findByScreeningSeatsContains() {
        // given
        Showroom showroom = new Showroom();
        showroom.setShowroomLetter(Letter.A);
        showroomRepository.save(showroom);
        ShowroomSeat showroomSeat = new ShowroomSeat();
        showroomSeat.setRowLetter(Letter.A);
        showroomSeat.setSeatNumber(1);
        showroomSeat.setShowroom(showroom);
        showroom.getShowroomSeats().add(showroomSeat);
        showroomSeatRepository.save(showroomSeat);
        ScreeningSeat screeningSeat = new ScreeningSeat();
        screeningSeat.setShowroomSeat(showroomSeat);
        showroomSeat.getScreeningSeats().add(screeningSeat);
        screeningSeatRepository.save(screeningSeat);
        // when
        Optional<ShowroomSeat> showroomSeatOptional1 = showroomSeatRepository
                .findByScreeningSeatsContains(screeningSeat);
        Optional<ShowroomSeat> showroomSeatOptional2 = showroomSeatRepository
                .findByScreeningSeatsContainsWithId(screeningSeat.getId());
        // then
        assertTrue(showroomSeatOptional1.isPresent());
        assertTrue(showroomSeatOptional2.isPresent());
        assertEquals(showroomSeat, showroomSeatOptional1.get());
        assertEquals(showroomSeat, showroomSeatOptional2.get());
    }

    @Test
    void findByShowroomAndRowLetterAndSeatNumber() {
        // given
        Showroom showroom = new Showroom();
        showroom.setShowroomLetter(Letter.A);
        showroomRepository.save(showroom);
        ShowroomSeat showroomSeat = new ShowroomSeat();
        showroomSeat.setShowroom(showroom);
        showroomSeat.setRowLetter(Letter.A);
        showroomSeat.setSeatNumber(1);
        showroomSeat.setShowroom(showroom);
        showroom.getShowroomSeats().add(showroomSeat);
        // when
        Optional<ShowroomSeat> showroomSeatOptional1 = showroomSeatRepository
                .findByShowroomAndRowLetterAndSeatNumber(showroom, Letter.A, 1);
        Optional<ShowroomSeat> showroomSeatOptional2 = showroomSeatRepository
                .findByShowroomWithIdAndRowLetterAndSeatNumber(showroom.getId(), Letter.A, 1);
        // then
        assertTrue(showroomSeatOptional1.isPresent());
        assertTrue(showroomSeatOptional2.isPresent());
        assertEquals(showroomSeat, showroomSeatOptional1.get());
        assertEquals(showroomSeat, showroomSeatOptional2.get());
    }

}