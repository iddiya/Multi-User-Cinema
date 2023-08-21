package com.ecinema.app.services;

import com.ecinema.app.configs.InitializationConfig;
import com.ecinema.app.domain.dtos.UserDto;
import com.ecinema.app.domain.entities.Customer;
import com.ecinema.app.domain.entities.Registration;
import com.ecinema.app.domain.entities.User;
import com.ecinema.app.domain.enums.SecurityQuestions;
import com.ecinema.app.domain.enums.UserAuthority;
import com.ecinema.app.domain.forms.RegistrationForm;
import com.ecinema.app.validators.PasswordValidator;
import com.ecinema.app.validators.RegistrationValidator;
import com.ecinema.app.validators.UserProfileValidator;
import com.ecinema.app.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RegistrationServiceTest {

    private UserService userService;
    private CustomerService customerService;
    private RegistrationService registrationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private RegistrationRepository registrationRepository;
    @MockBean
    private EncoderService encoderService;
    @MockBean
    private PasswordValidator passwordValidator;
    @MockBean
    private UserProfileValidator userProfileValidator;
    @MockBean
    private RegistrationValidator registrationValidator;
    @MockBean
    private EmailService emailService;
    @MockBean
    private InitializationConfig config;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(
                customerRepository, null, null,
                null, null, null, null, null);
        userService = new UserService(
                userRepository, customerService, null, null,
                encoderService, userProfileValidator,
                registrationValidator);
        registrationService = new RegistrationService(
                registrationRepository, userService, emailService,
                encoderService, registrationValidator);
    }

    @Test
    void register() {
        // given
        doNothing().when(emailService).sendFromBusinessEmail(
                anyString(), anyString(), anyString());
        given(encoderService.encode(anyString())).willReturn("ENCODED123?!");
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setEmail("test@gmail.com");
        registrationForm.setUsername("TestUser123");
        registrationForm.setFirstName("First");
        registrationForm.setLastName("Last");
        registrationForm.setPassword("password123?!");
        registrationForm.setConfirmPassword("password123?!");
        registrationForm.setSecurityQuestion1(SecurityQuestions.SQ1);
        registrationForm.setSecurityAnswer1("Answer 1");
        registrationForm.setSecurityQuestion2(SecurityQuestions.SQ2);
        registrationForm.setSecurityAnswer2("Answer 2");
        registrationForm.setBirthDate(LocalDate.of(2000, Month.JANUARY, 1));
        registrationForm.getAuthorities().add(UserAuthority.CUSTOMER);
        // when
        registrationService.submitRegistrationForm(registrationForm);
        // then
        ArgumentCaptor<Registration> registrationArgumentCaptor = ArgumentCaptor.forClass(Registration.class);
        verify(registrationRepository).save(registrationArgumentCaptor.capture());
        Registration registration = registrationArgumentCaptor.getValue();
        assertTrue(registration.registrationEquals(registrationForm));
        // given
        given(registrationRepository.findByToken(registration.getToken()))
                .willReturn(Optional.of(registration));
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        // when
        UserDto userDto = registrationService.confirmRegistrationRequest(registration.getToken());
        // then
        assertNotNull(userDto);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User user = userArgumentCaptor.getValue();
        assertEquals("test@gmail.com", user.getEmail());
        assertEquals("TestUser123", user.getUsername());
        assertEquals("First", user.getFirstName());
        assertEquals("Last", user.getLastName());
        assertEquals("ENCODED123?!", user.getPassword());
        assertEquals("ENCODED123?!", user.getSecurityAnswer1());
        assertEquals("ENCODED123?!", user.getSecurityAnswer2());
        assertEquals(SecurityQuestions.SQ1, user.getSecurityQuestion1());
        assertEquals(SecurityQuestions.SQ2, user.getSecurityQuestion2());
        assertEquals(LocalDate.of(
                2000, Month.JANUARY, 1), user.getBirthDate());
        assertTrue(user.getUserAuthorities().containsKey(UserAuthority.CUSTOMER));
        assertInstanceOf(Customer.class, user.getUserAuthorities().get(UserAuthority.CUSTOMER));
    }

}