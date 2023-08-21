package com.ecinema.app.controllers;

import com.ecinema.app.configs.InitializationConfig;
import com.ecinema.app.domain.entities.User;
import com.ecinema.app.repositories.UserRepository;
import com.ecinema.app.services.EncoderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class LoginControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncoderService encoderService;

    @MockBean
    private InitializationConfig config;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @Test
    void loginAvailable()
            throws Exception {
        mockMvc.perform(get("/login"))
               .andExpect(status().isOk());
    }

    @Test
    void successLogin()
            throws Exception {
        User user = new User();
        user.setEmail("User123");
        user.setPassword(encoderService.encode("password123?!"));
        user.setIsAccountEnabled(true);
        user.setIsAccountExpired(false);
        user.setIsAccountLocked(false);
        user.setIsCredentialsExpired(false);
        userRepository.save(user);
        mockMvc.perform(post("/perform-login")
                                .param("username", "User123")
                                .param("password", "password123?!"))
               .andExpect(redirectedUrl("/login-success"));
    }

    @Test
    void failLogin()
            throws Exception {
        mockMvc.perform(post("/perform-login")
                                .param("username", "dummy")
                                .param("password", "dummy"))
               .andExpect(redirectedUrl("/login-error"));
    }

    @Test
    @WithMockUser
    void loggedInUserCannotAccessLoginPage()
            throws Exception {
        mockMvc.perform(get("/login"))
               .andExpect(redirectedUrlPattern("/index**"));
    }

}