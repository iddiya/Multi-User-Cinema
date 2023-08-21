package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.User;
import com.ecinema.app.util.UtilMethods;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private final Random rand = new Random();

    @Test
    void existsByEmail() {
        // given
        User user = new User();
        user.setEmail("test@gmail.com");
        userRepository.save(user);
        // when
        boolean expected = userRepository.existsByEmail("test@gmail.com");
        // then
        assertTrue(expected);
    }

    @Test
    void findByEmail() {
        // given
        User user = new User();
        user.setEmail("test@gmail.com");
        userRepository.save(user);
        // when
        Optional<User> userOptional = userRepository.findByEmail("test@gmail.com");
        // then
        assertTrue(userOptional.isPresent() && userOptional.get().equals(user));
    }

    @Test
    void findIdByEmail() {
        // given
        User user = new User();
        user.setEmail("test@gmail.com");
        userRepository.save(user);
        // when
        Optional<Long> idOptional = userRepository.findIdByEmail("test@gmail.com");
        Optional<User> userOptional = userRepository.findByEmail("test@gmail.com");
        // then
        assertTrue(idOptional.isPresent());
        assertTrue(userOptional.isPresent());
        assertEquals(userOptional.get().getId(), idOptional.get());
    }

    @Test
    void findIdByUsername() {
        // given
        User user = new User();
        user.setUsername("test");
        userRepository.save(user);
        // when
        Optional<Long> idOptional = userRepository.findIdByUsername("test");
        Optional<User> userOptional = userRepository.findByUsername("test");
        // then
        assertTrue(idOptional.isPresent());
        assertTrue(userOptional.isPresent());
        assertEquals(userOptional.get().getId(), idOptional.get());
    }

    @Test
    void findByUsernameOrEmail() {
        // given
        User user = new User();
        user.setUsername("test123");
        user.setEmail("test123@gmail.com");
        userRepository.save(user);
        // when
        Optional<User> userOptional1 = userRepository.findByUsernameOrEmail("test123");
        Optional<User> userOptional2 = userRepository.findByUsernameOrEmail("test123@gmail.com");
        // then
        assertTrue(userOptional1.isPresent());
        assertTrue(userOptional2.isPresent());
        assertEquals(user, userOptional1.get());
        assertEquals(user, userOptional2.get());
    }

    @Test
    void failFindIdByUsername() {
        // given
        User user = new User();
        user.setUsername("test");
        userRepository.save(user);
        // when
        Optional<Long> idOptional = userRepository.findIdByUsername("false");
        Optional<User> userOptional = userRepository.findByUsername("false");
        // then
        assertTrue(idOptional.isEmpty());
        assertTrue(userOptional.isEmpty());
    }

    @Test
    void findAllByIsAccountLocked() {
        // given
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            User user = new User();
            user.setIsAccountLocked(rand.nextBoolean());
            userRepository.save(user);
            users.add(i, user);
        }
        // when
        List<User> unlockedUsers = users
                .stream().filter(User::isAccountNonLocked)
                .collect(Collectors.toList());
        // then
        assertEquals(unlockedUsers,
                     userRepository.findAllByIsAccountLocked(false));
    }

    @Test
    void findAllByCreationDateTimeBefore() {
        // given
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            User user = new User();
            user.setCreationDateTime(UtilMethods.randomDateTime());
            users.add(user);
            userRepository.save(user);
        }
        LocalDateTime randomLDT = UtilMethods.randomDateTime();
        // when
        List<User> usersBefore = users.stream().filter(
                user -> user.getCreationDateTime().isBefore(randomLDT))
                                      .collect(Collectors.toList());
        // then
        assertEquals(usersBefore,
                     userRepository.findAllByCreationDateTimeBefore(randomLDT));
    }

    @Test
    void findAllByCreationDateTimeAfter() {
        // given
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            User user = new User();
            user.setCreationDateTime(UtilMethods.randomDateTime());
            users.add(user);
            userRepository.save(user);
        }
        LocalDateTime randomLDT = UtilMethods.randomDateTime();
        // when
        List<User> usersBefore = users.stream().filter(
                user -> user.getCreationDateTime().isAfter(randomLDT))
                                      .collect(Collectors.toList());
        // then
        assertEquals(usersBefore,
                     userRepository.findAllByCreationDateTimeAfter(randomLDT));
    }

    @Test
    void findAllByLastActivityDateTimeBefore() {
        // given
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            User user = new User();
            user.setLastActivityDateTime(UtilMethods.randomDateTime());
            users.add(user);
            userRepository.save(user);
        }
        LocalDateTime randomLDT = UtilMethods.randomDateTime();
        // when
        List<User> usersBefore = users.stream().filter(
                user -> user.getLastActivityDateTime().isBefore(randomLDT))
                                      .collect(Collectors.toList());
        // then
        assertEquals(usersBefore,
                     userRepository.findAllByLastActivityDateTimeBefore(randomLDT));

    }

    @Test
    void findAllByLastActivityDateTimeAfter() {
        // given
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            User user = new User();
            user.setLastActivityDateTime(UtilMethods.randomDateTime());
            users.add(user);
            userRepository.save(user);
        }
        LocalDateTime randomLDT = UtilMethods.randomDateTime();
        // when
        List<User> usersBefore = users.stream().filter(
                user -> user.getLastActivityDateTime().isAfter(randomLDT))
                                      .collect(Collectors.toList());
        // then
        assertEquals(usersBefore,
                     userRepository.findAllByLastActivityDateTimeAfter(randomLDT));
    }

    @Test
    void findIdByUsernameOrEmail() {
        // given
        User user = new User();
        user.setUsername("username");
        user.setEmail("username@test.com");
        User savedUser = userRepository.save(user);
        // when
        Optional<Long> id2 = userRepository.findIdByUsernameOrEmail("username@test.com");
        Optional<Long> id1 = userRepository.findIdByUsernameOrEmail("username");
        Optional<Long> id3 = userRepository.findIdByUsernameOrEmail("FAIL");
        // then
        assertTrue(id1.isPresent());
        assertTrue(id2.isPresent());
        assertTrue(id3.isEmpty());
        assertEquals(savedUser.getId(), id1.get());
        assertEquals(savedUser.getId(), id2.get());
    }

}