package com.ecinema.app.controllers;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.configs.InitializationConfig;
import com.ecinema.app.domain.dtos.UserDto;
import com.ecinema.app.domain.entities.User;
import com.ecinema.app.domain.forms.ChangePasswordForm;
import com.ecinema.app.repositories.UserRepository;
import com.ecinema.app.services.ChangePasswordService;
import com.ecinema.app.services.EncoderService;
import com.ecinema.app.services.LoginService;
import com.ecinema.app.services.UserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithAnonymousUser
@RequiredArgsConstructor
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PasswordControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private EncoderService encoderService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private LoginService loginService;

    @MockBean
    private InitializationConfig config;

    @MockBean
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @Test
    void showGetEmailForChangePassword1()
            throws Exception {
        given(securityContext.findIdOfLoggedInUser()).willReturn(null);
        mockMvc.perform(get("/get-email-for-change-password"))
                .andExpect(status().isOk())
                .andExpect(result -> model().attribute(
                        "action", "/get-email-for-change-password"));
    }

    @Test
    void showGetEmailForChangePassword2()
            throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@gmail.com");
        given(securityContext.findIdOfLoggedInUser()).willReturn(1L);
        given(userService.findById(1L)).willReturn(userDto);
        mockMvc.perform(get("/get-email-for-change-password"))
                .andExpect(redirectedUrlPattern("/change-password?email=test@gmail.com**"));
    }

    @Test
    void failToPostGetEmailForChangePassword()
            throws Exception {
        given(securityContext.findIdOfLoggedInUser()).willReturn(null);
        given(userService.existsByEmail(anyString())).willReturn(false);
        mockMvc.perform(post("/get-email-for-change-password")
                                .param("email", "test@gmail.com"))
                .andExpect(result -> model().attributeExists("errors"))
                .andExpect(redirectedUrlPattern("/get-email-for-change-password**"));
    }

    @Test
    void showChangePasswordPage()
            throws Exception {
        User user = new User();
        user.setPassword("password123?!");
        user.setSecurityQuestion1("Question1");
        user.setSecurityQuestion2("Question2");
        user.setEmail("test@gmail.com");
        given(securityContext.findIdOfLoggedInUser()).willReturn(null);
        given(userRepository.findByEmail("test@gmail.com")).willReturn(Optional.of(user));
        mockMvc.perform(get("/change-password").param("email", "test@gmail.com"))
               .andExpect(status().isOk())
               .andExpect(result -> model().attributeExists("changePasswordForm"))
               .andExpect(result -> model().attribute("changePasswordForm", hasProperty(
                       "email", equalTo("test@gmail.com"))))
               .andExpect(result -> model().attribute("changePasswordForm", hasProperty(
                       "question1", equalTo("Question1"))))
               .andExpect(result -> model().attribute("changePasswordForm", hasProperty(
                       "question2", equalTo("Question2"))));
    }

    // TODO: Fix authentication error
    @Test
    void changePassword()
            throws Exception {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword(encoderService.encode("old_password123?!"));
        user.setSecurityQuestion1(encoderService.encode("Question 1"));
        user.setSecurityAnswer1(encoderService.encode("Answer 1"));
        user.setSecurityQuestion2(encoderService.encode("Question 2"));
        user.setSecurityAnswer2(encoderService.encode("Answer 2"));
        given(userRepository.findByEmail("test@gmail.com")).willReturn(Optional.of(user));
        ChangePasswordForm changePasswordForm = new ChangePasswordForm();
        changePasswordForm.setEmail("test@gmail.com");
        changePasswordForm.setQuestion1("Question 1");
        changePasswordForm.setAnswer1("Answer 1");
        changePasswordForm.setQuestion2("Question 2");
        changePasswordForm.setAnswer2("Answer2");
        changePasswordForm.setPassword("new_password123?!");
        changePasswordForm.setConfirmPassword("new_password123?!");
        given(securityContext.findIdOfLoggedInUser()).willReturn(null);
        mockMvc.perform(post("/change-password").flashAttr("changePasswordForm", changePasswordForm))
               .andExpect(redirectedUrlPattern("/message-page**"))
               .andExpect(result -> model().attribute("MESSAGE", PasswordController.MESSAGE));
    }

}