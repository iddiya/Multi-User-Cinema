package com.ecinema.app.domain.entities;

import com.ecinema.app.domain.contracts.IRegistration;
import com.ecinema.app.domain.enums.UserAuthority;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

/**
 * The type Registration.
 */
@Getter
@Setter
@Entity
@ToString
public class Registration extends AbstractEntity implements IRegistration {

    /**
     * Instantiates a new Registration.
     */
    public Registration() {
        setIsPasswordEncoded(false);
        setIsSecurityAnswer1Encoded(false);
        setIsSecurityAnswer2Encoded(false);
    }

    @Column
    private LocalDateTime creationDateTime;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<UserAuthority> authorities =
            EnumSet.noneOf(UserAuthority.class);

    @Column
    private String token;

    @Column
    private String username;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String confirmPassword;

    @Column
    private Boolean isPasswordEncoded;

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
    private Boolean isSecurityAnswer1Encoded;

    @Column
    private String securityQuestion2;

    @Column
    private String securityAnswer2;

    @Column
    private Boolean isSecurityAnswer2Encoded;

}
