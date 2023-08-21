package com.ecinema.app.services;

import com.ecinema.app.util.UtilMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EncoderService {

    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(EncoderService.class);

    /**
     * Instantiates a new Encoder service.
     *
     * @param passwordEncoder the password encoder
     */
    public EncoderService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String encode(String s) {
        logger.debug("Encoder AbstractEntityService: encode s");
        logger.debug("First 3 chars of s before encryption: " + s.subSequence(0, 3));
        String encoded = passwordEncoder.encode(UtilMethods.removeWhitespace(s));
        logger.debug("First 3 chars of encrypted s: " + encoded.subSequence(0, 3));
        return encoded;
    }

    public boolean matches(String raw, String encoded) {
        return passwordEncoder.matches(UtilMethods.removeWhitespace(raw), encoded);
    }

}
