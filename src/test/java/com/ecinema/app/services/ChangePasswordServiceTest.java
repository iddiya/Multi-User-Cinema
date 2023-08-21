package com.ecinema.app.services;

import com.ecinema.app.domain.entities.ChangePassword;
import com.ecinema.app.domain.entities.User;
import com.ecinema.app.domain.forms.ChangePasswordForm;
import com.ecinema.app.validators.PasswordValidator;
import com.ecinema.app.repositories.ChangePasswordRepository;
import com.ecinema.app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChangePasswordServiceTest {

    private UserService userService;
    private EncoderService encoderService;
    private ChangePasswordService changePasswordService;
    private PasswordValidator passwordValidator;
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChangePasswordRepository changePasswordRepository;

    @BeforeEach
    void setUp() {
        passwordValidator = new PasswordValidator();
        passwordEncoder = new BCryptPasswordEncoder();
        encoderService = new EncoderService(passwordEncoder);
        userService = new UserService(
                userRepository, null, null,
                null, encoderService,
                null, null);
        changePasswordService = new ChangePasswordService(
                changePasswordRepository, emailService,
                encoderService, userRepository, passwordValidator);
    }

    @Test
    void submitChangePasswordForm() {
        // when
        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");
        user.setUsername("Test_Username");
        user.setPassword(encoderService.encode("password123!"));
        user.setSecurityQuestion1("Question1");
        user.setSecurityAnswer1(encoderService.encode("Answer1"));
        user.setSecurityQuestion2("Question2");
        user.setSecurityAnswer2(encoderService.encode("Answer2"));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userRepository.findByEmail("test@gmail.com")).willReturn(Optional.of(user));
        userRepository.save(user);
        ChangePasswordForm changePasswordForm = new ChangePasswordForm();
        changePasswordForm.setEmail("test@gmail.com");
        changePasswordForm.setPassword("new_password123!?");
        changePasswordForm.setConfirmPassword("new_password123!?");
        changePasswordForm.setQuestion1("Question1");
        changePasswordForm.setAnswer1("Answer1");
        changePasswordForm.setQuestion2("Question2");
        changePasswordForm.setAnswer2("Answer2");
        ChangePassword changePassword = new ChangePassword();
        changePassword.setUserId(1L);
        changePassword.setToken("123");
        changePassword.setPassword(passwordEncoder.encode("new_password123!?"));
        changePassword.setExpirationDateTime(LocalDateTime.now());
        changePassword.setExpirationDateTime(LocalDateTime.now().plusMinutes(30));
        given(changePasswordRepository.findByToken("123"))
                .willReturn(Optional.of(changePassword));
        given(changePasswordRepository.existsByToken(anyString())).willReturn(false);
        assertTrue(passwordEncoder.matches("password123!", user.getPassword()));
        // when
        changePasswordService.submitChangePasswordForm(changePasswordForm);
        changePasswordService.confirmChangePassword("123");
        // then
        assertTrue(passwordEncoder.matches("new_password123!?", user.getPassword()));
    }

}