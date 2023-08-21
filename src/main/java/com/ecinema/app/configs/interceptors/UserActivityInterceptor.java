package com.ecinema.app.configs.interceptors;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.services.UserService;
import com.ecinema.app.util.UtilMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The type User activity interceptor.
 */
@RequiredArgsConstructor
public class UserActivityInterceptor implements HandlerInterceptor {

    private final UserService userService;
    private final SecurityContext securityContext;
    private final Logger logger = LoggerFactory.getLogger(UserActivityInterceptor.class);

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("User activity interceptor");
        boolean userIsLoggedIn = securityContext.userIsLoggedIn();
        logger.debug("User is logged in: " + userIsLoggedIn);
        if (securityContext.userIsLoggedIn()) {
            Long userId = securityContext.findIdOfLoggedInUser();
            logger.debug("User id: " + userId);
            userService.updateLastActivityDateTimeOfUserWithId(userId);
        }
    }

}
