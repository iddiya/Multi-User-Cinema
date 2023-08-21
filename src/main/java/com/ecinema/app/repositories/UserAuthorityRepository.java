package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.AbstractUserAuthority;
import com.ecinema.app.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

/**
 * The interface User role def repository.
 *
 * @param <T> the type parameter
 */
@NoRepositoryBean
public interface UserAuthorityRepository<T extends AbstractUserAuthority> extends JpaRepository<T, Long> {

    /**
     * Find by user optional.
     *
     * @param user the user
     * @return the optional
     */
    @Query("SELECT u FROM #{#entityName} u WHERE u.user = ?1")
    Optional<T> findByUser(User user);

    /**
     * Find by user with id optional.
     *
     * @param userId the user id
     * @return the optional
     */
    @Query("SELECT u FROM #{#entityName} u WHERE u.user.id = ?1")
    Optional<T> findByUserWithId(Long userId);

    /**
     * Find id by user with id optional.
     *
     * @param userId the user id
     * @return the optional
     */
    @Query("SELECT u.id FROM #{#entityName} u WHERE u.user.id = ?1")
    Optional<Long> findIdByUserWithId(Long userId);

    /**
     * Returns if a {@link AbstractUserAuthority} exists where {@link User#getId()} from
     * {@link AbstractUserAuthority#getUser()} equals the provided Long user id argument.
     *
     * @param userId the user id
     * @return if a User Authority exists matching the predicate
     */
    @Query("SELECT CASE WHEN count(u) > 0 THEN true ELSE false END " +
            "FROM #{#entityName} u WHERE u.user.id = ?1")
    boolean existsByUserWithId(Long userId);

}
