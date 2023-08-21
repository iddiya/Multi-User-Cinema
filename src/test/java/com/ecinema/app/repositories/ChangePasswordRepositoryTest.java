package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.ChangePassword;
import com.ecinema.app.domain.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ChangePasswordRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChangePasswordRepository changePasswordRepository;

    @Test
    void deleteAllByUserId() {
        // given
        User user = userRepository.save(new User());
        ChangePassword changePassword1 = new ChangePassword();
        changePassword1.setUserId(user.getId());
        changePasswordRepository.save(changePassword1);
        ChangePassword changePassword2 = new ChangePassword();
        changePassword2.setUserId(user.getId());
        changePasswordRepository.save(changePassword2);
        assertEquals(2, changePasswordRepository.findAll().size());
        // when
        changePasswordRepository.deleteAllByUserId(user.getId());
        // then
        assertTrue(changePasswordRepository.findAll().isEmpty());
    }

    @Test
    void findByToken() {
        // given
        ChangePassword changePassword = new ChangePassword();
        changePassword.setToken("123");
        changePasswordRepository.save(changePassword);
        // when
        Optional<ChangePassword> changePasswordOptional = changePasswordRepository.findByToken("123");
        // then
        assertTrue(changePasswordOptional.isPresent());
        assertEquals(changePassword, changePasswordOptional.get());
    }

}