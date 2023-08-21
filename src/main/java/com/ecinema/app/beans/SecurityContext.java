package com.ecinema.app.beans;

import com.ecinema.app.domain.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * The type Security context.
 */
@Component
public class SecurityContext {

    /**
     * Find logged in user user.
     *
     * @return the user
     */
    protected User findLoggedInUser() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object o = authentication.getPrincipal();
        return o instanceof User user ? user : null;
    }

    /**
     * Find id of logged-in user long.
     *
     * @return the long
     */
    public Long findIdOfLoggedInUser() {
        User user = findLoggedInUser();
        return user != null ? user.getId() : null;
    }

    /**
     * User is logged in boolean.
     *
     * @return the boolean
     */
    public boolean userIsLoggedIn() {
        return findLoggedInUser() != null;
    }

}
