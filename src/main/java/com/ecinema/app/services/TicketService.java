package com.ecinema.app.services;

import com.ecinema.app.domain.dtos.TicketDto;
import com.ecinema.app.domain.entities.*;
import com.ecinema.app.domain.enums.Letter;
import com.ecinema.app.domain.enums.TicketStatus;
import com.ecinema.app.domain.forms.SeatBookingForm;
import com.ecinema.app.domain.objects.SeatDesignation;
import com.ecinema.app.exceptions.InvalidActionException;
import com.ecinema.app.exceptions.InvalidArgumentException;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.exceptions.NoFieldFoundException;
import com.ecinema.app.repositories.*;
import com.ecinema.app.validators.SeatBookingValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TicketService extends AbstractEntityService<Ticket, TicketRepository, TicketDto> {

    private final EmailService emailService;
    private final CustomerRepository customerRepository;
    private final SeatBookingValidator seatBookingValidator;
    private final PaymentCardRepository paymentCardRepository;
    private final ScreeningSeatRepository screeningSeatRepository;

    public TicketService(TicketRepository repository, EmailService emailService,
                         SeatBookingValidator seatBookingValidator, CustomerRepository customerRepository,
                         PaymentCardRepository paymentCardRepository, ScreeningSeatRepository screeningSeatRepository) {
        super(repository);
        this.emailService = emailService;
        this.customerRepository = customerRepository;
        this.seatBookingValidator = seatBookingValidator;
        this.paymentCardRepository = paymentCardRepository;
        this.screeningSeatRepository = screeningSeatRepository;
    }

    @Override
    protected void onDelete(Ticket ticket) {
        logger.debug("Ticket on delete");
        // detach ScreeningSeat
        ScreeningSeat screeningSeat = ticket.getScreeningSeat();
        logger.debug("Detaching " + screeningSeat + " from " + ticket);
        if (screeningSeat != null) {
            screeningSeat.setTicket(null);
            ticket.setScreeningSeat(null);
        }
        // detach Customer
        Customer customer = ticket.getTicketOwner();
        logger.debug("Detaching " + customer + " from " + ticket);
        if (customer != null) {
            customer.getTickets().remove(ticket);
            ticket.setTicketOwner(null);
        }
    }

    @Override
    public TicketDto convertToDto(Ticket ticket)
            throws NoFieldFoundException {
        logger.debug("Convert ticket to DTO");
        logger.debug("Ticket: " + ticket);
        TicketDto ticketDto = new TicketDto();
        ticketDto.setId(ticket.getId());
        Long userId = findUserIdOfTicket(ticket.getId())
                .orElseThrow(() -> new NoFieldFoundException("user id", "ticket"));
        ticketDto.setUserId(userId);
        String email = findEmailOfTicketUserOwner(ticket.getId())
                .orElseThrow(() -> new NoFieldFoundException("email", "ticket"));
        ticketDto.setEmail(email);
        String username = findUsernameOfTicketUserOwner(ticket.getId())
                .orElseThrow(() -> new NoFieldFoundException("username", "ticket"));
        ticketDto.setUsername(username);
        String movieTitle = findMovieTitleAssociatedWithTicket(ticket.getId())
                .orElseThrow(() -> new NoFieldFoundException("movie title", "ticket"));
        ticketDto.setMovieTitle(movieTitle);
        Letter showroomLetter = findShowroomLetterAssociatedWithTicket(ticket.getId())
                .orElseThrow(() -> new NoFieldFoundException("showroom letter", "ticket"));
        ticketDto.setShowroomLetter(showroomLetter);
        LocalDateTime showDateTime = findShowDateTimeOfScreeningAssociatedWithTicket(ticket.getId())
                .orElseThrow(() -> new NoFieldFoundException("showDateTime", "ticket"));
        ticketDto.setShowDateTime(showDateTime);
        ticketDto.setIsRefundable(showDateTimeQualifiesForTicketRefund(showDateTime));
        LocalDateTime endDateTime = findEndDateTimeOfScreeningAssociatedWithTicket(ticket.getId())
                .orElseThrow(() -> new NoFieldFoundException("endDateTime", "ticket"));
        ticketDto.setEndDateTime(endDateTime);
        SeatDesignation seatDesignation = findSeatDesignationOfTicket(ticket.getId())
                .orElseThrow(() -> new NoFieldFoundException("seat designation", "ticket"));
        PaymentCard paymentCard = ticket.getPaymentCard();
        ticketDto.setPaymentCardId(paymentCard != null ? paymentCard.getId() : null);
        ticketDto.setSeatDesignation(seatDesignation);
        ticketDto.setTicketType(ticket.getTicketType());
        ticketDto.setTicketStatus(ticket.getTicketStatus());
        ticketDto.setCreationDateTime(ticket.getCreationDateTime());
        logger.debug("Ticket DTO: " + ticketDto);
        return ticketDto;
    }

    public void bookTicket(SeatBookingForm seatBookingForm)
            throws NoEntityFoundException, InvalidActionException, InvalidArgumentException {
        List<String> errors = new ArrayList<>();
        seatBookingValidator.validate(seatBookingForm, errors);
        if (!errors.isEmpty()) {
            throw new InvalidArgumentException(errors);
        }
        PaymentCard paymentCard = paymentCardRepository
                .findById(seatBookingForm.getPaymentCardId())
                .orElseThrow(() -> new NoEntityFoundException(
                        "payment card", "id", seatBookingForm.getPaymentCardId()));
        if (paymentCard.getExpirationDate().isBefore(LocalDate.now())) {
            throw new InvalidActionException("Cannot purchase ticket with expired payment card");
        }
        ScreeningSeat screeningSeat = screeningSeatRepository
                .findById(seatBookingForm.getScreeningSeatId())
                .orElseThrow(() -> new NoEntityFoundException(
                        "screening seat", "id", seatBookingForm.getScreeningSeatId()));
        if (screeningSeat.getTicket() != null) {
            throw new InvalidActionException("Cannot purchase ticket for seat that's already booked");
        }
        Screening screening = screeningSeat.getScreening();
        if (screening.getShowDateTime().isBefore(LocalDateTime.now())) {
            throw new InvalidActionException("Cannot purchase ticket for past screening");
        }
        Customer customer = customerRepository
                .findByUserWithId(seatBookingForm.getUserId())
                .orElseThrow(() -> new NoEntityFoundException(
                        "customer", "user id", seatBookingForm.getUserId()));
        if (!customer.getIsAuthorityValid()) {
            throw new InvalidActionException("Cannot purchase ticket because your customer authority has been " +
                                                     "invalidated by an administrator");
        }
        Ticket ticket = new Ticket();
        ticket.setTicketOwner(customer);
        customer.getTickets().add(ticket);
        ticket.setPaymentCard(paymentCard);
        paymentCard.getPurchasedTickets().add(ticket);
        ticket.setScreeningSeat(screeningSeat);
        screeningSeat.setTicket(ticket);
        ticket.setTicketType(seatBookingForm.getTicketType());
        ticket.setTicketStatus(TicketStatus.VALID);
        ticket.setCreationDateTime(LocalDateTime.now());
        save(ticket);
        if (seatBookingForm.getTokensToApply() > 0) {
            customer.subtractTokens(seatBookingForm.getTokensToApply());
        }
        sendPurchaseConfirmationEmail(
                convertToDto(ticket), seatBookingForm.getTokensToApply());
    }

    public void refundTicket(Long ticketId)
            throws NoEntityFoundException, InvalidActionException, NoFieldFoundException {
        if (!existsById(ticketId)) {
            throw new NoEntityFoundException("ticket", "id", ticketId);
        }
        if (!ticketIsRefundable(ticketId)) {
            throw new InvalidActionException("Ticket with id " + ticketId + " is not refundable");
        }
        TicketDto ticketDto = findById(ticketId);
        if (ticketDto.getPaymentCardId() == null ||
                !paymentCardRepository.existsById(ticketDto.getPaymentCardId())) {
            Customer customer = customerRepository.findByUserWithId(ticketDto.getUserId()).orElseThrow(
                    () -> new NoEntityFoundException("customer", "user id", ticketDto.getUserId()));
            customer.addTokens(ticketDto.getTicketType().getPrice());
        }
        delete(ticketId);
        sendRefundConfirmationEmail(ticketDto);
    }

    public boolean ticketIsRefundable(Long ticketId)
            throws NoFieldFoundException {
        LocalDateTime showDateTime = findShowDateTimeOfScreeningAssociatedWithTicket(ticketId).orElseThrow(
                () -> new NoFieldFoundException("showtime", "ticket"));
        return showDateTimeQualifiesForTicketRefund(showDateTime);
    }

    public boolean showDateTimeQualifiesForTicketRefund(LocalDateTime showDateTime) {
        return showDateTime.isAfter(LocalDateTime.now()) &&
                Duration.between(LocalDateTime.now(), showDateTime).compareTo(Duration.ofDays(3)) > 0;
    }

    public Optional<Long> findUserIdOfTicket(Long ticketId) {
        return repository.findUserIdOfTicket(ticketId);
    }

    public Optional<String> findEmailOfTicketUserOwner(Long ticketId) {
        return repository.findEmailOfTicketUserOwner(ticketId);
    }

    public Optional<String> findUsernameOfTicketUserOwner(Long ticketId) {
        return repository.findUsernameOfTicketUserOwner(ticketId);
    }

    public Optional<String> findMovieTitleAssociatedWithTicket(Long ticketId) {
        return repository.findMovieTitleAssociatedWithTicket(ticketId);
    }

    public Optional<Letter> findShowroomLetterAssociatedWithTicket(Long ticketId) {
        return repository.findShowroomLetterAssociatedWithTicket(ticketId);
    }

    public Optional<LocalDateTime> findShowDateTimeOfScreeningAssociatedWithTicket(Long ticketId) {
        return repository.findShowDateTimeOfScreeningAssociatedWithTicket(ticketId);
    }

    public Optional<LocalDateTime> findEndDateTimeOfScreeningAssociatedWithTicket(Long ticketId) {
        return repository.findEndDateTimeOfScreeningAssociatedWithTicket(ticketId);
    }

    public Optional<SeatDesignation> findSeatDesignationOfTicket(Long ticketId)
            throws NoEntityFoundException {
        ShowroomSeat showroomSeat = repository.findShowroomSeatAssociatedWithTicket(ticketId).orElse(null);
        return showroomSeat != null ? Optional.of(new SeatDesignation(
                showroomSeat.getRowLetter(), showroomSeat.getSeatNumber())) : Optional.empty();
    }

    public List<TicketDto> findAllByUserWithId(Long userId) {
        return convertToDto(repository.findAllByUserWithId(userId));
    }

    public List<TicketDto> findAllByUserWithIdAndShowDateTimeIsBefore(Long userId, LocalDateTime localDateTime) {
       return repository.findAllIdsByUserWithIdAndShowDateTimeIsBefore(userId, localDateTime)
               .stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<TicketDto> findAllByUserWithIdAndShowDateTimeIsAfter(Long userId, LocalDateTime localDateTime) {
        return repository.findAllIdsByUserWithIdAndShowDateTimeIsAfter(userId, localDateTime)
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<TicketDto> findAllByTicketStatus(TicketStatus ticketStatus) {
        return convertToDto(
                repository.findAllByTicketStatus(ticketStatus));
    }

    private void sendPurchaseConfirmationEmail(TicketDto ticketDto, Integer tokensApplied) {
        String append = tokensApplied > 0 ? tokensApplied + "tokens were applied to this purchase" : "";
        sendEmail(ticketDto, "Ticket Purchase Confirmation", "purchased", append);
    }

    private void sendRefundConfirmationEmail(TicketDto ticketDto) {
        PaymentCard paymentCard = paymentCardRepository.findById(ticketDto.getPaymentCardId()).orElse(null);
        String append = paymentCard != null ? "Refunded funds to payment card associated with ticket " :
                "The payment card used to purchase this ticket no longer exists.\n" +
                        "The funds have instead been refunded via ECinema tokens";
        sendEmail(ticketDto, "Ticket Refund Confirmation", "refunded", append);
    }

    private void sendEmail(TicketDto ticketDto, String subject, String action, String append) {
        String message = "You have just " + action + " the following ticket:\n" +
                "\tMovie: " + ticketDto.getMovieTitle() + "\n" +
                "\tShowroom: " + ticketDto.getShowroomLetter() + "\n" +
                "\tShowtime: " + ticketDto.showDateTimeFormatted() + "\n" +
                "\tEndtime: " + ticketDto.endDateTimeFormatted() + "\n" +
                "\tSeat Designation: " + ticketDto.getSeatDesignation() + "\n" +
                "\tTicket Status: " + ticketDto.getTicketStatus() + "\n" +
                "\tTicket Type: " + ticketDto.ticketTypeFormatted() + "\n" +
                "\tPayment Card Id: " + ticketDto.getPaymentCardId() + "\n" +
                "\tPurchased at: " + ticketDto.creationDateTimeFormatted() + "\n\n" + append;
        emailService.sendFromBusinessEmail(
                ticketDto.getEmail(), message, "Ticket Refund Confirmation");
    }

}
