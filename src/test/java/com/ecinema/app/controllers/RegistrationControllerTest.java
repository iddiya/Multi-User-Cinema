package com.ecinema.app.controllers;

import com.ecinema.app.configs.InitializationConfig;
import com.ecinema.app.domain.enums.SecurityQuestions;
import com.ecinema.app.domain.forms.RegistrationForm;
import com.ecinema.app.services.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RegistrationControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private InitializationConfig config;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @Test
    void showSubmitRegistrationPage()
            throws Exception {
        mockMvc.perform(get("/submit-customer-registration"))
                .andExpect(status().isOk())
                .andExpect(result -> model().attribute(
                        "registrationForm", new RegistrationForm()))
                .andExpect(result -> model().attribute(
                        "securityQuestions", SecurityQuestions.getList()));
    }

    @Test
    void submitRegistration()
            throws Exception {
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setEmail("test123@gmail.com");
        registrationForm.setUsername("TestUser123");
        registrationForm.setFirstName("First");
        registrationForm.setLastName("Last");
        registrationForm.setPassword("password123?!");
        registrationForm.setConfirmPassword("password123?!");
        registrationForm.setSecurityQuestion1(SecurityQuestions.SQ1);
        registrationForm.setSecurityAnswer1("Answer 1");
        registrationForm.setSecurityQuestion2(SecurityQuestions.SQ2);
        registrationForm.setSecurityAnswer2("Answer 2");
        doNothing().when(registrationService).submitRegistrationForm(any(RegistrationForm.class));
        mockMvc.perform(get("/submit-customer-registration")
                                .flashAttr("registrationForm", registrationForm))
                .andExpect(status().isOk());
    }

}