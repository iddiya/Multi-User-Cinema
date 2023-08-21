package com.ecinema.app.controllers;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.domain.dtos.CustomerDto;
import com.ecinema.app.domain.dtos.UserDto;
import com.ecinema.app.domain.enums.UserAuthority;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.services.CustomerService;
import com.ecinema.app.services.ModeratorService;
import com.ecinema.app.services.UserService;
import com.ecinema.app.util.UtilMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.ecinema.app.util.UtilMethods.addPageNumbersAttribute;

/**
 * The type Moderator controller.
 */
@Controller
@RequiredArgsConstructor
public class ModeratorController {

    private final UserService userService;
    private final CustomerService customerService;
    private final ModeratorService moderatorService;
    private final SecurityContext securityContext;
    private final Logger logger = LoggerFactory.getLogger(ModeratorController.class);

    /**
     * Show moderator censorship page string.
     *
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @param page               the page
     * @return the string
     */
    @GetMapping("/moderator-censorship")
    public String showModeratorCensorshipPage(final Model model, final RedirectAttributes redirectAttributes,
                                              @RequestParam(value = "page", required = false, defaultValue = "1")
                                              final Integer page) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Get mapping: moderator censorship");
        Long userId = securityContext.findIdOfLoggedInUser();
        if (userId == null) {
            redirectAttributes.addFlashAttribute(
                    "errors", List.of("FATAL ERROR: Forced to logout"));
            return "redirect:/logout";
        }
        UserDto userDto = userService.convertToDto(userId);
        logger.debug("User DTO: " + userDto);
        if (!userDto.getUserAuthorities().contains(UserAuthority.MODERATOR)) {
            logger.debug("ERROR: user does not have moderator authority");
            redirectAttributes.addFlashAttribute(
                    "errors", List.of("User does not have moderator authority"));
            return "redirect:/error";
        }
        Long moderatorId = moderatorService.findIdByUserWithId(userDto.getId());
        logger.debug("Moderator id: " + moderatorId);
        model.addAttribute("moderatorId", moderatorId);
        PageRequest pageRequest = PageRequest.of(page - 1, 10);
        Page<CustomerDto> pageOfDtos = customerService.findAll(pageRequest);
        model.addAttribute("censoredCustomers", pageOfDtos);
        addPageNumbersAttribute(model, pageOfDtos);
        logger.debug("Page number: " + page);
        model.addAttribute("page", page);
        return "moderator-censorship";
    }

    /**
     * Sets customer censor status.
     *
     * @param model              the model
     * @param redirectAttributes the redirect attributes
     * @param page               the page
     * @param moderatorId        the moderator id
     * @param customerId         the customer id
     * @param currentStatus      the current status
     * @return the customer censor status
     */
    @PostMapping("/set-customer-censor-status")
    public String setCustomerCensorStatus(final Model model, final RedirectAttributes redirectAttributes,
                                          @RequestParam("page") final Integer page,
                                          @RequestParam("moderatorId") final Long moderatorId,
                                          @RequestParam("customerId") final Long customerId,
                                          @RequestParam("currentStatus") final Boolean currentStatus) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Post mapping: set customer censor status");
        logger.debug("Page number: " + page);
        logger.debug("Moderator id: " + moderatorId);
        logger.debug("Customer id: " + customerId);
        logger.debug("Current status: " + currentStatus);
        try {
            moderatorService.setCustomerCensoredStatus(
                    moderatorId, customerId, !currentStatus);
            logger.debug("Successfully changed customer censor status from " + currentStatus + " to " + !currentStatus);
            redirectAttributes.addFlashAttribute("success", "Successfully changed " +
                    "customer censored status from " + currentStatus + " to " + !currentStatus);
            return "redirect:/moderator-censorship?page=" + page;
        } catch (NoEntityFoundException e) {
            logger.debug("Errors: " + e);
            model.addAttribute("errors", List.of("FATAL ERROR: " +
                                                         "Could not find customer by id " + customerId));
            return "error";
        }
    }

    /**
     * Show moderator reports page string.
     *
     * @return the string
     */
    @GetMapping("/moderator-reports")
    public String showModeratorReportsPage() {
        return null;
    }

}
