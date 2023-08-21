package com.ecinema.app.configs.interceptors;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.domain.dtos.UserDto;
import com.ecinema.app.domain.enums.UserAuthority;
import com.ecinema.app.services.UserService;
import com.ecinema.app.domain.objects.Pair;
import com.ecinema.app.util.UtilMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.parameters.P;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Model attributes interceptor.
 */
@RequiredArgsConstructor
public class ModelAttributesInterceptor implements HandlerInterceptor {

    private final UserService userService;
    private final SecurityContext securityContext;
    private final Logger logger = LoggerFactory.getLogger(ModelAttributesInterceptor.class);

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Model attributes interceptor");
        if (modelAndView != null) {
            Long userId = securityContext.findIdOfLoggedInUser();
            addUserDto(userId, modelAndView);
            addDropdownMenu(userId, modelAndView);
        } else {
            logger.debug("Model and view is null");
        }
    }

    private void addUserDto(final Long userId, final ModelAndView modelAndView) {
        logger.debug("User id: " + userId);
        UserDto userDto = userId != null ? userService.findById(userId) : null;
        logger.debug("User DTO: " + userDto);
        modelAndView.addObject("user", userDto);
        modelAndView.addObject("userIsAdmin", userDto != null && userDto.isAdmin());
        modelAndView.addObject("userIsCustomer", userDto != null && userDto.isCustomer());
        modelAndView.addObject("userIsModerator", userDto != null && userDto.isModerator());
    }

    private void addDropdownMenu(final Long userId, final ModelAndView modelAndView) {
        logger.debug("Id of logged in user: " + userId);
        List<Pair<String, String>> dropdownMenu = new ArrayList<>();
        if (userId == null) {
            dropdownMenu.add(new Pair<>("Login", "/login"));
            dropdownMenu.add(new Pair<>("Register New Customer Account", "/submit-customer-registration"));
            dropdownMenu.add(new Pair<>("Forgot My Password", "/get-email-for-change-password"));
        } else {
            UserDto userDto = userService.findById(userId);
            if (userDto == null) {
                return;
            }
            logger.debug("User DTO: " + userDto);
            if (userDto.getUserAuthorities().contains(UserAuthority.CUSTOMER)) {
                dropdownMenu.add(new Pair<>("Tickets", "/current-tickets"));
                dropdownMenu.add(new Pair<>("Payment Cards", "/payment-cards"));
            }
            if (userDto.getUserAuthorities().contains(UserAuthority.MODERATOR) ||
                    userDto.getUserAuthorities().contains(UserAuthority.ADMIN)) {
                dropdownMenu.add(new Pair<>("Management", "/management"));
            }
            dropdownMenu.add(new Pair<>("Profile", "/user-profile"));
            dropdownMenu.add(new Pair<>("Change Password", "/get-email-for-change-password"));
            dropdownMenu.add(new Pair<>("Logout", "/logout"));
        }
        modelAndView.addObject("dropdownMenu", dropdownMenu);
        logger.debug("Dropdown menu: " + dropdownMenu);
    }

}
