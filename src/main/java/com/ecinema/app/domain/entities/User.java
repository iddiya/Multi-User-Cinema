package com.ecinema.app.domain.entities;

import com.ecinema.app.domain.enums.UserAuthority;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * {@inheritDoc}
 * The User class contains the fields relevant for Spring Security and also acts as the "bucket"
 * for instances of {@link AbstractUserAuthority} with one-to-one relationships with User.
 */
@Getter
@Setter
@Entity
@ToString
public class User extends AbstractEntity implements UserDetails {

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private LocalDate birthDate;

    @Column
    private String securityQuestion1;

    @Column
    private String securityAnswer1;

    @Column
    private String securityQuestion2;

    @Column
    private String securityAnswer2;

    @Column
    private LocalDateTime creationDateTime;

    @Column
    private LocalDateTime lastActivityDateTime;

    @Column
    private Boolean isAccountEnabled;

    @Column
    private Boolean isAccountLocked;

    @Column
    private Boolean isAccountExpired;

    @Column
    private Boolean isCredentialsExpired;

    @MapKey(name = "userAuthority")
    @MapKeyEnumerated(EnumType.ORDINAL)
    @OneToMany(targetEntity = AbstractUserAuthority.class, mappedBy = "user",
            cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Map<UserAuthority, AbstractUserAuthority> userAuthorities =
            new EnumMap<>(UserAuthority.class);

    @Override
    public Set<UserAuthority> getAuthorities() {
        return new HashSet<>(userAuthorities.keySet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return !isAccountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isAccountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !isCredentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return isAccountEnabled;
    }

}