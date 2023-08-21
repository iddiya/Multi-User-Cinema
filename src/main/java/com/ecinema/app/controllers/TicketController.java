package com.ecinema.app.controllers;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.domain.dtos.PaymentCardDto;
import com.ecinema.app.domain.dtos.ScreeningDto;
import com.ecinema.app.domain.dtos.TicketDto;
import com.ecinema.app.domain.forms.LongListForm;
import com.ecinema.app.domain.forms.SeatBookingForm;
import com.ecinema.app.exceptions.*;
import com.ecinema.app.services.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.*;

import com.ecinema.app.domain.dtos.ScreeningSeatDto;
import com.ecinema.app.domain.enums.Letter;
import com.ecinema.app.util.UtilMethods;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * The type Ticket controller.
 */
@Controller
@RequiredArgsConstructor
@SessionAttributes("bookSeatsForm")
public class TicketController {

    private final TicketService ticketService;
    private final CustomerService customerService;
    private final SecurityContext securityContext;
    private final ScreeningService screeningService;
    private final PaymentCardService paymentCardService;
    private final ScreeningSeatService screeningSeatService;
    private final Logger logger = LoggerFactory.getLogger(TicketController.class);

    /**
     * See view seats page string.
     *
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @param screeningId        the screening id
     * @return the string
     */
    @GetMapping("/view-seats")
    public String seeViewSeatsPage(final Model model, final RedirectAttributes redirectAttributes,
                                   @RequestParam("id") final Long screeningId) {
        try {
            logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
            logger.debug("Get mapping: Choose seats to book");
            ScreeningDto screeningDto = screeningService.findById(screeningId);
            logger.debug("Screening DTO: " + screeningDto);
            Map<Letter, Set<ScreeningSeatDto>> mapOfScreeningSeats = screeningSeatService
                    .findScreeningSeatMapByScreeningWithId(screeningId);
            logger.debug("Map of screening seats has " + mapOfScreeningSeats.keySet().size() + " rows");
            model.addAttribute("screening", screeningDto);
            model.addAttribute("mapOfScreeningSeats", mapOfScreeningSeats);
            model.addAttribute("seatIdsForm", new LongListForm());
            for (Map.Entry<Letter, Set<ScreeningSeatDto>> entry : mapOfScreeningSeats.entrySet()) {
                logger.debug("Row " + entry.getKey() + " has " + entry.getValue() + " seats");
            }
            return "view-seats";
        } catch (NoEntityFoundException | InvalidAssociationException e) {
            logger.debug("Errors: " + e.getErrors());
            logger.debug("Redirecting to movie screening page");
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            return "redirect:/movie-screening/" + screeningId;
        }
    }

    /**
     * See book seats page string.
     *
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @param screeningId        the screening id
     * @param seatId             the seat id
     * @return the string
     */
    @GetMapping("/book-seat")
    public String seeBookSeatsPage(final Model model, final RedirectAttributes redirectAttributes,
                                   @RequestParam("screeningId") final Long screeningId,
                                   @RequestParam("seatId") final Long seatId) {
        try {
            logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
            logger.debug("Get mapping: book seat");
            if (!screeningService.existsById(screeningId)) {
                throw new NoEntityFoundException("screening", "id", screeningId);
            }
            if (screeningSeatService.screeningSeatIsBooked(seatId)) {
                throw new InvalidActionException("Seat with id " + seatId + " is already booked");
            }
            // screening dto
            ScreeningDto screening = screeningService.findById(screeningId);
            logger.debug("Screening DTO: " + screening);
            model.addAttribute("screening", screening);
            // screening seat dto
            ScreeningSeatDto screeningSeat = screeningSeatService.findById(seatId);
            logger.debug("Screening seat DTO: " + screeningSeat);
            model.addAttribute("screeningSeat", screeningSeat);
            // seat booking form
            SeatBookingForm seatBookingForm = screeningSeatService.fetchSeatBookingForm(seatId);
            logger.debug("Seat booking form: " + seatBookingForm);
            model.addAttribute("seatBookingForm", seatBookingForm);
            // user id
            Long userId = securityContext.findIdOfLoggedInUser();
            logger.debug("User id: " + userId);
            // payment cards
            List<PaymentCardDto> paymentCards = paymentCardService.findAllByCardUserWithId(userId);
            logger.debug("Payment cards: " + paymentCards);
            model.addAttribute("paymentCards", paymentCards);
            // tokens
            Integer tokens = customerService.numberOfTokensOwnedByUser(userId);
            logger.debug("Tokens: " + tokens);
            model.addAttribute("tokens", tokens);
            return "book-seat";
        } catch (NoEntityFoundException | InvalidActionException e) {
            logger.debug("Errors: " + e);
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            logger.debug("Redirecting to view seats page");
            return "redirect:/view-seats?id=" + screeningId;
        }
    }

    /**
     * Book seat string.
     *
     * @param redirectAttributes the redirect attributes
     * @param seatBookingForm    the seat booking form
     * @return the string
     */
    @PostMapping("/book-seat")
    public String bookSeat(final RedirectAttributes redirectAttributes,
                           @ModelAttribute("seatBookingForm") final SeatBookingForm seatBookingForm) {
        try {
            logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
            logger.debug("Post mapping: book seat");
            Long userId = securityContext.findIdOfLoggedInUser();
            logger.debug("User id: " + userId);
            seatBookingForm.setUserId(userId);
            logger.debug("Seat booking form: " + seatBookingForm);
            ticketService.bookTicket(seatBookingForm);
            logger.debug("Successfully booked seat");
            redirectAttributes.addFlashAttribute("success", "Successfully booked ticket for seat");
            Long screeningId = screeningSeatService.getScreeningIdOfScreeningSeatWithId(
                    seatBookingForm.getScreeningSeatId());
            return "redirect:/view-seats?id=" + screeningId;
        } catch (NoEntityFoundException | InvalidActionException | InvalidArgumentException e) {
            logger.debug("Errors: " + e);
            logger.debug("Redirecting to book seat page");
            logger.debug("Screening id: " + seatBookingForm.getScreeningSeatId());
            logger.debug("Seat id: " + seatBookingForm.getScreeningSeatId());
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            redirectAttributes.addFlashAttribute("seatBookingForm", seatBookingForm);
            Long screeningId = screeningSeatService.getScreeningIdOfScreeningSeatWithId(
                    seatBookingForm.getScreeningSeatId());
            return "redirect:/book-seat?screeningId=" + screeningId + "&seatId=" + seatBookingForm.getScreeningSeatId();
        }
    }

    /**
     * Show current ticket page string.
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/current-tickets")
    public String showCurrentTicketsPage(final Model model) {
        Long userId = securityContext.findIdOfLoggedInUser();
        List<TicketDto> tickets = ticketService.findAllByUserWithIdAndShowDateTimeIsAfter(
                userId, LocalDateTime.now());
        model.addAttribute("tickets", tickets);
        return "current-tickets";
    }

    /**
     * Show past tickets page string.
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/past-tickets")
    public String showPastTicketsPage(final Model model) {
        Long userId = securityContext.findIdOfLoggedInUser();
        List<TicketDto> tickets = ticketService.findAllByUserWithIdAndShowDateTimeIsBefore(
                userId, LocalDateTime.now());
        model.addAttribute("tickets", tickets);
        return "past-tickets";
    }

    /**
     * Show refund ticket page string.
     *
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @param ticketId           the ticket id
     * @return the string
     */
    @GetMapping("/refund-ticket")
    public String showRefundTicketPage(final Model model, final RedirectAttributes redirectAttributes,
                                       @RequestParam("id") final Long ticketId) {
        try {
            TicketDto ticketDto = ticketService.findById(ticketId);
            model.addAttribute("ticket", ticketDto);
            return "refund-ticket";
        } catch (NoEntityFoundException e) {
            logger.debug("Errors: " + e);
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            return "redirect:/current-tickets";
        }
    }

    /**
     * Refund ticket string.
     *
     * @param redirectAttributes the redirect attributes
     * @param ticketId           the ticket id
     * @return the string
     */
    @PostMapping("/refund-ticket/{id}")
    public String refundTicket(final RedirectAttributes redirectAttributes,
                               @PathVariable("id") final Long ticketId) {
        try {
            ticketService.refundTicket(ticketId);
            redirectAttributes.addFlashAttribute("success", "Successfully refunded ticket");
            return "redirect:/current-tickets";
        } catch (NoEntityFoundException | InvalidActionException | NoFieldFoundException e) {
            logger.debug("Errors: " + e);
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            return "redirect:/refund-ticket?id=" + ticketId;
        }
    }

}
