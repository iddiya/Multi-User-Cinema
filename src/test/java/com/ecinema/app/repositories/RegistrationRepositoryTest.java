package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.Registration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RegistrationRepositoryTest {

    @Autowired
    private RegistrationRepository registrationRepository;

    @AfterEach
    void tearDown() {
        registrationRepository.deleteAll();
    }

    @Test
    void findByToken() {
        // given
        String token = UUID.randomUUID().toString();
        Registration registration = new Registration();
        registration.setToken(token);
        registrationRepository.save(registration);
        // when
        Optional<Registration> registrationRequestOptional =
                registrationRepository.findByToken(token);
        // then
        assertTrue(registrationRequestOptional.isPresent() &&
                registrationRequestOptional.get().getToken().equals(token));
    }

    @Test
    void findAllByEmail() {
        // given
        String email = "test@gmail.com";
        for (int i = 0; i < 10; i++) {
            Registration registration = new Registration();
            if (i % 2 == 0) {
                registration.setEmail(email);
            } else {
                registration.setEmail("NULL");
            }
            registrationRepository.save(registration);
        }
        // when
        List<Registration> registrations =
                registrationRepository.findAllByEmail(email);
        // then
        assertEquals(5, registrations.size());
    }

    @Test
    void deleteAllByEmail() {
        // given
        String email = "test@gmail.com";
        for (int i = 0; i < 10; i++) {
            Registration registration = new Registration();
            if (i % 2 == 0) {
                registration.setEmail(email);
            } else {
                registration.setEmail("NULL");
            }
            registrationRepository.save(registration);
        }
        // when
        registrationRepository.deleteAll();
        // then
        assertTrue(registrationRepository.findAll().isEmpty());
    }

    @Test
    void deleteAllByCreationDateTimeBefore() {
        // given
        LocalDateTime time1 = LocalDateTime.of(2022, Month.APRIL, 1, 1, 1);
        LocalDateTime time2 = LocalDateTime.of(2022, Month.APRIL, 20, 20, 20);
        for (int i = 0; i < 10; i++) {
            Registration registration = new Registration();
            if (i % 2 == 0) {
                registration.setCreationDateTime(time1);
            } else {
                registration.setCreationDateTime(time2);
            }
            registrationRepository.save(registration);
        }
        // when
        LocalDateTime time3 = LocalDateTime.of(2022, Month.APRIL, 10, 10, 10);
        registrationRepository.deleteAllByCreationDateTimeBefore(time3);
        // then
        assertEquals(5, registrationRepository.findAll().size());
    }

}