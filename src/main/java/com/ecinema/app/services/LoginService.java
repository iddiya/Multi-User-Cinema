package com.ecinema.app.services;

import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.exceptions.PasswordMismatchException;
import com.ecinema.app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final EncoderService encoderService;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final Logger logger = LoggerFactory.getLogger(LoginService.class);

    public void login(final String s, final String password)
            throws NoEntityFoundException, PasswordMismatchException {
        logger.debug("Security AbstractEntityService login method");
        UserDetails user = userRepository.findByUsernameOrEmail(s).orElseThrow(
                () -> new NoEntityFoundException("user", "username or email", s));
        if (!encoderService.matches(password, user.getPassword())) {
            throw new PasswordMismatchException(s);
        }
        for (GrantedAuthority authority : user.getAuthorities()) {
            logger.debug("User has authority: " + authority.getAuthority());
        }
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        user, password, user.getAuthorities());
        daoAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            logger.debug(String.format("Auto login %s success!", user.getUsername()));
            securityContext.setAuthentication(usernamePasswordAuthenticationToken);
        }
    }

}
