package com.ecinema.app.controllers;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.domain.dtos.ChangePasswordDto;
import com.ecinema.app.domain.dtos.UserDto;
import com.ecinema.app.domain.forms.ChangePasswordForm;
import com.ecinema.app.exceptions.EmailException;
import com.ecinema.app.exceptions.ExpirationException;
import com.ecinema.app.exceptions.InvalidArgumentException;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.services.ChangePasswordService;
import com.ecinema.app.services.UserService;
import com.ecinema.app.util.UtilMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * The type Password controller.
 */
@Controller
@RequiredArgsConstructor
public class PasswordController {

    /**
     * The constant MESSAGE.
     */
    public static final String MESSAGE = "An email with further instructions has been sent\n"
            + "Your password will not be changed until you click on the token link in the email";

    private final UserService userService;
    private final SecurityContext securityContext;
    private final ChangePasswordService changePasswordService;
    private final Logger logger = LoggerFactory.getLogger(PasswordController.class);

    /**
     * Show get email change password string.
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/get-email-for-change-password")
    public String showGetEmailChangePassword(final Model model) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: get email for change password");
        Long userId = securityContext.findIdOfLoggedInUser();
        if (userId == null) {
            model.addAttribute("action", "/get-email-for-change-password");
            return "get-email";
        }
        UserDto userDto = userService.findById(userId);
        logger.debug("User DTO: " + userDto);
        return "redirect:/change-password?email=" + userDto.getEmail();
    }

    /**
     * Gets email for change password.
     *
     * @param redirectAttributes the redirect attributes
     * @param email              the email
     * @return the email for change password
     */
    @PostMapping("/get-email-for-change-password")
    public String getEmailForChangePassword(final RedirectAttributes redirectAttributes,
                                            @RequestParam("email") final String email) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Post mapping: get email for change password");
        logger.debug("Email: " + email);
        if (userService.existsByEmail(email)) {
            return "redirect:/change-password?email=" + email;
        } else {
            redirectAttributes.addAttribute("errors", List.of(
                    "No user is associated with the email: " + email));
            return "redirect:/get-email-for-change-password";
        }
    }

    /**
     * Show change password page string.
     *
     * @param model the model
     * @param email the email
     * @return the string
     */
    @GetMapping("/change-password")
    public String showChangePasswordPage(final Model model, @RequestParam("email") final String email) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: change password");
        logger.debug("Email: " + email);
        ChangePasswordForm changePasswordForm = changePasswordService.getChangePasswordForm(email);
        logger.debug("Change Password Form: " + changePasswordForm);
        model.addAttribute("changePasswordForm", changePasswordForm);
        model.addAttribute("securityQuestion1", changePasswordForm.getQuestion1());
        model.addAttribute("securityQuestion2", changePasswordForm.getQuestion2());
        model.addAttribute("email", email);
        return "change-password";
    }

    /**
     * Show change password page string.
     *
     * @param redirectAttributes the redirect attributes
     * @param changePasswordForm the change password form
     * @return the string
     */
    @PostMapping("/change-password")
    public String showChangePasswordPage(final RedirectAttributes redirectAttributes,
                                         @ModelAttribute("changePasswordForm")
                                         final ChangePasswordForm changePasswordForm) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Post mapping: change password");
        logger.debug("Change Password Form: " + changePasswordForm);
        try {
            changePasswordService.submitChangePasswordForm(changePasswordForm);
            redirectAttributes.addFlashAttribute("message", MESSAGE);
            return "redirect:/message-page";
        } catch (NoEntityFoundException | InvalidArgumentException | EmailException e) {
            logger.debug("Errors: " + e);
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            return "redirect:/change-password?email=" + changePasswordForm.getEmail();
        }
    }

    /**
     * Confirm change password string.
     *
     * @param model the model
     * @param token the token
     * @return the string
     */
    @GetMapping("/change-password-confirm/{token}")
    public String confirmChangePassword(final Model model, @PathVariable("token") final String token) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: change password confirmation");
        logger.debug("Token: " + token);
        try {
            changePasswordService.confirmChangePassword(token);
            model.addAttribute("message", "Success, your password has now been changed!");
            return "message-page";
        } catch (NoEntityFoundException | ExpirationException e) {
            logger.debug("Errors: " + e);
            model.addAttribute("errors", e.getErrors());
            return "error";
        }
    }

}
