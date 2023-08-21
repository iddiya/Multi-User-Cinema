package com.ecinema.app.controllers;

import com.ecinema.app.exceptions.TooManyPaymentCardsException;
import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.domain.dtos.PaymentCardDto;
import com.ecinema.app.domain.forms.PaymentCardForm;
import com.ecinema.app.exceptions.InvalidArgumentException;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.services.PaymentCardService;
import com.ecinema.app.services.UserService;
import com.ecinema.app.util.UtilMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/**
 * The type Payment card controller.
 */
@Controller
@RequiredArgsConstructor
public class PaymentCardController {

    private final UserService userService;
    private final SecurityContext securityContext;
    private final PaymentCardService paymentCardService;
    private final Logger logger = LoggerFactory.getLogger(PaymentCardController.class);

    /**
     * Payment card form payment card form.
     *
     * @return the payment card form
     */
    @ModelAttribute("paymentCardForm")
    public PaymentCardForm paymentCardForm() {
        return new PaymentCardForm();
    }

    /**
     * Show payment cards page string.
     *
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @return the string
     */
    @GetMapping("/payment-cards")
    public String showPaymentCardsPage(final Model model, final RedirectAttributes redirectAttributes) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: payment cards");
        Long userId = securityContext.findIdOfLoggedInUser();
        logger.debug("User id: " + userId);
        List<PaymentCardDto> paymentCards = paymentCardService.findAllByCardUserWithId(userId);
        logger.debug("Payment cards: " + paymentCards);
        model.addAttribute("paymentCards", paymentCards);
        return "payment-cards";
    }

    /**
     * Show add payment card page string.
     *
     * @param model           the model
     * @param paymentCardForm the payment card form
     * @return the string
     */
    @GetMapping("/add-payment-card")
    public String showAddPaymentCardPage(final Model model,
                                         @ModelAttribute("paymentCardForm") final PaymentCardForm paymentCardForm) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: add payment card");
        model.addAttribute("paymentCardForm", paymentCardForm);
        model.addAttribute("minDate", LocalDate.now());
        model.addAttribute("maxDate", LocalDate.now().plusYears(20));
        return "add-payment-card";
    }

    /**
     * Add payment card string.
     *
     * @param redirectAttributes the redirect attributes
     * @param paymentCardForm    the payment card form
     * @return the string
     */
    @PostMapping("/add-payment-card")
    public String addPaymentCard(final RedirectAttributes redirectAttributes,
                                 @ModelAttribute("paymentCardForm") final PaymentCardForm paymentCardForm) {
        try {
            logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
            logger.debug("Post mapping: edit payment card");
            Long userId = securityContext.findIdOfLoggedInUser();
            logger.debug("User id: " + userId);
            paymentCardForm.setUserId(userId);
            logger.debug("Payment card form: " + paymentCardForm);
            paymentCardService.submitPaymentCardFormToAddNewPaymentCard(paymentCardForm);
            logger.debug("Successfully submitted payment card form");
            redirectAttributes.addFlashAttribute(
                    "success", "Successfully added new payment card");
            return "redirect:/payment-cards";
        } catch (NoEntityFoundException | TooManyPaymentCardsException | InvalidArgumentException e) {
            logger.debug("Errors: " + e);
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            logger.debug("Payment card form: " + paymentCardForm);
            redirectAttributes.addFlashAttribute("paymentCardForm", paymentCardForm);
            return "redirect:/add-payment-card";
        }
    }

    /**
     * Show edit payment card page string.
     *
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @param paymentCardId      the payment card id
     * @return the string
     */
    @GetMapping("/edit-payment-card")
    public String showEditPaymentCardPage(final Model model, final RedirectAttributes redirectAttributes,
                                          @RequestParam("id") final Long paymentCardId) {
        try {
            logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
            logger.debug("Get mapping: edit payment card");
            logger.debug("Payment card id: " + paymentCardId);
            model.addAttribute("paymentCardId", paymentCardId);
            PaymentCardForm paymentCardForm = paymentCardService.fetchAsForm(paymentCardId);
            logger.debug("Payment card form: " + paymentCardForm);
            model.addAttribute("paymentCardForm", paymentCardForm);
            model.addAttribute("minDate", LocalDate.now());
            model.addAttribute("maxDate", LocalDate.now().plusYears(20));
            return "edit-payment-card";
        } catch (NoEntityFoundException e) {
            logger.debug("Errors: " + e);
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            return "redirect:/payment-cards";
        }
    }

    /**
     * Edit payment card string.
     *
     * @param redirectAttributes the redirect attributes
     * @param paymentCardId      the payment card id
     * @param paymentCardForm    the payment card form
     * @return the string
     */
    @PostMapping("/edit-payment-card/{id}")
    public String editPaymentCard(final RedirectAttributes redirectAttributes,
                                  @PathVariable("id") final Long paymentCardId,
                                  @ModelAttribute("paymentCardForm") final PaymentCardForm paymentCardForm) {
        try {
            logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
            logger.debug("Post mapping: edit payment card");
            Long userId = securityContext.findIdOfLoggedInUser();
            logger.debug("User id: " + userId);
            paymentCardForm.setUserId(userId);
            paymentCardForm.setPaymentCardId(paymentCardId);
            logger.debug("Payment card form: " + paymentCardForm);
            paymentCardService.submitPaymentCardFormToEditPaymentCard(paymentCardForm);
            logger.debug("Successfully submitted payment card form");
            redirectAttributes.addFlashAttribute(
                    "success", "Successfully edited payment card");
            return "redirect:/payment-cards";
        } catch (NoEntityFoundException | InvalidArgumentException e) {
            logger.debug("Errors: " + e);
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            logger.debug("Payment card form: " + paymentCardForm);
            redirectAttributes.addFlashAttribute("paymentCardForm", paymentCardForm);
            return "redirect:/edit-payment-card?id=" + paymentCardForm.getPaymentCardId();
        }
    }

    /**
     * Delete payment card string.
     *
     * @param redirectAttributes the redirect attributes
     * @param paymentCardId      the payment card id
     * @return the string
     */
    @PostMapping("/delete-payment-card/{id}")
    public String deletePaymentCard(final RedirectAttributes redirectAttributes,
                                    @PathVariable("id") final Long paymentCardId) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Post mapping: delete payment card");
        logger.debug("Payment card id: " + paymentCardId);
        try {
            if (paymentCardId == null) {
                throw new InvalidArgumentException("Payment card id cannot be null");
            }
            paymentCardService.delete(paymentCardId);
            redirectAttributes.addFlashAttribute("success",
                                                 "Successfully deleted payment card");
        } catch (NoEntityFoundException | InvalidArgumentException e) {
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
        }
        return "redirect:/payment-cards";
    }

}
