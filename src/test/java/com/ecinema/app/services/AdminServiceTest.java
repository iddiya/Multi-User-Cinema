package com.ecinema.app.services;

import com.ecinema.app.domain.dtos.AdminDto;
import com.ecinema.app.domain.entities.*;
import com.ecinema.app.domain.enums.UserAuthority;
import com.ecinema.app.domain.forms.AdminChangeUserPasswordForm;
import com.ecinema.app.validators.PasswordValidator;
import com.ecinema.app.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    private AdminService adminService;
    private EncoderService encoderService;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private UserRepository userRepository;
    
    @BeforeEach
    void setUp() {
        PasswordValidator passwordValidator = new PasswordValidator();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        encoderService = new EncoderService(passwordEncoder);
        adminService = new AdminService(adminRepository, userRepository,
                                        passwordValidator, encoderService);
    }

    @Test
    void deleteAdminAndCascade() {
        // given
        User user = new User();
        user.setId(1L);
        userRepository.save(user);
        Admin admin = new Admin();
        admin.setId(2L);
        admin.setUser(user);
        user.getUserAuthorities().put(UserAuthority.ADMIN, admin);
        adminRepository.save(admin);
        assertNotNull(admin.getUser());
        assertNotNull(user.getUserAuthorities().get(UserAuthority.ADMIN));
        // when
        adminService.delete(admin);
        // then
        assertNull(admin.getUser());
        assertNull(user.getUserAuthorities().get(UserAuthority.ADMIN));
    }

    @Test
    void adminDto() {
        // given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");
        user.setUsername("test");
        Admin admin = new Admin();
        admin.setId(1L);
        user.getUserAuthorities().put(UserAuthority.ADMIN, admin);
        admin.setUser(user);
        adminRepository.save(admin);
        given(adminRepository.findById(1L))
                .willReturn(Optional.of(admin));
        // when
        AdminDto adminDto = adminService.convertToDto(1L);
        // then
        assertEquals(admin.getId(), adminDto.getId());
        assertEquals(user.getId(), adminDto.getUserId());
        assertEquals(user.getEmail(), adminDto.getEmail());
        assertEquals(user.getUsername(), adminDto.getUsername());
    }

    @Test
    void adminChangeUserPassword() {
        // given
        User user = new User();
        given(userRepository.findByUsernameOrEmail("user")).willReturn(Optional.of(user));
        AdminChangeUserPasswordForm form = new AdminChangeUserPasswordForm();
        form.setEmailOrUsername("user");
        form.setPassword("password123?!");
        form.setConfirmPassword("password123?!");
        // when
        adminService.changeUserPassword(form);
        // then
        assertTrue(encoderService.matches("password123?!", user.getPassword()));
    }

}