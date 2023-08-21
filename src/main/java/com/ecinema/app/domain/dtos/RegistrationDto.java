package com.ecinema.app.domain.dtos;

import com.ecinema.app.domain.contracts.AbstractDto;
import com.ecinema.app.domain.contracts.IRegistration;
import com.ecinema.app.domain.enums.UserAuthority;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.userdetails.memory.UserAttribute;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

/**
 * The type Registration dto.
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RegistrationDto extends AbstractDto implements IRegistration {
    private String token;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String securityQuestion1;
    private String securityQuestion2;
    private String securityAnswer1;
    private String securityAnswer2;
    private Boolean isSecurityAnswer1Encoded;
    private Boolean isSecurityAnswer2Encoded;
    private String password;
    private String confirmPassword;
    private Boolean isPasswordEncoded;
    private LocalDate birthDate;
    private LocalDateTime creationDateTime;
    private Set<UserAuthority> authorities =
            EnumSet.noneOf(UserAuthority.class);
}
