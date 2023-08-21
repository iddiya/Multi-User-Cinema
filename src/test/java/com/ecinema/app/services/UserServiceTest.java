package com.ecinema.app.services;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.domain.entities.*;
import com.ecinema.app.domain.enums.UserAuthority;
import com.ecinema.app.repositories.*;
import com.ecinema.app.domain.dtos.UserDto;
import com.ecinema.app.exceptions.ClashException;
import com.ecinema.app.validators.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;
    private AdminService adminService;
    private ReviewService reviewService;
    private TicketService ticketService;
    private EmailValidator emailValidator;
    private CustomerService customerService;
    private SecurityContext securityContext;
    private ModeratorService moderatorService;
    private ReviewVoteService reviewVoteService;
    private UsernameValidator usernameValidator;
    private PasswordValidator PasswordValidator;
    private PaymentCardService paymentCardService;
    private SeatBookingValidator seatBookingValidator;
    private UserProfileValidator userProfileValidator;
    private RegistrationValidator registrationValidator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ModeratorRepository moderatorRepository;
    @Mock
    private ReviewVoteRepository reviewVoteRepository;
    @Mock
    private PaymentCardRepository paymentCardRepository;
    @Mock
    private ScreeningSeatRepository screeningSeatRepository;

    @BeforeEach
    void setUp() {
        seatBookingValidator = new SeatBookingValidator();
        securityContext = new SecurityContext();
        emailValidator = new EmailValidator();
        usernameValidator = new UsernameValidator();
        PasswordValidator = new PasswordValidator();
        userProfileValidator = new UserProfileValidator();
        registrationValidator = new RegistrationValidator(emailValidator, userProfileValidator, usernameValidator,
                PasswordValidator);
        reviewVoteService = new ReviewVoteService(reviewVoteRepository, reviewRepository, customerRepository);
        reviewService = new ReviewService(reviewRepository, null, null, null, reviewVoteService);
        ticketService = new TicketService(ticketRepository, null, seatBookingValidator, customerRepository,
                paymentCardRepository, screeningSeatRepository);
        paymentCardService = new PaymentCardService(paymentCardRepository, null, null, null);
        adminService = new AdminService(adminRepository, userRepository, null, null);
        customerService = new CustomerService(customerRepository, screeningSeatRepository, null, reviewService,
                ticketService, paymentCardService, reviewVoteService, securityContext);
        moderatorService = new ModeratorService(moderatorRepository, customerRepository);
        userService = new UserService(userRepository, customerService, moderatorService, adminService, null,
                userProfileValidator, registrationValidator);
    }

    @Test
    void findByEmail() {
        // given
        String email = "test@gmail.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        // when
        UserDto userDto = userService.findByEmail(email);
        // then
        assertEquals(email, userDto.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void addUserRoleDefsToUser() {
        // given
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        // when
        userService.addUserAuthorityToUser(
                1L, UserAuthority.CUSTOMER, UserAuthority.ADMIN, UserAuthority.MODERATOR);
        // then
        assertTrue(user.getUserAuthorities().containsKey(UserAuthority.CUSTOMER));
        assertTrue(user.getUserAuthorities().get(UserAuthority.CUSTOMER) instanceof Customer);
        assertTrue(user.getUserAuthorities().containsKey(UserAuthority.ADMIN));
        assertTrue(user.getUserAuthorities().get(UserAuthority.ADMIN) instanceof Admin);
        assertTrue(user.getUserAuthorities().containsKey(UserAuthority.MODERATOR));
        assertTrue(user.getUserAuthorities().get(UserAuthority.MODERATOR) instanceof Moderator);
    }

    @Test
    void failToAddUserRoleDefToUser() {
        // given
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        userService.addUserAuthorityToUser(1L, UserAuthority.CUSTOMER);
        // then
        assertThrows(ClashException.class,
                     () -> userService.addUserAuthorityToUser(
                             1L, UserAuthority.CUSTOMER));
    }

    @Test
    void deleteUserAndCascade() {
        // given
        User user = new User();
        user.setId(1L);
        given(userRepository.findById(1L))
                .willReturn(Optional.of(user));
        userService.save(user);
        Admin admin = new Admin();
        admin.setId(2L);
        admin.setUser(user);
        user.getUserAuthorities().put(UserAuthority.ADMIN, admin);
        adminService.save(admin);
        Customer customer = new Customer();
        customer.setId(3L);
        customer.setUser(user);
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, customer);
        customerService.save(customer);
        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setId(4L);
        paymentCard.setCardOwner(customer);
        customer.getPaymentCards().add(paymentCard);
        paymentCardService.save(paymentCard);
        // then
        assertNotNull(user.getUserAuthorities().get(UserAuthority.ADMIN));
        assertNotNull(user.getUserAuthorities().get(UserAuthority.CUSTOMER));
        assertNotNull(admin.getUser());
        assertNotNull(customer.getUser());
        assertNotNull(paymentCard.getCardOwner());
        // when
        userService.delete(user.getId());
        // then
        assertNull(user.getUserAuthorities().get(UserAuthority.ADMIN));
        assertNull(user.getUserAuthorities().get(UserAuthority.CUSTOMER));
        assertNull(admin.getUser());
        assertNull(customer.getUser());
        assertNull(paymentCard.getCardOwner());
    }

    @Test
    void userDto() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");
        user.setUsername("test");
        user.setFirstName("John");
        user.setLastName("Lavender");
        user.getUserAuthorities().put(UserAuthority.CUSTOMER, null);
        userService.save(user);
        given(userRepository.findById(1L))
                .willReturn(Optional.of(user));
        // when
        UserDto userDto = userService.convertToDto(1L);
        // then
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getUsername(), userDto.getUsername());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertTrue(userDto.getUserAuthorities().contains(UserAuthority.CUSTOMER));
    }

}