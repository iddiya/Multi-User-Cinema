package com.ecinema.app.domain.entities;

import com.ecinema.app.domain.enums.UserAuthority;
import lombok.*;

import javax.persistence.*;

/**
 * {@inheritDoc}
 * This class defines a set of fields and mappings for the permissions granted by a user role.
 * Each class extending this has a direct one-to-one relationship with a value defined in {@link UserAuthority}.
 * The relationship between this and {@link User} is required and many-to-one.
 */
@Getter
@Setter
@Entity
@ToString
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractUserAuthority extends AbstractEntity {

    @JoinColumn
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @Column
    @Enumerated(EnumType.STRING)
    @Setter(AccessLevel.NONE)
    private UserAuthority userAuthority;

    @Column
    private Boolean isAuthorityValid;

    /**
     * Instantiates a new User role def.
     */
    public AbstractUserAuthority() {
        userAuthority = defineUserRole();
    }

    /**
     * The value of {@link #userAuthority} must be immutable, so it is defined once by this abstract method.
     * Its setter has been excluded to ensure that the value of userAuthority is final in this class's database
     * representation
     *
     * @return the final value of userAuthority.
     */
    protected abstract UserAuthority defineUserRole();

}
