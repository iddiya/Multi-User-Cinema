package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * The jpa repository for {@link User}.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Return if a {@link User} already exists with {@link User#getUsername()} equal to the provided argument.
     *
     * @param username the username
     * @return true if a {@link User} already exists with the provided username, else false
     */
    boolean existsByUsername(String username);

    /**
     * Return if a {@link User} already exists with {@link User#getEmail()} equal to the provided argument.
     *
     * @param email the email
     * @return true if a {@link User} already exists with the provided email, else false
     */
    boolean existsByEmail(String email);

    /**
     * Returns true if there is a {@link User} that exists where {@link User#getUsername()} or
     * {@link User#getEmail()} equals the provided String.
     *
     * @param s the String to match to username or email
     * @return true if there is a User that matches the predicate
     */
    @Query("SELECT CASE WHEN count(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = ?1 OR u.email = ?1")
    boolean existsByUsernameOrEmail(String s);

    /**
     * Find optional {@link User#getId()} for the {@link User} with either username or email equal to the
     * provided argument.
     *
     * @param s the argument
     * @return the optional id
     */
    @Query("SELECT u.id FROM User u WHERE u.email = ?1 OR u.username = ?1")
    Optional<Long> findIdByUsernameOrEmail(String s);

    /**
     * Find optional {@link User} by {@link User#getUsername()}.
     *
     * @param username the username
     * @return the optional User
     */
    Optional<User> findByUsername(String username);

    /**
     * Find optional {@link User} by {@link User#getEmail()}.
     *
     * @param email the email
     * @return the optional User
     */
    Optional<User> findByEmail(String email);

    /**
     * Find optional {@link User#getId()} for the {@link User} with {@link User#getUsername()} to the
     * provided argument.
     *
     * @param username the username
     * @return the optional id
     */
    @Query("SELECT u.id FROM User u WHERE u.username = ?1")
    Optional<Long> findIdByUsername(String username);

    /**
     * Find optional {@link User#getId()} for the {@link User} with {@link User#getEmail()} equal to the
     * provided argument.
     *
     * @param email the email
     * @return the optional id
     */
    @Query("SELECT u.id FROM User u WHERE u.email = ?1")
    Optional<Long> findIdByEmail(String email);


    /**
     * Find optional {@link User} for the User with either {@link User#getEmail()} or {@link User#getUsername()}
     * equal to the provided argument.
     *
     * @param s the argument
     * @return the optional User
     */
    @Query("SELECT u FROM User u WHERE u.username = ?1 OR u.email = ?1")
    Optional<User> findByUsernameOrEmail(String s);

    /**
     * Find all {@link User} by {@link User#getIsAccountLocked()} equal to the provided argument.
     *
     * @param isAccountLocked the boolean value
     * @return the list of User
     */
    List<User> findAllByIsAccountLocked(boolean isAccountLocked);

    /**
     * Find all {@link User} by {@link User#getIsAccountEnabled()} equal to the provided argument.
     *
     * @param isAccountEnabled the boolean value
     * @return the list of User
     */
    List<User> findAllByIsAccountEnabled(boolean isAccountEnabled);

    /**
     * Find all {@link User#getIsAccountExpired()} equal to the provided argument.
     *
     * @param isAccountExpired the boolean value
     * @return the list of User
     */
    List<User> findAllByIsAccountExpired(boolean isAccountExpired);

    /**
     * Find all {@link User} by {@link User#getIsCredentialsExpired()} equal to the provided argument.
     *
     * @param isCredentialsExpired the boolean value
     * @return the list of User
     */
    List<User> findAllByIsCredentialsExpired(boolean isCredentialsExpired);

    /**
     * Find all {@link User} by {@link User#getCreationDateTime()} before or equal to the provided argument.
     *
     * @param localDateTime the local date time
     * @return the list of User
     */
    List<User> findAllByCreationDateTimeBefore(LocalDateTime localDateTime);

    /**
     * Find all {@link User} by {@link User#getCreationDateTime()} after or equal to the provided argument.
     *
     * @param localDateTime the local date time
     * @return the list of User
     */
    List<User> findAllByCreationDateTimeAfter(LocalDateTime localDateTime);

    /**
     * Find all {@link User} by {@link User#getLastActivityDateTime()} before or equal to the provided argument.
     *
     * @param localDateTime the local date time
     * @return the list of User
     */
    List<User> findAllByLastActivityDateTimeBefore(LocalDateTime localDateTime);

    /**
     * Find all {@link User} by {@link User#getLastActivityDateTime()} after or equal to the provided argument.
     *
     * @param localDateTime the local date time
     * @return the list of User
     */
    List<User> findAllByLastActivityDateTimeAfter(LocalDateTime localDateTime);

}
