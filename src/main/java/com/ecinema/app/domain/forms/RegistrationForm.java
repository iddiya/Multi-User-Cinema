package com.ecinema.app.domain.forms;

import com.ecinema.app.domain.contracts.IRegistration;
import com.ecinema.app.domain.enums.UserAuthority;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.util.EnumSet;
import java.util.Set;

@Data
public class RegistrationForm implements IRegistration, Serializable {
    private Boolean isPasswordEncoded = false;
    private Boolean isSecurityAnswer1Encoded = false;
    private Boolean isSecurityAnswer2Encoded = false;
    private String username = "";
    private String email = "";
    private String password = "";
    private String confirmPassword = "";
    private String firstName = "";
    private String lastName = "";
    private String securityQuestion1 = "";
    private String securityAnswer1 = "";
    private String securityQuestion2 = "";
    private String securityAnswer2 = "";
    private Set<UserAuthority> authorities = EnumSet.noneOf(UserAuthority.class);
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate = LocalDate.of(2000, Month.JANUARY, 1);
}
