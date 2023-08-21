package com.ecinema.app.services;

import com.ecinema.app.domain.dtos.UserAuthorityDto;
import com.ecinema.app.domain.entities.User;
import com.ecinema.app.domain.entities.AbstractUserAuthority;
import com.ecinema.app.exceptions.NoEntityFoundException;
import com.ecinema.app.repositories.UserAuthorityRepository;
import com.ecinema.app.util.UtilMethods;
import lombok.ToString;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for entities extending {@link AbstractUserAuthority}.
 *
 * @param <E> the Entity type parameter
 * @param <R> the JpaRepository type parameter
 * @param <D> the {@link com.ecinema.app.domain.contracts.AbstractDto} type parameter
 */
@ToString
@Transactional
public abstract class UserAuthorityService<E extends AbstractUserAuthority, R extends UserAuthorityRepository<E>,
        D extends UserAuthorityDto> extends AbstractEntityService<E, R, D> {

    /**
     * Instantiates a new User authority service.
     *
     * @param repository the repository
     */
    public UserAuthorityService(R repository) {
        super(repository);
    }

    @Override
    protected void onDelete(E userAuthority) {
        User user = userAuthority.getUser();
        logger.debug("Before detach user authority from user: " + user);
        if (user != null) {
            user.getUserAuthorities().remove(userAuthority.getUserAuthority());
            userAuthority.setUser(null);
        }
        logger.debug("After detach user authority from user: " + user);
    }

    /**
     * Fill common user authority dto fields of {@link D}.
     *
     * @param userAuthority the user authority
     * @param dto           the dto
     */
    protected void fillCommonUserAuthorityDtoFields(E userAuthority, D dto) {
        dto.setId(userAuthority.getId());
        dto.setUserId(userAuthority.getUser().getId());
        dto.setEmail(userAuthority.getUser().getEmail());
        dto.setUsername(userAuthority.getUser().getUsername());
    }

    /**
     * Find optional {@link E} by {@link User#getId()} from {@link E#getUser()}.
     *
     * @param userId the user id
     * @return the optional E
     */
    public Optional<D> findByUserWithId(Long userId) {
        logger.debug(UtilMethods.getLoggingSubjectDelimiterLine());
        logger.debug("Find by user with id: " + userId);
        E authority = repository.findByUserWithId(userId).orElse(null);
        logger.debug("Found user: " + authority);
        if (authority == null) {
            return Optional.empty();
        }
        return Optional.of(convertToDto(authority));
    }

    /**
     * Find {@link E#getId()} of the {@link E} where {@link User#getId()} from {@link E#getUser()} equals the
     * provided Long user id argument.
     *
     * @param userId the user id
     * @return the User Authority id
     * @throws NoEntityFoundException thrown if no E matches the predicate
     */
    public Long findIdByUserWithId(Long userId)
            throws NoEntityFoundException {
        return repository.findIdByUserWithId(userId).orElseThrow(
                () -> new NoEntityFoundException("user authority id", "user id", userId));
    }

    /**
     * Return if a {@link E} exists where {@link User#getId()} from {@link E#getUser()} equals the provided Long
     * user id argument.
     *
     * @param userId the user id
     * @return true if a {@link E} matches the predicate
     */
    public boolean existsByUserWithId(Long userId) {
        return repository.existsByUserWithId(userId);
    }

}
