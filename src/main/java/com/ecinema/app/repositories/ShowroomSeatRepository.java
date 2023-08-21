package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.ScreeningSeat;
import com.ecinema.app.domain.entities.Showroom;
import com.ecinema.app.domain.entities.ShowroomSeat;
import com.ecinema.app.domain.enums.Letter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The interface Showroom seat repository.
 */
@Repository
public interface ShowroomSeatRepository extends JpaRepository<ShowroomSeat, Long> {

    /**
     * Find all by showroom list.
     *
     * @param showroom the showroom
     * @return the list
     */
    List<ShowroomSeat> findAllByShowroom(Showroom showroom);

    /**
     * Find all by showroom with id list.
     *
     * @param showroomId the showroom id
     * @return the list
     */
    @Query("SELECT s FROM ShowroomSeat s WHERE s.showroom.id = ?1")
    List<ShowroomSeat> findAllByShowroomWithId(Long showroomId);

    /**
     * Find all by showroom and row letter list.
     *
     * @param showroom  the showroom
     * @param rowLetter the row letter
     * @return the list
     */
    List<ShowroomSeat> findAllByShowroomAndRowLetter(Showroom showroom, Letter rowLetter);

    /**
     * Find all by showroom with id and row letter list.
     *
     * @param showroomId the showroom id
     * @param rowLetter  the row letter
     * @return the list
     */
    @Query("SELECT s FROM ShowroomSeat s WHERE s.showroom.id = ?1 AND s.rowLetter = ?2")
    List<ShowroomSeat> findAllByShowroomWithIdAndRowLetter(Long showroomId, Letter rowLetter);

    /**
     * Find by screening seats contains optional.
     *
     * @param screeningSeat the screening seat
     * @return the optional
     */
    @Query("SELECT s FROM ShowroomSeat s JOIN s.screeningSeats ss WHERE ss = ?1")
    Optional<ShowroomSeat> findByScreeningSeatsContains(ScreeningSeat screeningSeat);

    /**
     * Find by screening seats contains with id optional.
     *
     * @param screeningSeatId the screening seat id
     * @return the optional
     */
    @Query("SELECT s FROM ShowroomSeat s JOIN s.screeningSeats ss WHERE ss.id = ?1")
    Optional<ShowroomSeat> findByScreeningSeatsContainsWithId(Long screeningSeatId);

    /**
     * Find by showroom and row letter and seat number optional.
     *
     * @param showroom   the showroom
     * @param rowLetter  the row letter
     * @param seatNumber the seat number
     * @return the optional
     */
    Optional<ShowroomSeat> findByShowroomAndRowLetterAndSeatNumber(Showroom showroom, Letter rowLetter,
                                                                   Integer seatNumber);

    /**
     * Find by showroom with id and row letter and seat number optional.
     *
     * @param showroomId the showroom id
     * @param rowLetter  the row letter
     * @param seatNumber the seat number
     * @return the optional
     */
    @Query("SELECT s FROM ShowroomSeat s WHERE s.showroom.id = ?1 AND s.rowLetter = ?2 AND s.seatNumber = ?3")
    Optional<ShowroomSeat> findByShowroomWithIdAndRowLetterAndSeatNumber(Long showroomId, Letter rowLetter,
                                                                         Integer seatNumber);

}
