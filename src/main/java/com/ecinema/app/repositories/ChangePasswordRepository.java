package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.ChangePassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The jpa repository for {@link ChangePassword}.
 */
@Repository
public interface ChangePasswordRepository extends JpaRepository<ChangePassword, Long> {

    /**
     * Delete by user id.
     *
     * @param userId the user id
     */
    void deleteAllByUserId(Long userId);

    /**
     * Exists by token boolean.
     *
     * @param token the token
     * @return the boolean
     */
    boolean existsByToken(String token);

    /**
     * Find by token optional.
     *
     * @param token the token
     * @return the optional
     */
    Optional<ChangePassword> findByToken(String token);

    /**
     * Find all by user id list.
     *
     * @param userId the user id
     * @return the list
     */
    List<ChangePassword> findAllByUserId(Long userId);

}
