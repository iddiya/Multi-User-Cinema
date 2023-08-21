package com.ecinema.app.controllers;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.configs.InitializationConfig;
import com.ecinema.app.domain.contracts.IProfile;
import com.ecinema.app.domain.dtos.UserDto;
import com.ecinema.app.domain.forms.UserProfileForm;
import com.ecinema.app.validators.UserProfileValidator;
import com.ecinema.app.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserProfileControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserProfileValidator userProfileValidator;

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
    @WithMockUser(username = "user")
    void showUserProfilePage()
            throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@gmail.com");
        userDto.setUsername("username");
        userDto.setFirstName("name");
        userDto.setLastName("name");
        userDto.setBirthDate(LocalDate.now());
        userDto.setCreationDateTime(LocalDateTime.now());
        userDto.setLastActivityDateTime(LocalDateTime.now());
        given(securityContext.findIdOfLoggedInUser()).willReturn(1L);
        given(userService.findById(1L)).willReturn(userDto);
        mockMvc.perform(get("/user-profile"))
                .andExpect(status().isOk())
                .andExpect(result -> model().attribute("user", userDto));
    }

    @Test
    @WithAnonymousUser
    void failToShowUserProfilePage()
            throws Exception {
        mockMvc.perform(get("/user-profile"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user")
    void showEditUserProfilePage()
            throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFirstName("First");
        userDto.setLastName("Last");
        userDto.setBirthDate(LocalDate.of(2000, Month.JANUARY, 1));
        given(securityContext.findIdOfLoggedInUser()).willReturn(1L);
        given(userService.findById(1L)).willReturn(userDto);
        mockMvc.perform(get("/edit-user-profile"))
                .andExpect(status().isOk())
                .andExpect(result -> model().attributeExists("profileForm"))
                .andExpect(result -> model().attribute("profileForm", hasProperty(
                        "userId", is(1L))))
                .andExpect(result -> model().attribute("profileForm", hasProperty(
                        "firstName", is("First"))))
                .andExpect(result -> model().attribute("profileForm", hasProperty(
                        "lastName", is("Last"))))
                .andExpect(result -> model().attribute("profileForm", hasProperty(
                        "birthDate", is(LocalDate.of(
                                2000, Month.JANUARY, 1)))));
    }

    @Test
    @WithAnonymousUser
    void failToShowEditUserProfilePage()
            throws Exception {
        mockMvc.perform(get("/edit-user-profile"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user")
    void postEditUserProfile()
            throws Exception {
        UserDto userDto = new UserDto();
        given(userService.findById(1L)).willReturn(userDto);
        UserProfileForm profileForm = new UserProfileForm();
        profileForm.setFirstName("First");
        profileForm.setLastName("Last");
        profileForm.setBirthDate(LocalDate.of(2000, Month.JANUARY, 1));
        doNothing().when(userProfileValidator).validate(any(IProfile.class), anyCollection());
        given(securityContext.findIdOfLoggedInUser()).willReturn(1L);
        given(userService.convertToDto(1L)).willReturn(userDto);
        mockMvc.perform(post("/edit-user-profile")
                                .flashAttr("profileForm", profileForm))
                .andExpect(redirectedUrlPattern("/user-profile**"))
                .andExpect(result -> model().attributeExists("success"));
    }

}