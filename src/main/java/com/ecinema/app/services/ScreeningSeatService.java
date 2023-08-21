package com.ecinema.app.services;

import com.ecinema.app.domain.contracts.ISeat;
import com.ecinema.app.domain.dtos.ScreeningSeatDto;
import com.ecinema.app.domain.entities.Screening;
import com.ecinema.app.domain.entities.ScreeningSeat;
import com.ecinema.app.domain.entities.ShowroomSeat;
import com.ecinema.app.domain.entities.Ticket;
import com.ecinema.app.domain.enums.TicketType;
import com.ecinema.app.domain.forms.SeatBookingForm;
import com.ecinema.app.exceptions.InvalidAssociationException;
import com.ecinema.app.domain.enums.Letter;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.repositories.ScreeningSeatRepository;
import com.ecinema.app.util.UtilMethods;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ScreeningSeatService extends AbstractEntityService<
        ScreeningSeat, ScreeningSeatRepository, ScreeningSeatDto> {

    private final TicketService ticketService;

    public ScreeningSeatService(ScreeningSeatRepository repository, TicketService ticketService) {
        super(repository);
        this.ticketService = ticketService;
    }

    @Override
    protected void onDelete(ScreeningSeat screeningSeat) {
        logger.debug("Screening seat on delete");
        // cascade delete Ticket
        Ticket ticket = screeningSeat.getTicket();
        if (ticket != null) {
            logger.debug("Detach ticket: " + ticket);
            screeningSeat.setTicket(null);
            ticket.setScreeningSeat(null);
            ticketService.delete(ticket);
        }
        // detach Screening
        Screening screening = screeningSeat.getScreening();
        if (screening != null) {
            logger.debug("Detach screening " + screening);
            screening.getScreeningSeats().remove(screeningSeat);
            screeningSeat.setScreening(null);
        }
        // detach ShowroomSeat
        ShowroomSeat showroomSeat = screeningSeat.getShowroomSeat();
        if (showroomSeat != null) {
            logger.debug("Detach showroom seat: " + showroomSeat);
            showroomSeat.getScreeningSeats().remove(screeningSeat);
            screeningSeat.setShowroomSeat(null);
        }
    }

    @Override
    public ScreeningSeatDto convertToDto(ScreeningSeat screeningSeat) {
        ScreeningSeatDto screeningSeatDTO = new ScreeningSeatDto();
        screeningSeatDTO.setId(screeningSeat.getId());
        screeningSeatDTO.setRowLetter(screeningSeat.getShowroomSeat().getRowLetter());
        screeningSeatDTO.setSeatNumber(screeningSeat.getShowroomSeat().getSeatNumber());
        screeningSeatDTO.setIsBooked(screeningSeat.getTicket() != null);
        screeningSeatDTO.setScreeningId(screeningSeat.getScreening().getId());
        logger.debug("Convert screening seat to DTO: " + screeningSeatDTO);
        logger.debug("Screening seat: " + screeningSeat);
        return screeningSeatDTO;
    }

    public Map<Letter, Set<ScreeningSeatDto>> findScreeningSeatMapByScreeningWithId(Long screeningId)
            throws InvalidAssociationException {
        List<ScreeningSeatDto> screeningSeatDtos = findAllByScreeningWithId(screeningId);
        if (screeningSeatDtos.isEmpty()) {
            throw new InvalidAssociationException("No screening seats mapped to screening with id " + screeningId);
        }
        Map<Letter, Set<ScreeningSeatDto>> mapOfScreeningSeats = new TreeMap<>();
        for (ScreeningSeatDto screeningSeatDto : screeningSeatDtos) {
            mapOfScreeningSeats.putIfAbsent(
                    screeningSeatDto.getRowLetter(), new TreeSet<>(ISeat.SeatComparator.getInstance()));
            mapOfScreeningSeats.get(screeningSeatDto.getRowLetter()).add(screeningSeatDto);
        }
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Find screening seat map by screening with id: " + screeningId);
        logger.debug("Screening seat map: " + screeningSeatDtos);
        return mapOfScreeningSeats;
    }

    public Long getScreeningIdOfScreeningSeatWithId(Long screeningSeatId) {
        ScreeningSeat screeningSeat = repository.findById(screeningSeatId).orElseThrow(
                () -> new NoEntityFoundException("No screening seat found with id " + screeningSeatId));
        return screeningSeat.getScreening() != null ? screeningSeat.getScreening().getId() : null;
    }

    public SeatBookingForm fetchSeatBookingForm(Long screeningSeatId)
            throws NoEntityFoundException {
        ScreeningSeat screeningSeat = repository.findById(screeningSeatId).orElseThrow(
                () -> new NoEntityFoundException("screening seat", "id", screeningSeatId));
        Long screeningId = repository.findScreeningIdOfScreeningSeatWithId(screeningSeatId)
                .orElseThrow(() -> new NoEntityFoundException(
                        "screening id", "screening seat id", screeningSeat));
        SeatBookingForm seatBookingForm = new SeatBookingForm();
        seatBookingForm.setScreeningSeatId(screeningSeatId);
        seatBookingForm.setTicketType(TicketType.ADULT);
        seatBookingForm.setTokensToApply(0);
        return seatBookingForm;
    }

    public boolean screeningSeatIsBooked(Long screeningSeatId)
            throws NoEntityFoundException {
        ScreeningSeat screeningSeat = repository.findById(screeningSeatId).orElseThrow(
                () -> new NoEntityFoundException("screening seat", "id", screeningSeatId));
        return screeningSeat.getTicket() != null;
    }

    public List<ScreeningSeatDto> findAllByScreeningWithId(Long screeningId) {
        return sortAndConvert(repository.findAllByScreeningWithId(screeningId));
    }

    private List<ScreeningSeatDto> sortAndConvert(List<ScreeningSeat> screeningSeats) {
        screeningSeats.sort(ISeat.SeatComparator.getInstance());
        return convertToDto(screeningSeats);
    }


}
