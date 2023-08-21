package com.ecinema.app.controllers;

import com.ecinema.app.domain.entities.Admin;
import com.ecinema.app.domain.entities.Customer;
import com.ecinema.app.domain.entities.User;
import com.ecinema.app.domain.enums.SecurityQuestions;
import com.ecinema.app.domain.enums.UserAuthority;
import com.ecinema.app.domain.forms.RegistrationForm;
import com.ecinema.app.exceptions.*;
import com.ecinema.app.services.RegistrationService;
import com.ecinema.app.util.UtilMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for client registration process.
 */
@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    /**
     * Gets the submit-registration page for the purpose of registering a {@link com.ecinema.app.domain.entities.User}
     * with {@link com.ecinema.app.domain.entities.Customer} authority. Adds to the model {@link RegistrationForm}
     * and {@link SecurityQuestions#getList()}.
     *
     * @param model            the model of the view
     * @param registrationForm the registration form
     * @return the view name
     */
    @GetMapping("/submit-customer-registration")
    public String showCustomerRegistrationPage(final Model model,
                                       @ModelAttribute("registrationForm") final RegistrationForm registrationForm) {
        UtilMethods.addRegistrationPageAttributes(model, registrationForm);
        model.addAttribute("action", "/submit-customer-registration");
        return "submit-registration";
    }

    /**
     * Posts the {@link RegistrationForm} for registering a new {@link User} with {@link Customer} authority.
     *
     * @param redirectAttributes the redirect attributes
     * @param registrationForm   the registration form
     * @return the view name
     */
    @PostMapping("/submit-customer-registration")
    public String registerCustomer(final RedirectAttributes redirectAttributes,
                           @ModelAttribute("registrationForm") final RegistrationForm registrationForm) {
        registrationForm.getAuthorities().add(UserAuthority.CUSTOMER);
        return submitRegistration(redirectAttributes, registrationForm,
                                  "redirect:/submit-customer-registration");
    }

    /**
     * Posts the {@link RegistrationForm} that the client has filled out. The form is processed by
     * {@link RegistrationService#submitRegistrationForm(RegistrationForm)}. On success, the client is
     * redirected to the message-page get-mapping and is notified with an on-success message detailing
     * the next steps the client needs to take to complete the registration process.
     */
    private String submitRegistration(final RedirectAttributes redirectAttributes,
                                      final RegistrationForm registrationForm, final String returnOnError) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Post mapping: submit registration");
        try {
            registrationService.submitRegistrationForm(registrationForm);
            redirectAttributes.addFlashAttribute(
                    "message", "Registration request has been processed. \n Please see " +
                            "the email sent with a link to confirm your registration.");
            logger.debug("Successfully submitted registration form");
            return "redirect:/message-page";
        } catch (ClashException | InvalidArgumentException | EmailException e) {
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            redirectAttributes.addFlashAttribute("registrationForm", registrationForm);
            logger.debug("Errors: " + e.getErrors());
            return returnOnError;
        }
    }

    /**
     * Gets the confirm-registration page and calls {@link RegistrationService#confirmRegistrationRequest(String)}
     * using the path variable "token". On success, the client is redirected to the message-page get-mapping
     * and is notified of being able to log into their newly-created account.
     *
     * @param model the model
     * @param token the token
     * @return the view name
     */
    @GetMapping("/confirm-registration/{token}")
    public String confirmRegistration(final Model model, @PathVariable("token") final String token) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Showing confirm registration page");
        logger.debug("Token: " + token);
        try {
            if (token == null || token.isBlank()) {
                throw new BadRuntimeVarException("Token cannot be empty");
            }
            registrationService.confirmRegistrationRequest(token);
            logger.debug("Successfully confirmed registration token");
            model.addAttribute("message",
                               "Successfully confirmed registration! You may now login.");
            model.addAttribute("redirectLink", "/login");
            model.addAttribute("redirectMessage", "Go to Login Page");
            return "message-page";
        } catch (InvalidArgumentException | ClashException e) {
            model.addAttribute("errors", e.getErrors());
            logger.debug("Errors: " + e.getErrors());
            return "error";
        }
    }

}
