package com.ecinema.app.controllers;

import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.exceptions.PasswordMismatchException;
import com.ecinema.app.services.LoginService;
import com.ecinema.app.util.UtilMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The type Login controller.
 * https://www.thymeleaf.org/doc/articles/springsecurity.html
 */
@Controller
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final LoginService loginService;

    /**
     * Instantiates a new Login controller.
     *
     * @param loginService the login service
     */
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * Show login page string.
     *
     * @return the string
     */
    @GetMapping("/login")
    public String showLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Login get mapping");
        logger.debug("Authentication obj: " + authentication);
        return authentication == null || authentication instanceof AnonymousAuthenticationToken ?
                "login" : "redirect:/index";
    }

    /**
     * Perform login string.
     *
     * @param username the username
     * @param password the password
     * @return the string
     */
    @PostMapping("/perform-login")
    public String performLogin(@RequestParam("username") final String username,
                               @RequestParam("password") final String password) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Perform login post mapping");
        try {
            loginService.login(username, password);
            return "redirect:/index";
        } catch (NoEntityFoundException | PasswordMismatchException e) {
            logger.debug(e.toString());
            return "login";
        }
    }

    /**
     * Login error string.
     *
     * @param model the model
     * @return the string
     */
    @GetMapping("/login-error")
    public String loginError(final Model model) {
        model.addAttribute("error", "Failed to login, bad credentials");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Login error get mapping");
        return authentication == null || authentication instanceof AnonymousAuthenticationToken ?
                "login" : "redirect:/index";
    }

}
