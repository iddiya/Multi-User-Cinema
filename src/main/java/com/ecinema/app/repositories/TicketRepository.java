package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.*;
import com.ecinema.app.domain.enums.Letter;
import com.ecinema.app.domain.enums.TicketStatus;
import com.ecinema.app.domain.enums.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * The jpa repository for {@link Ticket}.
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Find all {@link Ticket} where {@link Screening#getId()} from {@link ScreeningSeat#getScreening()} from
     * {@link Ticket#getScreeningSeat()} equals the provided Long screening id argument.
     *
     * @param screeningId the screening id
     * @return the list of tickets
     */
    @Query("SELECT t FROM Ticket t WHERE t.screeningSeat.screening.id = ?1")
    List<Ticket> findAllByScreeningWithId(Long screeningId);

    /**
     * Find all {@link Ticket} where {@link ScreeningSeat#getId()} from {@link Ticket#getScreeningSeat()} equals
     * the provided Long screening seat id argument.
     *
     * @param screeningSeatId the screening seat id
     * @return the optional ticket
     */
    @Query("SELECT t FROM Ticket t WHERE t.screeningSeat.id = ?1")
    Optional<Ticket> findByScreeningSeatWithId(Long screeningSeatId);

    /**
     * Find all {@link Ticket} where {@link Showroom#getId()} from {@link Screening#getShowroom()} from
     * {@link ScreeningSeat#getScreening()} from {@link Ticket#getScreeningSeat()} equals the provided Long
     * showroom id argument.
     *
     * @param showroomId the showroom id
     * @return the list of tickets
     */
    @Query("SELECT t FROM Ticket t WHERE t.screeningSeat.screening.showroom.id = ?1")
    List<Ticket> findAllByShowroomWithId(Long showroomId);

    /**
     * Find all {@link Ticket} where {@link Ticket#getCreationDateTime()} less than or equal to the provided
     * {@link LocalDateTime}.
     *
     * @param localDateTime the local date time
     * @return the list of tickets
     */
    List<Ticket> findAllByCreationDateTimeLessThanEqual(LocalDateTime localDateTime);

    /**
     * Find all {@link Ticket} where {@link Ticket#getCreationDateTime()} greater than or equals to the provided
     * {@link LocalDateTime}.
     *
     * @param localDateTime the local date time
     * @return the list of tickets
     */
    List<Ticket> findAllByCreationDateTimeGreaterThanEqual(LocalDateTime localDateTime);

    /**
     * Find all {@link Ticket} where {@link Ticket#getTicketStatus()} equals the provided {@link TicketStatus}
     * argument.
     *
     * @param ticketStatus the ticket status
     * @return the list of tickets
     */
    List<Ticket> findAllByTicketStatus(TicketStatus ticketStatus);

    /**
     * Find all {@link Ticket} where {@link Ticket#getTicketType()} equals the provided {@link TicketType} argument.
     *
     * @param ticketType the ticket type
     * @return the list of tickets
     */
    List<Ticket> findAllByTicketType(TicketType ticketType);

    /**
     * Find all {@link Ticket} where {@link User#getId()} from {@link Customer#getUser()} from
     * {@link Ticket#getTicketOwner()} equals the provided Long user id argument.
     *
     * @param userId the user id
     * @return the list of tickets
     */
    @Query("SELECT t FROM Ticket t WHERE t.ticketOwner.user.id = ?1")
    List<Ticket> findAllByUserWithId(Long userId);

    /**
     * Find all {@link Ticket#getId()} where {@link User#getId()} from {@link Customer#getUser()} from
     * {@link Ticket#getTicketOwner()} equals the provided Long user id argument and {@link Screening#getShowDateTime()}
     * from {@link ScreeningSeat#getScreening()} from {@link Ticket#getScreeningSeat()} is before the provided
     * {@link LocalDateTime} argument.
     *
     * @param userId        the user id
     * @param localDateTime the local date time
     * @return the list of tickets
     */
    @Query("SELECT t.id FROM Ticket t WHERE t.ticketOwner.user.id = ?1 AND t.screeningSeat.screening.showDateTime < ?2")
    List<Long> findAllIdsByUserWithIdAndShowDateTimeIsBefore(Long userId, LocalDateTime localDateTime);

    /**
     * Find all {@link Ticket#getId()} where {@link User#getId()} from {@link Customer#getUser()} from
     * {@link Ticket#getTicketOwner()} equals the provided Long user id argument and {@link Screening#getShowDateTime()}
     * from {@link ScreeningSeat#getScreening()} from {@link Ticket#getScreeningSeat()} is after the provided
     * {@link LocalDateTime} argument.
     *
     * @param userId        the user id
     * @param localDateTime the local date time
     * @return the list
     */
    @Query("SELECT t.id FROM Ticket t WHERE t.ticketOwner.user.id = ?1 AND t.screeningSeat.screening.showDateTime > ?2")
    List<Long> findAllIdsByUserWithIdAndShowDateTimeIsAfter(Long userId, LocalDateTime localDateTime);

    /**
     * Find optional {@link User#getId()} from {@link Customer#getUser()} from {@link Ticket#getTicketOwner()}
     * where {@link Ticket#getId()} equals the provided Long ticket id argument.
     *
     * @param ticketId the ticket id
     * @return the optional {@link User} id
     */
    @Query("SELECT t.ticketOwner.user.id FROM Ticket t WHERE t.id = ?1")
    Optional<Long> findUserIdOfTicket(Long ticketId);

    /**
     * Find optional {@link User#getEmail()} from {@link Customer#getUser()} from {@link Ticket#getTicketOwner()} where
     * {@link Ticket#getId()} equals the provided Long ticket id argument.
     *
     * @param ticketId the ticket id
     * @return the optional email
     */
    @Query("SELECT t.ticketOwner.user.email FROM Ticket t WHERE t.id = ?1")
    Optional<String> findEmailOfTicketUserOwner(Long ticketId);

    /**
     * Find optional {@link User#getUsername()} from {@link Customer#getUser()} from {@link Ticket#getTicketOwner()}
     * where {@link Ticket#getId()} equals the provided Long ticket id argument.
     *
     * @param ticketId the ticket id
     * @return the optional username
     */
    @Query("SELECT t.ticketOwner.user.username FROM Ticket t WHERE t.id = ?1")
    Optional<String> findUsernameOfTicketUserOwner(Long ticketId);

    /**
     * Find optional {@link Movie#getTitle()} from {@link Screening#getMovie()} from {@link ScreeningSeat#getScreening()}
     * where {@link Ticket#getScreeningSeat()} where {@link Ticket#getId()} equals the provided Long ticket id argument.
     *
     * @param ticketId the ticket id
     * @return the optional movie title
     */
    @Query("SELECT t.screeningSeat.screening.movie.title FROM Ticket t WHERE t.id = ?1")
    Optional<String> findMovieTitleAssociatedWithTicket(Long ticketId);

    /**
     * Find optional {@link Showroom#getShowroomLetter()} from {@link Screening#getShowroom()} from
     * {@link ScreeningSeat#getScreening()} from {@link Ticket#getScreeningSeat()} where {@link Ticket#getId()}
     * equals the provided Long ticket id argument.
     *
     * @param ticketId the ticket id
     * @return the optional showroom letter
     */
    @Query("SELECT t.screeningSeat.screening.showroom.showroomLetter FROM Ticket t WHERE t.id = ?1")
    Optional<Letter> findShowroomLetterAssociatedWithTicket(Long ticketId);

    /**
     * Find optional {@link Screening#getShowDateTime()} from {@link ScreeningSeat#getScreening()} from
     * {@link Ticket#getScreeningSeat()} where {@link Ticket#getId()} equals the provided Long ticket id argument.
     *
     * @param ticketId the ticket id
     * @return the optional show date time
     */
    @Query("SELECT t.screeningSeat.screening.showDateTime FROM Ticket t WHERE t.id = ?1")
    Optional<LocalDateTime> findShowDateTimeOfScreeningAssociatedWithTicket(Long ticketId);

    /**
     * Find optional {@link Screening#getEndDateTime()} from {@link ScreeningSeat#getScreening()} from
     * {@link Ticket#getScreeningSeat()} where {@link Ticket#getId()} equals the provided Long ticket id argument.
     *
     * @param ticketId the ticket id
     * @return the optional end date time
     */
    @Query("SELECT t.screeningSeat.screening.endDateTime FROM Ticket t WHERE t.id = ?1")
    Optional<LocalDateTime> findEndDateTimeOfScreeningAssociatedWithTicket(Long ticketId);

    /**
     * Find optional {@link ShowroomSeat} from {@link ScreeningSeat#getShowroomSeat()} from
     * {@link Ticket#getScreeningSeat()} where {@link Ticket#getId()} equals the provided Long ticket id argument.
     *
     * @param ticketId the ticket id
     * @return the optional showroom seat
     */
    @Query("SELECT t.screeningSeat.showroomSeat FROM Ticket t WHERE t.id = ?1")
    Optional<ShowroomSeat> findShowroomSeatAssociatedWithTicket(Long ticketId);

}
