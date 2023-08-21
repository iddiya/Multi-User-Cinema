package com.ecinema.app.controllers;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.domain.dtos.UserDto;
import com.ecinema.app.domain.forms.UserProfileForm;
import com.ecinema.app.exceptions.InvalidArgumentException;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.services.UserService;
import com.ecinema.app.util.UtilMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/**
 * The type User profile controller.
 */
@Controller
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final SecurityContext securityContext;
    private final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    /**
     * Show user profile page string.
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/user-profile")
    public String showUserProfilePage(final Model model) {
        Long userId = securityContext.findIdOfLoggedInUser();
        UserDto userDto = userService.findById(userId);
        model.addAttribute("user", userDto);
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: user profile page");
        logger.debug("User DTO: " + userDto);
        return "user-profile";
    }

    /**
     * Show edit user profile page string.
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/edit-user-profile")
    public String showEditUserProfilePage(final Model model) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: edit user profile");
        Long userId = securityContext.findIdOfLoggedInUser();
        logger.debug("User id: " + userId);
        UserDto userDto = userService.findById(userId);
        logger.debug("User DTO: " + userDto);
        UserProfileForm userProfileForm = new UserProfileForm();
        userProfileForm.setToIProfile(userDto);
        logger.debug("User profile form: " + userProfileForm);
        model.addAttribute("profileForm", userProfileForm);
        model.addAttribute("minDate", LocalDate.now().minusYears(120));
        model.addAttribute("maxDate", LocalDate.now().minusYears(16));
        return "edit-user-profile";
    }

    /**
     * Edit user profile string.
     *
     * @param redirectAttributes the redirect attributes
     * @param userProfileForm    the user profile form
     * @return the string
     */
    @PostMapping("/edit-user-profile")
    public String editUserProfile(final RedirectAttributes redirectAttributes,
                                  @ModelAttribute("profileForm") final UserProfileForm userProfileForm) {
        try {
            logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
            logger.debug("Post mapping: edit user profile");
            Long userId = securityContext.findIdOfLoggedInUser();
            logger.debug("User id: " + userId);
            userProfileForm.setUserId(userId);
            logger.debug("User profile form: " + userProfileForm);
            userService.editUserProfile(userProfileForm);
            redirectAttributes.addFlashAttribute("success", "Successfully edited profile");
            logger.debug("Successfully edited profile");
            return "redirect:/user-profile";
        } catch (InvalidArgumentException e) {
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            logger.debug("Errors: " + e.getErrors());
            logger.debug("Redirect to edit user profile page");
            return "redirect:/edit-user-profile";
        } catch (NoEntityFoundException e) {
            e.getErrors().add("FATAL ERROR: Forced to logout");
            redirectAttributes.addFlashAttribute("errors", e.getErrors());
            logger.debug("Errors: " + e.getErrors());
            logger.debug("Redirect to logout page");
            return "redirect:/logout";
        }
    }

}
